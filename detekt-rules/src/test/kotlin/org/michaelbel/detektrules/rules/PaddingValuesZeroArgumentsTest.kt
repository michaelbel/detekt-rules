package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PaddingValuesZeroArgumentsTest {

    private val subject = PaddingValuesZeroArguments(Config.empty)

    @Test
    fun `reports PaddingValues with single zero positional argument`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val p = PaddingValues(0.dp)
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports PaddingValues with two zero positional arguments`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val p = PaddingValues(0.dp, 0.dp)
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports PaddingValues with four zero positional arguments`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val p = PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports PaddingValues with all named zero arguments`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val p1 = PaddingValues(all = 0.dp)
                val p2 = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                val p3 = PaddingValues(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
            }
        """.trimIndent()

        assertEquals(3, subject.lint(code).size)
    }

    @Test
    fun `does not report PaddingValues with non-zero arguments`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.ui.unit.dp

            fun valid() {
                val p1 = PaddingValues(8.dp)
                val p2 = PaddingValues(8.dp, 16.dp)
                val p3 = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                val p4 = PaddingValues(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `does not report PaddingValues with mixed zero and non-zero arguments`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.ui.unit.dp

            fun valid() {
                val p = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `does not report empty PaddingValues`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.ui.unit.dp

            fun valid() {
                val p = PaddingValues()
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `does not report custom PaddingValues without compose import`() {
        val code = """
            package test

            fun PaddingValues(a: Int) = Unit

            fun valid() {
                PaddingValues(0)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `reports PaddingValues with wildcard import`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.*
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val p = PaddingValues(0.dp, 0.dp)
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }
}
