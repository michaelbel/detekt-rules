package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SizeModifierWithConstrainAsTest {

    private val subject = SizeModifierWithConstrainAs(Config.empty)

    @Test
    fun `reports size before constrainAs`() {
        val code = """
            fun Test() {
                Column(
                    modifier = Modifier
                        .size(width = 50.dp, height = 58.dp)
                        .constrainAs(textsRef) {
                            start.linkTo(parent.start, 16.dp)
                            top.linkTo(sizeListRef.top)
                        }
                )
            }
        """.trimIndent()
        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports width before constrainAs`() {
        val code = """
            fun Test() {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .constrainAs(boxRef) {
                            start.linkTo(parent.start)
                        }
                )
            }
        """.trimIndent()
        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports height before constrainAs`() {
        val code = """
            fun Test() {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .constrainAs(boxRef) {
                            top.linkTo(parent.top)
                        }
                )
            }
        """.trimIndent()
        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports size after constrainAs`() {
        val code = """
            fun Test() {
                Box(
                    modifier = Modifier
                        .constrainAs(boxRef) {
                            start.linkTo(parent.start)
                        }
                        .size(80.dp)
                )
            }
        """.trimIndent()
        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports fillMaxWidth with constrainAs`() {
        val code = """
            fun Test() {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(boxRef) {
                            top.linkTo(parent.top)
                        }
                )
            }
        """.trimIndent()
        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports fillMaxSize with constrainAs`() {
        val code = """
            fun Test() {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .constrainAs(boxRef) {
                            top.linkTo(parent.top)
                        }
                )
            }
        """.trimIndent()
        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports message with correct dimension hint for size`() {
        val code = """
            fun Test() {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .constrainAs(boxRef) {
                            top.linkTo(parent.top)
                        }
                )
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertEquals(1, findings.size)
        assert(findings[0].message.contains("Dimension.value(...)"))
    }

    @Test
    fun `reports message with correct dimension hint for fillMaxHeight`() {
        val code = """
            fun Test() {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .constrainAs(boxRef) {
                            top.linkTo(parent.top)
                        }
                )
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertEquals(1, findings.size)
        assert(findings[0].message.contains("Dimension.fillToConstraints"))
    }

    @Test
    fun `does not report constrainAs without size modifiers`() {
        val code = """
            fun Test() {
                Box(
                    modifier = Modifier
                        .constrainAs(boxRef) {
                            width = Dimension.value(50.dp)
                            height = Dimension.value(58.dp)
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                        }
                )
            }
        """.trimIndent()
        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report constrainAs with padding modifier`() {
        val code = """
            fun Test() {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .constrainAs(boxRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                        }
                )
            }
        """.trimIndent()
        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report constrainAs alone`() {
        val code = """
            fun Test() {
                Text(
                    modifier = Modifier.constrainAs(textRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                )
            }
        """.trimIndent()
        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }
}
