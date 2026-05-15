package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ModifierWidthHeightCanBeSizeTest {

    private val subject = ModifierWidthHeightCanBeSize(Config.empty)

    @Test
    fun `reports width followed by height on Modifier`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.width
            import androidx.compose.foundation.layout.height
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val modifier = Modifier
                    .width(100.dp)
                    .height(32.dp)
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports height followed by width on Modifier`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.width
            import androidx.compose.foundation.layout.height
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val modifier = Modifier
                    .height(32.dp)
                    .width(100.dp)
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports correct suggestion message`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.width
            import androidx.compose.foundation.layout.height
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val modifier = Modifier.width(100.dp).height(32.dp)
            }
        """.trimIndent()

        val findings = subject.lint(code)
        assertEquals(1, findings.size)
        assertEquals(
            "Replace 'Modifier.width(100.dp).height(32.dp)' with 'Modifier.size(width = 100.dp, height = 32.dp)'.",
            findings.first().message
        )
    }

    @Test
    fun `reports when width-height chain is part of a longer modifier chain`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.width
            import androidx.compose.foundation.layout.height
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val modifier = Modifier
                    .width(100.dp)
                    .height(32.dp)
                    .padding(8.dp)
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `does not report when only width is used`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.width
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun valid() {
                val modifier = Modifier.width(100.dp)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `does not report when only height is used`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.height
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun valid() {
                val modifier = Modifier.height(32.dp)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `does not report when size is already used`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.size
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun valid() {
                val modifier = Modifier.size(width = 100.dp, height = 32.dp)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `does not report width height chain on non-Modifier receiver`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.width
            import androidx.compose.foundation.layout.height
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun valid(someModifier: Modifier) {
                val modifier = someModifier
                    .width(100.dp)
                    .height(32.dp)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `does not report without compose layout import`() {
        val code = """
            package test

            fun width(value: Any) = Unit
            fun height(value: Any) = Unit

            object Modifier {
                fun width(value: Any) = this
                fun height(value: Any) = this
            }

            fun valid() {
                Modifier.width(100).height(32)
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `reports with wildcard layout import`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.*
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            fun invalid() {
                val modifier = Modifier.width(100.dp).height(32.dp)
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }
}
