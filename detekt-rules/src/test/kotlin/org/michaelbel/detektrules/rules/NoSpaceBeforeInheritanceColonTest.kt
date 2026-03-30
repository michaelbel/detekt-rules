package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NoSpaceBeforeInheritanceColonTest {

    private val subject = NoSpaceBeforeInheritanceColon(Config.empty)

    @Test
    fun `reports inheritance and delegation with space before colon`() {
        val code = """
            package test

            interface Rule
            interface Delegate

            class Inheritance(config: String) : Rule
            class Delegation(delegate: Delegate) : Rule by delegate
            object Singleton : Rule
        """.trimIndent()

        assertEquals(
            listOf(
                "Remove whitespace before ':' in inheritance or delegation declarations.",
                "Remove whitespace before ':' in inheritance or delegation declarations.",
                "Remove whitespace before ':' in inheritance or delegation declarations.",
            ),
            subject.lint(code).map { it.message },
        )
    }

    @Test
    fun `does not report when there is no space before colon`() {
        val code = """
            package test

            interface Rule
            interface Delegate

            class Inheritance(config: String): Rule
            class Delegation(delegate: Delegate): Rule by delegate
            object Singleton: Rule
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report regular type declarations`() {
        val code = """
            package test

            class Example(
                val title: String,
            ) {
                fun map(value: Int): String = value.toString()
            }
        """.trimIndent()

        assertEquals(0, subject.lint(code).size)
    }
}
