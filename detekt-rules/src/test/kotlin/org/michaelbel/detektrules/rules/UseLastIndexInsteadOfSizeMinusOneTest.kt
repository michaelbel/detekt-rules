package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UseLastIndexInsteadOfSizeMinusOneTest {

    private val subject = UseLastIndexInsteadOfSizeMinusOne(Config.empty)

    @Test
    fun `reports size minus 1 operator`() {
        val code = """
            fun test(index: Int, list: List<String>) {
                if (index != list.size - 1) { }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace 'list.size - 1' with 'list.lastIndex'."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports size minus function`() {
        val code = """
            fun test(index: Int, list: List<String>) {
                if (index != list.size.minus(1)) { }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace 'list.size.minus(1)' with 'list.lastIndex'."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports nested receiver with size minus 1`() {
        val code = """
            fun test(index: Int, pojo: Pojo) {
                if (index != pojo.deliveryEntities.size - 1) { }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace 'pojo.deliveryEntities.size - 1' with 'pojo.deliveryEntities.lastIndex'."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports nested receiver with size minus function`() {
        val code = """
            fun test(index: Int, pojo: Pojo) {
                if (index != pojo.deliveryEntities.size.minus(1)) { }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace 'pojo.deliveryEntities.size.minus(1)' with 'pojo.deliveryEntities.lastIndex'."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `does not report lastIndex usage`() {
        val code = """
            fun test(index: Int, list: List<String>) {
                if (index != list.lastIndex) { }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report size minus non-one`() {
        val code = """
            fun test(index: Int, list: List<String>) {
                if (index != list.size - 2) { }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report minus on non-size receiver`() {
        val code = """
            fun test(index: Int, list: List<String>) {
                if (index != list.count() - 1) { }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }
}
