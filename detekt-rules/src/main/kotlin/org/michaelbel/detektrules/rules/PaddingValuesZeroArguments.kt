package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtValueArgument

class PaddingValuesZeroArguments(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "PaddingValues with all-zero arguments should use the no-arg constructor " +
            "since all padding dimensions default to 0.dp.",
        debt = Debt.FIVE_MINS
    )

    override fun visitKtFile(file: KtFile) {
        hasComposePaddingValuesImport = file.importDirectives.any { directive ->
            val importPath = directive.importPath?.pathStr ?: return@any false
            importPath == COMPOSE_PADDING_VALUES_IMPORT || importPath == "$COMPOSE_PADDING_PACKAGE.*"
        }
        super.visitKtFile(file)
    }

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!hasComposePaddingValuesImport) return
        if (expression.calleeExpression?.text != PADDING_VALUES_FUNCTION_NAME) return

        val args = expression.valueArguments
        if (args.isEmpty() || args.size > MAX_PADDING_ARGS) return
        if (args.all { it.isZeroDp() }) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(expression),
                    message = "Replace PaddingValues(0.dp) with PaddingValues() — " +
                        "all padding dimensions default to 0.dp."
                )
            )
        }
    }

    private var hasComposePaddingValuesImport: Boolean = false

    private fun KtValueArgument.isZeroDp(): Boolean {
        val expr = getArgumentExpression() as? KtDotQualifiedExpression ?: return false
        return expr.receiverExpression.text == "0" && expr.selectorExpression?.text == "dp"
    }

    private companion object {
        const val MAX_PADDING_ARGS = 4
        const val PADDING_VALUES_FUNCTION_NAME = "PaddingValues"
        const val COMPOSE_PADDING_PACKAGE = "androidx.compose.foundation.layout"
        const val COMPOSE_PADDING_VALUES_IMPORT = "$COMPOSE_PADDING_PACKAGE.$PADDING_VALUES_FUNCTION_NAME"
    }
}
