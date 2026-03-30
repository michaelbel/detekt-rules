package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TextAlignInTextStyleTest {

    private val subject = TextAlignInTextStyle(Config.empty)

    @Test
    fun `reports when material3 textAlign is passed separately`() {
        val code = """
            package test

            import androidx.compose.material3.MaterialTheme
            import androidx.compose.material3.Text
            import androidx.compose.ui.text.style.TextAlign

            fun invalid() {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                    textAlign = TextAlign.Center,
                )
            }
        """.trimIndent()

        assertEquals(
            listOf("Move Text textAlign into the style argument instead of passing it separately."),
            subject.lint(code).map { it.message },
        )
    }

    @Test
    fun `reports when material textAlign is passed separately`() {
        val code = """
            package test

            import androidx.compose.material.Text
            import androidx.compose.ui.text.style.TextAlign

            fun invalid() {
                Text(
                    text = "Title",
                    textAlign = TextAlign.Center,
                )
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `does not report when textAlign is declared in style`() {
        val code = """
            package test

            import androidx.compose.material3.MaterialTheme
            import androidx.compose.material3.Text
            import androidx.compose.ui.text.style.TextAlign

            fun valid() {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report custom text without compose import`() {
        val code = """
            package test

            fun Text(text: String, textAlign: String) = Unit

            fun valid() {
                Text(
                    text = "Title",
                    textAlign = "center",
                )
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }
}
