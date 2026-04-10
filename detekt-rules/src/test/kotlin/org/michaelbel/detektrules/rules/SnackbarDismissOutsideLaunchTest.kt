package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SnackbarDismissOutsideLaunchTest {

    private val subject = SnackbarDismissOutsideLaunch(Config.empty)

    @Test
    fun `reports dismiss inside launch with run receiver`() {
        val code = """
            fun Test() {
                scope.launch {
                    snackbarHostState.run {
                        currentSnackbarData?.dismiss()
                        showSnackbar("Message")
                    }
                }
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports dismiss inside launch with snackbar host state receiver`() {
        val code = """
            fun Test() {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar("Message")
                }
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `does not report dismiss before launch`() {
        val code = """
            fun Test() {
                snackbarHostState.currentSnackbarData?.dismiss()
                scope.launch {
                    snackbarHostState.showSnackbar("Message")
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report other dismiss calls inside launch`() {
        val code = """
            fun Test() {
                scope.launch {
                    dialog.dismiss()
                    snackbarHostState.showSnackbar("Message")
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports dismiss inside launch function argument`() {
        val code = """
            fun Test() {
                scope.launch({
                    currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar("Message")
                })
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }
}
