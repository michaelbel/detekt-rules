package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PaddingValuesSymmetryTest {

    private val subject = PaddingValuesSymmetry(Config.empty)

    @Test
    fun `reports all when all four arguments are equal`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            val contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
            val modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
        """.trimIndent()

        assertEquals(
            listOf(
                "Symmetric Compose padding arguments can be replaced with all = 8.dp.",
                "Symmetric Compose padding arguments can be replaced with all = 8.dp.",
            ),
            subject.lint(code).map { it.message },
        )
    }

    @Test
    fun `reports horizontal and vertical when all four arguments are pairwise symmetric`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            val contentPadding = PaddingValues(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 0.dp)
            val modifier = Modifier.padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 0.dp)
        """.trimIndent()

        assertEquals(
            listOf(
                "Symmetric Compose padding arguments can be replaced with horizontal = 8.dp and vertical = 0.dp.",
                "Symmetric Compose padding arguments can be replaced with horizontal = 8.dp and vertical = 0.dp.",
            ),
            subject.lint(code).map { it.message },
        )
    }

    @Test
    fun `reports when only horizontal pair is symmetric`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            val contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
            val modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        """.trimIndent()

        assertEquals(
            listOf(
                "Symmetric Compose padding arguments can be replaced with horizontal = 8.dp.",
                "Symmetric Compose padding arguments can be replaced with horizontal = 8.dp.",
            ),
            subject.lint(code).map { it.message },
        )
    }

    @Test
    fun `reports when only vertical pair is symmetric`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            val contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
            val modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
        """.trimIndent()

        assertEquals(
            listOf(
                "Symmetric Compose padding arguments can be replaced with vertical = 12.dp.",
                "Symmetric Compose padding arguments can be replaced with vertical = 12.dp.",
            ),
            subject.lint(code).map { it.message },
        )
    }

    @Test
    fun `does not report when values are not symmetric`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            val first = PaddingValues(start = 8.dp, top = 0.dp, end = 4.dp, bottom = 0.dp)
            val second = PaddingValues(start = 8.dp, end = 4.dp)
            val third = PaddingValues(top = 4.dp, bottom = 12.dp)
            val fourth = Modifier.padding(start = 8.dp, top = 0.dp, end = 4.dp, bottom = 0.dp)
            val fifth = Modifier.padding(start = 8.dp, end = 4.dp)
            val sixth = Modifier.padding(top = 4.dp, bottom = 12.dp)
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report non explicit Modifier receiver`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun example(someModifier: Modifier) {
                someModifier.padding(start = 8.dp, end = 8.dp)
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report custom padding apis without compose import`() {
        val code = """
            package test

            fun PaddingValues(start: Int, top: Int, end: Int, bottom: Int) = Unit
            fun padding(start: Int, top: Int, end: Int, bottom: Int) = Unit

            val contentPadding = PaddingValues(start = 8, top = 0, end = 8, bottom = 0)
            val modifier = padding(start = 8, top = 0, end = 8, bottom = 0)
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }
}
