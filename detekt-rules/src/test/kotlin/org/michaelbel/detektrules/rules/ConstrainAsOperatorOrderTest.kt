package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConstrainAsOperatorOrderTest {

    private val subject = ConstrainAsOperatorOrder(Config.empty)

    @Test
    fun `does not report when all operators are in correct order`() {
        val code = """
            val modifier = Modifier.constrainAs(productsRow) {
                width = Dimension.fillToConstraints
                height = Dimension.value(68.dp)
                start.linkTo(parent.start, 16.dp)
                top.linkTo(statusRow.bottom, 16.dp)
                end.linkTo(parent.end, 16.dp)
                bottom.linkTo(parent.bottom, 16.dp)
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when only some operators are present in correct order`() {
        val code = """
            val modifier = Modifier.constrainAs(productsRow) {
                start.linkTo(parent.start, 16.dp)
                top.linkTo(parent.top, 16.dp)
                end.linkTo(parent.end, 16.dp)
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report width and height only in correct order`() {
        val code = """
            val modifier = Modifier.constrainAs(productsRow) {
                width = Dimension.fillToConstraints
                height = Dimension.value(68.dp)
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports when start comes before height`() {
        val code = """
            val modifier = Modifier.constrainAs(productsRow) {
                width = Dimension.fillToConstraints
                start.linkTo(parent.start, 16.dp)
                height = Dimension.value(68.dp)
                top.linkTo(parent.top, 16.dp)
                end.linkTo(parent.end, 16.dp)
                bottom.linkTo(parent.bottom, 16.dp)
            }
        """.trimIndent()

        assertEquals(
            listOf(
                "Operators inside constrainAs must be ordered: width, height, start, top, end, bottom. " +
                    "Found: width, start, height, top, end, bottom."
            ),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports when bottom comes before top`() {
        val code = """
            val modifier = Modifier.constrainAs(productsRow) {
                start.linkTo(parent.start, 16.dp)
                bottom.linkTo(parent.bottom, 16.dp)
                top.linkTo(parent.top, 16.dp)
                end.linkTo(parent.end, 16.dp)
            }
        """.trimIndent()

        assertEquals(
            listOf(
                "Operators inside constrainAs must be ordered: width, height, start, top, end, bottom. " +
                    "Found: start, bottom, top, end."
            ),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports when end comes before start`() {
        val code = """
            val modifier = Modifier.constrainAs(productsRow) {
                end.linkTo(parent.end, 16.dp)
                start.linkTo(parent.start, 16.dp)
                top.linkTo(parent.top, 16.dp)
                bottom.linkTo(parent.bottom, 16.dp)
            }
        """.trimIndent()

        assertEquals(
            listOf(
                "Operators inside constrainAs must be ordered: width, height, start, top, end, bottom. " +
                    "Found: end, start, top, bottom."
            ),
            subject.lint(code).map { it.message }
        )
    }
}
