package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class UseLastIndexInsteadOfSizeMinusOne(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Use lastIndex instead of size - 1 or size.minus(1) to check for the last element.",
        debt = Debt.FIVE_MINS
    )

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        val left = expression.left
        val right = expression.right
        if (expression.operationToken != KtTokens.MINUS || left == null || right == null ||
            right !is KtConstantExpression || right.text != "1") return

        val receiverText = left.sizeReceiverText() ?: return
        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = "Replace '${left.text} - 1' with '$receiverText.lastIndex'."
            )
        )
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        val selector = expression.selectorExpression as? KtCallExpression
        val args = selector?.valueArguments
        if (selector?.calleeExpression?.text != "minus" || args?.size != 1 ||
            args[0].getArgumentExpression()?.text != "1") return

        val receiverText = expression.receiverExpression.sizeReceiverText() ?: return
        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = "Replace '${expression.receiverExpression.text}.minus(1)' with '$receiverText.lastIndex'."
            )
        )
    }

    private fun KtExpression.sizeReceiverText(): String? =
        (this as? KtDotQualifiedExpression)
            ?.takeIf { (it.selectorExpression as? KtNameReferenceExpression)?.getReferencedName() == "size" }
            ?.receiverExpression?.text
}
