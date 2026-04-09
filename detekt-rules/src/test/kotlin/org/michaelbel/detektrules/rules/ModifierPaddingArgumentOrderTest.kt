package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ModifierPaddingArgumentOrderTest {

    private val subject = ModifierPaddingArgumentOrder(Config.empty)

    @Test
    fun `does not report when arguments follow compose api order`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun valid() {
                val modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 16.dp)
                    .padding(start = 8.dp, bottom = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .padding(horizontal = 16.dp)

                val paddingValues = PaddingValues(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp)
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports when arguments are out of api order`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 16.dp)
                    .padding(bottom = 16.dp, start = 8.dp)
                    .padding(vertical = 20.dp, horizontal = 16.dp)

                val paddingValues = PaddingValues(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            }
        """.trimIndent()

        assertEquals(4, subject.lint(code).size)
    }

    @Test
    fun `does not report when padding is called on non explicit Modifier receiver`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun valid(someModifier: Modifier) {
                someModifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 16.dp)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `does not report other padding overloads`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun valid() {
                val modifier = Modifier
                    .padding(8.dp)
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .padding(all = 8.dp)

                val paddingValues = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `reports when 0dp padding arguments are redundant`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 0.dp)
                    .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 0.dp)

                val paddingValues = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                val paddingValues2 = PaddingValues(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 0.dp)
            }
        """.trimIndent()

        assertEquals(4, subject.lint(code).size)
    }

    @Test
    fun `does not report padding with all non-zero arguments`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun valid() {
                val modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp)

                val paddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `does not report when single named argument is 0dp without other args`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.PaddingValues
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun valid() {
                val modifier = Modifier.padding(horizontal = 8.dp)
                val paddingValues = PaddingValues(horizontal = 8.dp)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `does not report custom padding without compose import`() {
        val code = """
            package test

            fun PaddingValues(start: Int, end: Int, top: Int, bottom: Int) = Unit
            fun padding(start: Int, end: Int, top: Int, bottom: Int) = Unit

            fun valid() {
                padding(start = 1, end = 2, top = 3, bottom = 4)
                PaddingValues(start = 1, end = 2, top = 3, bottom = 4)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }
}
