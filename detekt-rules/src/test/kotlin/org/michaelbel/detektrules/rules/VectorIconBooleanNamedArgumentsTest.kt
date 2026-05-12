package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VectorIconBooleanNamedArgumentsTest {

    private val subject = VectorIconBooleanNamedArguments(Config.empty)

    @Test
    fun `reports unnamed boolean arguments in vector icon file`() {
        val code = """
            import androidx.compose.ui.graphics.vector.ImageVector
            val icon: ImageVector get() {
                return ImageVector.Builder().apply {
                    path {
                        arcTo(10.5F, 10.5F, 0F, true, true, 1.5F, 12F)
                    }
                }.build()
            }
        """.trimIndent()

        assertEquals(
            listOf(
                "Boolean argument 'true' must use a named parameter in vector icon files.",
                "Boolean argument 'true' must use a named parameter in vector icon files.",
            ),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `does not report named boolean arguments in vector icon file`() {
        val code = """
            import androidx.compose.ui.graphics.vector.ImageVector
            val icon: ImageVector get() {
                return ImageVector.Builder().apply {
                    path {
                        arcTo(10.5F, 10.5F, 0F,
                            isMoreThanHalf = true,
                            isPositiveArc = true,
                            x1 = 1.5F,
                            y1 = 12F
                        )
                    }
                }.build()
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report unnamed boolean arguments outside vector icon file`() {
        val code = """
            fun test() {
                doSomething(true, false)
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports only unnamed booleans when mixed with named in vector icon file`() {
        val code = """
            import androidx.compose.ui.graphics.vector.ImageVector
            val icon: ImageVector get() {
                return ImageVector.Builder().apply {
                    path {
                        arcTo(10.5F, 10.5F, 0F,
                            true,
                            isPositiveArc = true,
                            x1 = 1.5F,
                            y1 = 12F
                        )
                    }
                }.build()
            }
        """.trimIndent()

        assertEquals(
            listOf("Boolean argument 'true' must use a named parameter in vector icon files."),
            subject.lint(code).map { it.message }
        )
    }
}
