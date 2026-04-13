package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UseArrangementSpacedByTest {

    private val subject = UseArrangementSpacedBy(Config.empty)

    @Test
    fun `reports Row with two equal width Spacers`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text()
                    Spacer(modifier = Modifier.width(16.dp))
                    Button()
                }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace Spacer elements with horizontalArrangement = Arrangement.spacedBy(16.dp)."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports Column with two equal height Spacers`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Column
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Column {
                    Text()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text()
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace Spacer elements with verticalArrangement = Arrangement.spacedBy(8.dp)."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports Row with three equal width Spacers`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    A()
                    Spacer(modifier = Modifier.width(12.dp))
                    B()
                    Spacer(modifier = Modifier.width(12.dp))
                    C()
                    Spacer(modifier = Modifier.width(12.dp))
                    D()
                }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace Spacer elements with horizontalArrangement = Arrangement.spacedBy(12.dp)."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports Row with positional Spacer argument`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon()
                    Spacer(Modifier.width(16.dp))
                    Text()
                    Spacer(Modifier.width(16.dp))
                }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace Spacer elements with horizontalArrangement = Arrangement.spacedBy(16.dp)."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports Row with size Spacer modifier`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    A()
                    Spacer(Modifier.size(16.dp))
                    B()
                    Spacer(Modifier.size(16.dp))
                }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace Spacer elements with horizontalArrangement = Arrangement.spacedBy(16.dp)."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `does not report when Spacers between children have different sizes`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon()
                    Spacer(modifier = Modifier.width(8.dp))
                    Text()
                    Spacer(modifier = Modifier.width(16.dp))
                    Button()
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports when one Spacer is between Row children`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text()
                }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace Spacer elements with horizontalArrangement = Arrangement.spacedBy(16.dp)."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports Row with start padding on second child`() {
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
                    Text(modifier = Modifier.padding(start = 16.dp))
                }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace Spacer elements with horizontalArrangement = Arrangement.spacedBy(16.dp)."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports Row with equal start padding on each child after first`() {
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
                    Text(modifier = Modifier.padding(start = 16.dp))
                    Button(modifier = Modifier.padding(start = 16.dp))
                }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace Spacer elements with horizontalArrangement = Arrangement.spacedBy(16.dp)."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `reports Column with top padding on second child`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Column
            import androidx.compose.foundation.layout.padding
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Column {
                    Icon()
                    Text(modifier = Modifier.padding(top = 16.dp))
                }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace Spacer elements with verticalArrangement = Arrangement.spacedBy(16.dp)."),
            subject.lint(code).map { it.message }
        )
    }

    @Test
    fun `does not report padding on first Row child`() {
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
                    Text()
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when only one Row gap has spacing`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text()
                    Button()
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report flexible weight Spacers`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.ui.Modifier

            @Composable
            fun Example() {
                Row {
                    Icon()
                    Spacer(modifier = Modifier.weight(1f))
                    Text()
                    Spacer(modifier = Modifier.weight(1f))
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
                    Icon()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text()
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report height Spacers in Row`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Row
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Row {
                    Icon()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report width Spacers in Column`() {
        val code = """
            package test

            import androidx.compose.foundation.layout.Column
            import androidx.compose.foundation.layout.Spacer
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Example() {
                Column {
                    Icon()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text()
                    Spacer(modifier = Modifier.width(16.dp))
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
                    Icon()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text()
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        """.trimIndent()

        assertEquals(
            listOf("Replace Spacer elements with horizontalArrangement = Arrangement.spacedBy(16.dp)."),
            subject.lint(code).map { it.message }
        )
    }
}
