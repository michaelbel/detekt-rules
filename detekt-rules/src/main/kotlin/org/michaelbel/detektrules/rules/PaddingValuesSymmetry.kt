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
import org.jetbrains.kotlin.psi.ValueArgument

class PaddingValuesSymmetry(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Symmetric Compose padding arguments should use horizontal and vertical parameters.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitKtFile(file: KtFile) {
        hasComposePaddingImport = file.importDirectives.any { directive ->
            val importPath = directive.importPath?.pathStr ?: return@any false
            importPath == COMPOSE_PADDING_IMPORT || importPath == "$COMPOSE_PADDING_PACKAGE.*"
        }
        hasComposePaddingValuesImport = file.importDirectives.any { directive ->
            val importPath = directive.importPath?.pathStr ?: return@any false
            importPath == COMPOSE_PADDING_VALUES_IMPORT || importPath == "$COMPOSE_PADDING_PACKAGE.*"
        }
        super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val isPaddingValuesCall = hasComposePaddingValuesImport &&
            expression.calleeExpression?.text == PADDING_VALUES_FUNCTION_NAME

        val isModifierPaddingCall = hasComposePaddingImport &&
            expression.calleeExpression?.text == PADDING_FUNCTION_NAME &&
            expression.hasExplicitModifierReceiver()

        if (!isPaddingValuesCall && !isModifierPaddingCall) {
            return
        }

        val namedArguments = expression.valueArguments.associateByName()
        val suggestion = namedArguments.toSuggestionMessage() ?: return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = suggestion,
            ),
        )
    }

    private var hasComposePaddingImport: Boolean = false
    private var hasComposePaddingValuesImport: Boolean = false

    private fun List<ValueArgument>.associateByName(): Map<String, ValueArgument> =
        mapNotNull { argument ->
            val argumentName = argument.getArgumentName()?.asName?.identifier ?: return@mapNotNull null
            argumentName to argument
        }.toMap()

    private fun KtCallExpression.hasExplicitModifierReceiver(): Boolean {
        val qualifiedExpression = parent as? KtDotQualifiedExpression
        return qualifiedExpression?.selectorExpression === this &&
            qualifiedExpression.receiverExpression.rootReceiverName() == MODIFIER_RECEIVER_NAME
    }

    private fun org.jetbrains.kotlin.psi.KtExpression.rootReceiverName(): String? = when (this) {
        is KtNameReferenceExpression -> getReferencedName()
        is KtQualifiedExpression -> receiverExpression.rootReceiverName()
        else -> null
    }

    private fun Map<String, ValueArgument>.toSuggestionMessage(): String? {
        val start = get("start")?.getArgumentExpression()?.text
        val top = get("top")?.getArgumentExpression()?.text
        val end = get("end")?.getArgumentExpression()?.text
        val bottom = get("bottom")?.getArgumentExpression()?.text

        val hasOnlyHorizontalPair = keys == setOf("start", "end") && start != null && start == end
        val hasOnlyVerticalPair = keys == setOf("top", "bottom") && top != null && top == bottom
        val hasBothPairs = keys == setOf("start", "top", "end", "bottom") &&
            start != null &&
            top != null &&
            start == end &&
            top == bottom

        return when {
            hasBothPairs ->
                "Symmetric Compose padding arguments can be replaced with horizontal = $start and vertical = $top."

            hasOnlyHorizontalPair ->
                "Symmetric Compose padding arguments can be replaced with horizontal = $start."

            hasOnlyVerticalPair ->
                "Symmetric Compose padding arguments can be replaced with vertical = $top."

            else -> null
        }
    }

    private companion object {
        const val MODIFIER_RECEIVER_NAME = "Modifier"
        const val PADDING_FUNCTION_NAME = "padding"
        const val PADDING_VALUES_FUNCTION_NAME = "PaddingValues"
        const val COMPOSE_PADDING_PACKAGE = "androidx.compose.foundation.layout"
        const val COMPOSE_PADDING_IMPORT = "$COMPOSE_PADDING_PACKAGE.$PADDING_FUNCTION_NAME"
        const val COMPOSE_PADDING_VALUES_IMPORT = "$COMPOSE_PADDING_PACKAGE.$PADDING_VALUES_FUNCTION_NAME"
    }
}
