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

class LazyListItemsSharedPadding(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Equal horizontal padding on all LazyColumn item blocks (or vertical on LazyRow) " +
            "should be moved to contentPadding.",
        debt = Debt.FIVE_MINS
    )

    private var hasFoundationImport: Boolean = false

    override fun visitKtFile(file: KtFile) {
        hasFoundationImport = file.importDirectives.any { directive ->
            val importPath = directive.importPath?.pathStr ?: return@any false
            importPath.startsWith(COMPOSE_FOUNDATION_PACKAGE)
        }
        super.visitKtFile(file)
    }

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!hasFoundationImport) return

        val callee = expression.calleeExpression?.text ?: return
        if (callee != LAZY_COLUMN && callee != LAZY_ROW) return

        val lambdaBody = expression.lambdaArguments
            .firstOrNull()
            ?.getLambdaExpression()
            ?.bodyExpression ?: return

        val paddingArgName = if (callee == LAZY_COLUMN) HORIZONTAL else VERTICAL

        val itemCalls = lambdaBody.statements
            .filterIsInstance<KtCallExpression>()
            .filter { it.calleeExpression?.text == ITEM }

        if (itemCalls.size < 2) return

        val paddingValues = itemCalls.map { itemCall ->
            extractItemPaddingValue(itemCall, paddingArgName) ?: return
        }

        val uniqueValue = paddingValues.toSet()
        if (uniqueValue.size != 1) return

        val paddingValue = uniqueValue.first()
        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = "Remove padding($paddingArgName = $paddingValue) from all $callee item blocks " +
                    "and add PaddingValues($paddingArgName = $paddingValue) to the contentPadding argument."
            )
        )
    }

    private fun extractItemPaddingValue(itemCall: KtCallExpression, paddingArgName: String): String? {
        val itemLambdaBody = itemCall.lambdaArguments
            .firstOrNull()
            ?.getLambdaExpression()
            ?.bodyExpression ?: return null

        val rootCall = itemLambdaBody.statements.firstOrNull() as? KtCallExpression ?: return null
        val modifierExpr = rootCall.valueArguments.firstModifierArgument()?.getArgumentExpression() ?: return null
        return findPaddingInChain(modifierExpr, paddingArgName)
    }

    @Suppress("ReturnCount")
    private fun findPaddingInChain(expr: KtExpression, paddingArgName: String): String? {
        if (expr !is KtDotQualifiedExpression) return null
        val selector = expr.selectorExpression as? KtCallExpression ?: return null

        if (selector.calleeExpression?.text == PADDING_MODIFIER) {
            val args = selector.valueArguments
            if (args.size == 1) {
                val arg = args.first()
                if (arg.getArgumentName()?.asName?.identifier == paddingArgName) {
                    return arg.getArgumentExpression()?.text
                }
            }
        }

        return findPaddingInChain(expr.receiverExpression, paddingArgName)
    }

    private fun List<ValueArgument>.firstModifierArgument(): ValueArgument? =
        firstOrNull { it.getArgumentName()?.asName?.identifier == MODIFIER_ARGUMENT } ?: firstOrNull {
            it.getArgumentExpression()?.text?.startsWith(MODIFIER_RECEIVER) == true
        }

    private companion object {
        const val LAZY_COLUMN = "LazyColumn"
        const val LAZY_ROW = "LazyRow"
        const val ITEM = "item"
        const val PADDING_MODIFIER = "padding"
        const val HORIZONTAL = "horizontal"
        const val VERTICAL = "vertical"
        const val MODIFIER_ARGUMENT = "modifier"
        const val MODIFIER_RECEIVER = "Modifier"
        const val COMPOSE_FOUNDATION_PACKAGE = "androidx.compose.foundation"
    }
}