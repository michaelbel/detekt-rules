package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SealedClassCanBeInterfaceTest {

    private val subject = SealedClassCanBeInterface(Config.empty)

    @Test
    fun `reports sealed class with only data objects`() {
        val code = """
            sealed class Style {
                data object Card : Style()
                data object Screen : Style()
            }
        """.trimIndent()
        val expected = "Sealed class 'Style' has no constructor parameters and only object/data object members. " +
            "Consider replacing it with a sealed interface."

        assertEquals(listOf(expected), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports sealed class with only objects`() {
        val code = """
            sealed class Action {
                object Ok : Action()
                object Cancel : Action()
            }
        """.trimIndent()
        val expected = "Sealed class 'Action' has no constructor parameters and only object/data object members. " +
            "Consider replacing it with a sealed interface."

        assertEquals(listOf(expected), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report sealed class with constructor parameters`() {
        val code = """
            sealed class Event(val id: Int) {
                data object Start : Event(0)
                data object Stop : Event(1)
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report sealed class with data class members`() {
        val code = """
            sealed class Result {
                data class Success(val value: String) : Result()
                data object Loading : Result()
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report sealed class with properties`() {
        val code = """
            sealed class State {
                abstract val label: String
                data object Active : State() { override val label = "active" }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report sealed interface`() {
        val code = """
            sealed interface Style {
                data object Card : Style
                data object Screen : Style
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }
}
