package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile

class TextAlignInTextStyle(config: Config): Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Compose Text should declare textAlign inside style instead of a separate textAlign argument.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitKtFile(file: KtFile) {
        hasComposeTextImport = file.importDirectives.any { directive ->
            val importPath = directive.importPath?.pathStr ?: return@any false
            importPath in COMPOSE_TEXT_IMPORTS || COMPOSE_TEXT_PACKAGES.any { importPath == "$it.*" }
        }
        super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val isComposeTextCall = hasComposeTextImport && expression.calleeExpression?.text == TEXT_FUNCTION_NAME
        if (!isComposeTextCall) {
            return
        }

        val hasSeparateTextAlignArgument = expression.valueArguments.any { argument ->
            argument.getArgumentName()?.asName?.identifier == TEXT_ALIGN_ARGUMENT_NAME
        }

        if (hasSeparateTextAlignArgument) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(expression),
                    message = "Move Text textAlign into the style argument instead of passing it separately.",
                ),
            )
        }
    }

    private var hasComposeTextImport: Boolean = false

    private companion object {
        const val TEXT_FUNCTION_NAME = "Text"
        const val TEXT_ALIGN_ARGUMENT_NAME = "textAlign"

        val COMPOSE_TEXT_IMPORTS = setOf(
            "androidx.compose.material.Text",
            "androidx.compose.material3.Text",
        )

        val COMPOSE_TEXT_PACKAGES = setOf(
            "androidx.compose.material",
            "androidx.compose.material3",
        )
    }
}
