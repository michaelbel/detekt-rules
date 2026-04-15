package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MultipleSerializableApiModelsTest {

    private val subject = MultipleSerializableApiModels(Config.empty)

    @Test
    fun `reports second serializable api model in file`() {
        val code = """
            import kotlinx.serialization.SerialName
            import kotlinx.serialization.Serializable

            @Serializable
            data class ProductResponse(
                @SerialName("id") val id: String?
            )

            @Serializable
            data class ProductColorResponse(
                @SerialName("name") val name: String?
            )
        """.trimIndent()

        assertEquals(
            listOf("Move API model 'ProductColorResponse' to a separate file."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports every serializable api model after first one`() {
        val code = """
            import kotlinx.serialization.SerialName
            import kotlinx.serialization.Serializable

            @Serializable
            data class ProductResponse(
                @SerialName("id") val id: String?
            )

            @Serializable
            data class ProductColorResponse(
                @SerialName("name") val name: String?
            )

            @Serializable
            data class ProductActionResponse(
                @SerialName("title") val title: String?
            )
        """.trimIndent()

        assertEquals(
            listOf(
                "Move API model 'ProductColorResponse' to a separate file.",
                "Move API model 'ProductActionResponse' to a separate file.",
            ),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `does not report single serializable api model in file`() {
        val code = """
            import kotlinx.serialization.SerialName
            import kotlinx.serialization.Serializable

            @Serializable
            data class ProductResponse(
                @SerialName("id") val id: String?
            )
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not count serializable data class without SerialName`() {
        val code = """
            import kotlinx.serialization.SerialName
            import kotlinx.serialization.Serializable

            @Serializable
            data class ProductResponse(
                @SerialName("id") val id: String?
            )

            @Serializable
            data class LocalProduct(
                val id: String?
            )
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not count non serializable data class with SerialName`() {
        val code = """
            import kotlinx.serialization.SerialName
            import kotlinx.serialization.Serializable

            @Serializable
            data class ProductResponse(
                @SerialName("id") val id: String?
            )

            data class ProductColorResponse(
                @SerialName("name") val name: String?
            )
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not count regular class with Serializable and SerialName`() {
        val code = """
            import kotlinx.serialization.SerialName
            import kotlinx.serialization.Serializable

            @Serializable
            data class ProductResponse(
                @SerialName("id") val id: String?
            )

            @Serializable
            class ProductColorResponse(
                @SerialName("name") val name: String?
            )
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }
}
