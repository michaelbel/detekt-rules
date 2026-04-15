package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UseParentHorizontalPaddingTest {

    private val subject = UseParentHorizontalPadding(Config.empty)

    @Test
    fun `reports Row when first child has padding start and last child has equal padding end`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon(modifier = Modifier.padding(start = 16.dp).size(24.dp))
                    Text(modifier = Modifier.fillMaxWidth().padding(end = 16.dp))
                }
            }
        """.trimIndent()

        val expected = "Move padding(start = 16.dp) from first child and padding(end = 16.dp) " +
            "from last child to parent Row with padding(horizontal = 16.dp)."
        assertEquals(listOf(expected), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports Column when first child has padding top and last child has equal padding bottom`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Column
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Column {
                    Icon(modifier = Modifier.padding(top = 8.dp))
                    Text(modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        """.trimIndent()

        val expected = "Move padding(top = 8.dp) from first child and padding(bottom = 8.dp) " +
            "from last child to parent Column with padding(vertical = 8.dp)."
        assertEquals(listOf(expected), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports Row with more than two children`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon(modifier = Modifier.padding(start = 16.dp))
                    Spacer()
                    Text(modifier = Modifier.padding(end = 16.dp))
                }
            }
        """.trimIndent()

        val expected = "Move padding(start = 16.dp) from first child and padding(end = 16.dp) " +
            "from last child to parent Row with padding(horizontal = 16.dp)."
        assertEquals(listOf(expected), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when padding values are different`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon(modifier = Modifier.padding(start = 16.dp))
                    Text(modifier = Modifier.padding(end = 8.dp))
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when first child has padding end instead of start`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon(modifier = Modifier.padding(end = 16.dp))
                    Text(modifier = Modifier.padding(end = 16.dp))
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when last child has padding start instead of end`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon(modifier = Modifier.padding(start = 16.dp))
                    Text(modifier = Modifier.padding(start = 16.dp))
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when first child padding start is combined with other sides`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon(modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                    Text(modifier = Modifier.padding(end = 16.dp))
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when only one child`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon(modifier = Modifier.padding(start = 16.dp))
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report without compose layout import`() {
        val code = """
            package test

            @Composable
            fun Example() {
                Row {
                    Icon(modifier = Modifier.padding(start = 16.dp))
                    Text(modifier = Modifier.padding(end = 16.dp))
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when first child has no modifier`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon()
                    Text(modifier = Modifier.padding(end = 16.dp))
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports with wildcard layout import`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.*
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon(modifier = Modifier.padding(start = 16.dp).size(24.dp))
                    Text(modifier = Modifier.fillMaxWidth().padding(end = 16.dp))
                }
            }
        """.trimIndent()

        val expected = "Move padding(start = 16.dp) from first child and padding(end = 16.dp) " +
            "from last child to parent Row with padding(horizontal = 16.dp)."
        assertEquals(listOf(expected), subject.lint(code).map { it.message })
    }
}
