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
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class ConstrainAsOperatorOrder(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Operators inside constrainAs block must be ordered: width, height, start, top, end, bottom.",
        debt = Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != CONSTRAIN_AS_FUNCTION_NAME) return

        val statements = expression.lambdaArguments.firstOrNull()
            ?.getLambdaExpression()?.bodyExpression?.statements ?: return

        val presentConstraints = statements.mapNotNull { extractConstraintName(it) }
            .filter { it in EXPECTED_ORDER }

        val sortedConstraints = presentConstraints.sortedBy { EXPECTED_ORDER.indexOf(it) }

        if (presentConstraints != sortedConstraints) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(expression),
                    message = "Operators inside constrainAs must be ordered: ${EXPECTED_ORDER.joinToString()}. " +
                        "Found: ${presentConstraints.joinToString()}."
                )
            )
        }
    }

    private fun extractConstraintName(statement: KtExpression): String? {
        return when (statement) {
            is KtBinaryExpression -> {
                if (statement.operationToken == KtTokens.EQ) {
                    (statement.left as? KtNameReferenceExpression)?.getReferencedName()
                } else null
            }
            is KtDotQualifiedExpression -> {
                (statement.receiverExpression as? KtNameReferenceExpression)?.getReferencedName()
            }
            else -> null
        }
    }

    private companion object {
        const val CONSTRAIN_AS_FUNCTION_NAME = "constrainAs"
        val EXPECTED_ORDER = listOf("width", "height", "start", "top", "end", "bottom")
    }
}
