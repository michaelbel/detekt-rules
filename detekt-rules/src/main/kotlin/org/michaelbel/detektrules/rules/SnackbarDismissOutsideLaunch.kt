package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiElement

class SnackbarDismissOutsideLaunch(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "SnackbarData.dismiss() should be called before launching a coroutine for showSnackbar().",
        debt = Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != DISMISS_FUNCTION_NAME) return
        if (!expression.hasCurrentSnackbarDataReceiver()) return
        if (!expression.isInsideLaunchLambda()) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = "Call currentSnackbarData?.dismiss() outside scope.launch because dismiss() is synchronous."
            )
        )
    }

    private fun KtCallExpression.hasCurrentSnackbarDataReceiver(): Boolean {
        val qualifiedExpression = parent as? KtQualifiedExpression ?: return false
        return qualifiedExpression.selectorExpression === this &&
            qualifiedExpression.receiverExpression.hasCurrentSnackbarDataReference()
    }

    private fun org.jetbrains.kotlin.psi.KtExpression.hasCurrentSnackbarDataReference(): Boolean =
        when (this) {
            is KtNameReferenceExpression -> getReferencedName() == CURRENT_SNACKBAR_DATA_PROPERTY_NAME
            is KtQualifiedExpression -> receiverExpression.hasCurrentSnackbarDataReference() ||
                selectorExpression?.text == CURRENT_SNACKBAR_DATA_PROPERTY_NAME
            else -> false
        }

    private fun KtCallExpression.isInsideLaunchLambda(): Boolean {
        var current: PsiElement? = parent
        while (current != null) {
            if (current is KtLambdaExpression && current.parentCallName() == LAUNCH_FUNCTION_NAME) {
                return true
            }
            current = current.parent
        }
        return false
    }

    private fun KtLambdaExpression.parentCallName(): String? {
        var current: PsiElement? = parent
        repeat(MAX_LAMBDA_PARENT_DEPTH) {
            if (current is KtCallExpression) {
                return current.calleeExpression?.text
            }
            current = current?.parent
        }
        return null
    }

    private companion object {
        const val DISMISS_FUNCTION_NAME = "dismiss"
        const val CURRENT_SNACKBAR_DATA_PROPERTY_NAME = "currentSnackbarData"
        const val LAUNCH_FUNCTION_NAME = "launch"
        const val MAX_LAMBDA_PARENT_DEPTH = 3
    }
}
