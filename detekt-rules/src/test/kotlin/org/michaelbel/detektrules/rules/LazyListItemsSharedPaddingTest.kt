package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LazyListItemsSharedPaddingTest {

    private val subject = LazyListItemsSharedPadding(Config.empty)

    @Test
    fun `reports LazyColumn when all items have equal horizontal padding`() {
        val code = """
            package test

            import androidx.compose.foundation.lazy.LazyColumn

            @Composable
            fun Example(contentPadding: PaddingValues) {
                LazyColumn(contentPadding = contentPadding) {
                    item {
                        Card(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth())
                    }
                    item {
                        Text(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                    }
                }
            }
        """.trimIndent()

        val expected = "Remove padding(horizontal = 16.dp) from all LazyColumn item blocks " +
            "and add PaddingValues(horizontal = 16.dp) to the contentPadding argument."
        assertEquals(listOf(expected), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports LazyRow when all items have equal vertical padding`() {
        val code = """
            package test

            import androidx.compose.foundation.lazy.LazyRow

            @Composable
            fun Example() {
                LazyRow {
                    item {
                        Card(modifier = Modifier.padding(vertical = 8.dp))
                    }
                    item {
                        Text(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        """.trimIndent()

        val expected = "Remove padding(vertical = 8.dp) from all LazyRow item blocks " +
            "and add PaddingValues(vertical = 8.dp) to the contentPadding argument."
        assertEquals(listOf(expected), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports LazyColumn with more than two items all having equal horizontal padding`() {
        val code = """
            package test

            import androidx.compose.foundation.lazy.LazyColumn

            @Composable
            fun Example() {
                LazyColumn {
                    item {
                        Card(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                    item {
                        Text(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                    item {
                        Spacer(modifier = Modifier.padding(horizontal = 16.dp).height(8.dp))
                    }
                }
            }
        """.trimIndent()

        val expected = "Remove padding(horizontal = 16.dp) from all LazyColumn item blocks " +
            "and add PaddingValues(horizontal = 16.dp) to the contentPadding argument."
        assertEquals(listOf(expected), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when padding values differ`() {
        val code = """
            package test

            import androidx.compose.foundation.lazy.LazyColumn

            @Composable
            fun Example() {
                LazyColumn {
                    item {
                        Card(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                    item {
                        Text(modifier = Modifier.padding(horizontal = 8.dp))
                    }
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when one item has no horizontal padding`() {
        val code = """
            package test

            import androidx.compose.foundation.lazy.LazyColumn

            @Composable
            fun Example() {
                LazyColumn {
                    item {
                        Card(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                    item {
                        Text(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when LazyColumn has only one item`() {
        val code = """
            package test

            import androidx.compose.foundation.lazy.LazyColumn

            @Composable
            fun Example() {
                LazyColumn {
                    item {
                        Card(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report without compose foundation import`() {
        val code = """
            package test

            @Composable
            fun Example() {
                LazyColumn {
                    item {
                        Card(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                    item {
                        Text(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when LazyColumn items have vertical padding instead of horizontal`() {
        val code = """
            package test

            import androidx.compose.foundation.lazy.LazyColumn

            @Composable
            fun Example() {
                LazyColumn {
                    item {
                        Card(modifier = Modifier.padding(vertical = 16.dp))
                    }
                    item {
                        Text(modifier = Modifier.padding(vertical = 16.dp))
                    }
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when padding is combined with other named arguments`() {
        val code = """
            package test

            import androidx.compose.foundation.lazy.LazyColumn

            @Composable
            fun Example() {
                LazyColumn {
                    item {
                        Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                    item {
                        Text(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports with wildcard foundation import`() {
        val code = """
            package test

            import androidx.compose.foundation.lazy.*

            @Composable
            fun Example() {
                LazyColumn {
                    item {
                        Card(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                    item {
                        Text(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        """.trimIndent()

        val expected = "Remove padding(horizontal = 16.dp) from all LazyColumn item blocks " +
            "and add PaddingValues(horizontal = 16.dp) to the contentPadding argument."
        assertEquals(listOf(expected), subject.lint(code).map { it.message })
    }
}
