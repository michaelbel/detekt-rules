package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ComposableFileOptInTest {

    private val subject = ComposableFileOptIn(Config.empty)

    @Test
    fun `reports function level opt in on composable`() {
        val code = """
            package test

            import androidx.compose.material3.ExperimentalMaterial3Api
            import androidx.compose.runtime.Composable
            import kotlin.OptIn

            @OptIn(ExperimentalMaterial3Api::class)
            @Composable
            fun Example() = Unit
        """.trimIndent()

        assertEquals(
            listOf("Move experimental annotations from @Composable declarations to @file:OptIn(...)."),
            subject.lint(code).map { it.message },
        )
    }

    @Test
    fun `reports direct experimental annotation on composable`() {
        val code = """
            package test

            import androidx.compose.material3.ExperimentalMaterial3Api
            import androidx.compose.runtime.Composable

            @ExperimentalMaterial3Api
            @Composable
            fun Example() = Unit
        """.trimIndent()

        assertEquals(
            listOf("Move experimental annotations from @Composable declarations to @file:OptIn(...)."),
            subject.lint(code).map { it.message },
        )
    }

    @Test
    fun `does not report file level opt in on composable`() {
        val code = """
            @file:OptIn(ExperimentalMaterial3Api::class)

            package test

            import androidx.compose.material3.ExperimentalMaterial3Api
            import androidx.compose.runtime.Composable
            import kotlin.OptIn

            @Composable
            fun Example() = Unit
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report opt in on non composable`() {
        val code = """
            package test

            import androidx.compose.material3.ExperimentalMaterial3Api
            import kotlin.OptIn

            @OptIn(ExperimentalMaterial3Api::class)
            fun Example() = Unit
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }

    @Test
    fun `does not report non experimental annotations on composable`() {
        val code = """
            package test

            import androidx.compose.runtime.Composable

            annotation class StablePreview

            @StablePreview
            @Composable
            fun Example() = Unit
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }
}
