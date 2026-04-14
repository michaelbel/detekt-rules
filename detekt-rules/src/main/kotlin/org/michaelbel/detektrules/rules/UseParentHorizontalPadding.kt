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
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.ValueArgument

class UseParentHorizontalPadding(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Equal edge padding on first and last children should be moved to the parent container as horizontal or vertical padding.",
        debt = Debt.FIVE_MINS
    )

    private var hasLayoutImport: Boolean = false

    override fun visitKtFile(file: KtFile) {
        hasLayoutImport = file.importDirectives.any { directive ->
            val importPath = directive.importPath?.pathStr ?: return@any false
            importPath.startsWith(COMPOSE_LAYOUT_PACKAGE)
        }
        super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!hasLayoutImport) return

        val callee = expression.calleeExpression?.text ?: return
        if (callee != ROW && callee != COLUMN) return

        val lambdaBody = expression.lambdaArguments
            .firstOrNull()
            ?.getLambdaExpression()
            ?.bodyExpression ?: return

        val statements = lambdaBody.statements
        if (statements.size < 2) return

        val isRow = callee == ROW
        val leadingArg = if (isRow) START_ARG else TOP_ARG
        val trailingArg = if (isRow) END_ARG else BOTTOM_ARG

        val firstChild = statements.first() as? KtCallExpression ?: return
        val lastChild = statements.last() as? KtCallExpression ?: return

        val firstPaddingValue = findExclusivePaddingArgValue(firstChild, leadingArg) ?: return
        val lastPaddingValue = findExclusivePaddingArgValue(lastChild, trailingArg) ?: return

        if (firstPaddingValue != lastPaddingValue) return

        val paddingType = if (isRow) HORIZONTAL else VERTICAL
        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = "Move padding($leadingArg = $firstPaddingValue) from first child and padding($trailingArg = $lastPaddingValue) from last child to parent $callee with padding($paddingType = $firstPaddingValue)."
            )
        )
    }

    private fun findExclusivePaddingArgValue(call: KtCallExpression, argName: String): String? {
        val modifierExpr = call.valueArguments.firstModifierArgument()?.getArgumentExpression() ?: return null
        return findExclusivePaddingInChain(modifierExpr, argName)
    }

    private fun findExclusivePaddingInChain(expr: KtExpression, argName: String): String? {
        if (expr !is KtDotQualifiedExpression) return null
        val selector = expr.selectorExpression as? KtCallExpression ?: return null

        if (selector.calleeExpression?.text == PADDING_MODIFIER) {
            val args = selector.valueArguments
            if (args.size == 1) {
                val arg = args.first()
                val name = arg.getArgumentName()?.asName?.identifier
                if (name == argName) {
                    return arg.getArgumentExpression()?.text
                }
            }
        }

        return findExclusivePaddingInChain(expr.receiverExpression, argName)
    }

    private fun List<ValueArgument>.firstModifierArgument(): ValueArgument? =
        firstOrNull { it.getArgumentName()?.asName?.identifier == MODIFIER_ARGUMENT } ?: firstOrNull {
            it.getArgumentExpression()?.text?.startsWith(MODIFIER_RECEIVER) == true
        }

    private companion object {
        const val ROW = "Row"
        const val COLUMN = "Column"
        const val PADDING_MODIFIER = "padding"
        const val START_ARG = "start"
        const val END_ARG = "end"
        const val TOP_ARG = "top"
        const val BOTTOM_ARG = "bottom"
        const val HORIZONTAL = "horizontal"
        const val VERTICAL = "vertical"
        const val MODIFIER_ARGUMENT = "modifier"
        const val MODIFIER_RECEIVER = "Modifier"
        const val COMPOSE_LAYOUT_PACKAGE = "androidx.compose.foundation.layout"
    }
}
