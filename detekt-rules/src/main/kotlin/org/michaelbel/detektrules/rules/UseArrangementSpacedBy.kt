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

class UseArrangementSpacedBy(config: Config): Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Equal spacing between Row/Column children via Spacer should use Arrangement.spacedBy().",
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

        val isRow = callee == ROW

        val spacingSizes = lambdaBody.statements.spacingSizesBetweenContentCalls(isRow) ?: return

        if (spacingSizes.isEmpty()) return

        val uniqueSize = spacingSizes.toSet()
        if (uniqueSize.size != 1) return

        val size = uniqueSize.first()
        val arrangement = if (isRow) "horizontalArrangement" else "verticalArrangement"
        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = "Replace Spacer elements with $arrangement = Arrangement.spacedBy($size)."
            )
        )
    }

    private fun KtCallExpression.isSpacer(): Boolean = calleeExpression?.text == SPACER

    private fun KtExpression?.isContentCall(): Boolean =
        this is KtCallExpression && !isSpacer()

    private fun List<KtExpression>.spacingSizesBetweenContentCalls(isRow: Boolean): List<String>? {
        var hasContentCall = false
        val spacingSizes = mutableListOf<String>()

        forEachIndexed { index, statement ->
            val call = statement as? KtCallExpression ?: return@forEachIndexed
            if (call.isSpacer()) return@forEachIndexed

            if (hasContentCall) {
                spacingSizes += extractSpacingBeforeContentCall(index, call, isRow) ?: return null
            }
            hasContentCall = true
        }

        return spacingSizes
    }

    private fun List<KtExpression>.extractSpacingBeforeContentCall(
        index: Int,
        call: KtCallExpression,
        isRow: Boolean
    ): String? {
        val previousStatement = getOrNull(index - 1)
        return when {
            previousStatement.isContentCall() -> extractLeadingPaddingSize(call, isRow)
            previousStatement is KtCallExpression &&
                previousStatement.isSpacer() &&
                getOrNull(index - 2).isContentCall() ->
                extractSpacerSize(previousStatement, isRow)
            else -> null
        }
    }

    private fun extractSpacerSize(spacer: KtCallExpression, isRow: Boolean): String? {
        val modifierExpr = spacer.valueArguments
            .firstOrNull()
            ?.getArgumentExpression() ?: return null
        return extractSizeFromModifierChain(modifierExpr, isRow)
    }

    private fun extractLeadingPaddingSize(call: KtCallExpression, isRow: Boolean): String? {
        val modifierExpr = call.valueArguments
            .firstModifierArgument()
            ?.getArgumentExpression() ?: return null
        return extractLeadingPaddingFromModifierChain(modifierExpr, isRow)
    }

    private fun extractSizeFromModifierChain(expr: KtExpression, isRow: Boolean): String? {
        if (expr !is KtDotQualifiedExpression) return null
        val selector = expr.selectorExpression as? KtCallExpression ?: return null
        val functionName = selector.calleeExpression?.text ?: return null

        val isAxisModifier = functionName == SIZE_MODIFIER ||
            (isRow && functionName == WIDTH_MODIFIER) ||
            (!isRow && functionName == HEIGHT_MODIFIER)

        if (isAxisModifier) {
            return selector.valueArguments.firstOrNull()?.getArgumentExpression()?.text
        }

        return extractSizeFromModifierChain(expr.receiverExpression, isRow)
    }

    private fun extractLeadingPaddingFromModifierChain(expr: KtExpression, isRow: Boolean): String? {
        if (expr !is KtDotQualifiedExpression) return null
        val selector = expr.selectorExpression as? KtCallExpression ?: return null

        if (selector.calleeExpression?.text == PADDING_MODIFIER) {
            val leadingArgument = if (isRow) START_PADDING_ARGUMENT else TOP_PADDING_ARGUMENT
            return selector.valueArguments
                .firstOrNull { it.getArgumentName()?.asName?.identifier == leadingArgument }
                ?.getArgumentExpression()
                ?.text
        }

        return extractLeadingPaddingFromModifierChain(expr.receiverExpression, isRow)
    }

    private fun List<ValueArgument>.firstModifierArgument(): ValueArgument? =
        firstOrNull { it.getArgumentName()?.asName?.identifier == MODIFIER_ARGUMENT } ?: firstOrNull {
            it.getArgumentExpression()?.text?.startsWith(MODIFIER_RECEIVER) == true
        }

    private companion object {
        const val ROW = "Row"
        const val COLUMN = "Column"
        const val SPACER = "Spacer"
        const val WIDTH_MODIFIER = "width"
        const val HEIGHT_MODIFIER = "height"
        const val SIZE_MODIFIER = "size"
        const val PADDING_MODIFIER = "padding"
        const val START_PADDING_ARGUMENT = "start"
        const val TOP_PADDING_ARGUMENT = "top"
        const val MODIFIER_ARGUMENT = "modifier"
        const val MODIFIER_RECEIVER = "Modifier"
        const val COMPOSE_LAYOUT_PACKAGE = "androidx.compose.foundation.layout"
    }
}
