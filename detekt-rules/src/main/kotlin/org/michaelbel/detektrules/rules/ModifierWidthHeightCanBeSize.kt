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
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression

class ModifierWidthHeightCanBeSize(config: Config) : Rule(config) {

    override val issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Use Modifier.size(width, height) instead of chaining .width() and .height() modifiers.",
        debt = Debt.FIVE_MINS
    )

    private var hasLayoutImport = false

    override fun visitKtFile(file: KtFile) {
        hasLayoutImport = file.importDirectives.any { directive ->
            val importPath = directive.importPath?.pathStr ?: return@any false
            importPath == "$COMPOSE_LAYOUT_PACKAGE.$WIDTH" ||
                importPath == "$COMPOSE_LAYOUT_PACKAGE.$HEIGHT" ||
                importPath == "$COMPOSE_LAYOUT_PACKAGE.*"
        }
        super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!hasLayoutImport) return

        val outerFn = expression.calleeExpression?.text ?: return
        if (outerFn != WIDTH && outerFn != HEIGHT) return

        val outerDotExpr = expression.parent as? KtDotQualifiedExpression ?: return
        if (outerDotExpr.selectorExpression !== expression) return

        val innerDotExpr = outerDotExpr.receiverExpression as? KtDotQualifiedExpression ?: return
        val innerCall = innerDotExpr.selectorExpression as? KtCallExpression ?: return
        val innerFn = innerCall.calleeExpression?.text ?: return

        val isWidthHeightPair = (outerFn == HEIGHT && innerFn == WIDTH) ||
            (outerFn == WIDTH && innerFn == HEIGHT)
        if (!isWidthHeightPair) return

        if (innerDotExpr.receiverExpression.rootReceiverName() != MODIFIER) return

        val widthCall = if (innerFn == WIDTH) innerCall else expression
        val heightCall = if (innerFn == HEIGHT) innerCall else expression

        val widthArg = widthCall.valueArguments.firstOrNull()?.text ?: return
        val heightArg = heightCall.valueArguments.firstOrNull()?.text ?: return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(outerDotExpr),
                message = "Replace '$MODIFIER.$WIDTH($widthArg).$HEIGHT($heightArg)' " +
                    "with '$MODIFIER.size(width = $widthArg, height = $heightArg)'."
            )
        )
    }

    private fun org.jetbrains.kotlin.psi.KtExpression.rootReceiverName(): String? = when (this) {
        is KtNameReferenceExpression -> getReferencedName()
        is KtQualifiedExpression -> receiverExpression.rootReceiverName()
        else -> null
    }

    private companion object {
        const val MODIFIER = "Modifier"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val COMPOSE_LAYOUT_PACKAGE = "androidx.compose.foundation.layout"
    }
}
