package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConstraintLayoutRefsPostfixTest {

    private val subject = ConstraintLayoutRefsPostfix(Config.empty)

    @Test
    fun `does not report refs with Ref postfix`() {
        val code = """
            fun Test() {
                ConstraintLayout {
                    val (titleRef, tableButtonRef, textsRef, sizeListRef, statusRef) = createRefs()
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports refs without Ref postfix`() {
        val code = """
            fun Test() {
                ConstraintLayout {
                    val (title, tableButton, texts, sizeList, status) = createRefs()
                }
            }
        """.trimIndent()

        assertEquals(
            listOf(
                "ConstraintLayout ref \"title\" must end with \"Ref\".",
                "ConstraintLayout ref \"tableButton\" must end with \"Ref\".",
                "ConstraintLayout ref \"texts\" must end with \"Ref\".",
                "ConstraintLayout ref \"sizeList\" must end with \"Ref\".",
                "ConstraintLayout ref \"status\" must end with \"Ref\".",
            ),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports only refs without Ref postfix`() {
        val code = """
            fun Test() {
                ConstraintLayout {
                    val (titleRef, tableButton, textsRef, sizeList, _) = createRefs()
                }
            }
        """.trimIndent()

        assertEquals(
            listOf(
                "ConstraintLayout ref \"tableButton\" must end with \"Ref\".",
                "ConstraintLayout ref \"sizeList\" must end with \"Ref\".",
            ),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports createRef property without Ref postfix`() {
        val code = """
            fun Test() {
                ConstraintLayout {
                    val title = createRef()
                }
            }
        """.trimIndent()

        assertEquals(
            listOf("ConstraintLayout ref \"title\" must end with \"Ref\"."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `does not report createRef property with Ref postfix`() {
        val code = """
            fun Test() {
                ConstraintLayout {
                    val titleRef = createRef()
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report unrelated destructuring declaration`() {
        val code = """
            val (title, tableButton) = pair
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }
}
