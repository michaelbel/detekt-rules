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
import org.jetbrains.kotlin.psi.KtValueArgument

class ModifierPaddingArgumentOrder(config: Config): Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Named arguments in Compose padding APIs must match the Compose API order and must not use redundant 0.dp values.",
        debt = Debt.FIVE_MINS
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

        val namedArguments = expression.valueArguments.mapNotNull { argument ->
            argument.getArgumentName()?.asName?.identifier
        }

        val expectedArgumentOrder = supportedArgumentOrders.firstOrNull { argumentOrder ->
            namedArguments.all(argumentOrder::contains)
        }

        val isModifierPaddingCall = hasComposePaddingImport &&
            expression.calleeExpression?.text == PADDING_FUNCTION_NAME &&
            expression.hasExplicitModifierReceiver()

        val isPaddingValuesCall = hasComposePaddingValuesImport &&
            expression.calleeExpression?.text == PADDING_VALUES_FUNCTION_NAME

        val shouldReport = (isModifierPaddingCall || isPaddingValuesCall) &&
            namedArguments.size >= 2 &&
            expectedArgumentOrder != null &&
            namedArguments.map(expectedArgumentOrder::indexOf).let { actualOrder ->
                actualOrder != actualOrder.sorted()
            }

        if (shouldReport) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(expression),
                    message = "Compose padding arguments must follow the Compose API order."
                )
            )
        }

        val zeroDpArguments = expression.valueArguments.filter { argument ->
            argument.getArgumentName() != null && argument.isZeroDp()
        }

        val shouldReportZeroDp = (isModifierPaddingCall || isPaddingValuesCall) &&
            zeroDpArguments.isNotEmpty()

        if (shouldReportZeroDp) {
            val redundantArgs = zeroDpArguments.joinToString { "${it.getArgumentName()?.asName?.identifier} = 0.dp" }
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(expression),
                    message = "Redundant 0.dp padding arguments can be omitted: $redundantArgs. " +
                        "Unspecified padding dimensions default to 0.dp."
                )
            )
        }
    }

    private var hasComposePaddingImport: Boolean = false
    private var hasComposePaddingValuesImport: Boolean = false

    private fun KtValueArgument.isZeroDp(): Boolean {
        val expression = getArgumentExpression() as? KtDotQualifiedExpression ?: return false
        return expression.receiverExpression.text == "0" && expression.selectorExpression?.text == "dp"
    }

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

    private companion object {
        const val MODIFIER_RECEIVER_NAME = "Modifier"
        const val PADDING_FUNCTION_NAME = "padding"
        const val PADDING_VALUES_FUNCTION_NAME = "PaddingValues"
        const val COMPOSE_PADDING_PACKAGE = "androidx.compose.foundation.layout"
        const val COMPOSE_PADDING_IMPORT = "$COMPOSE_PADDING_PACKAGE.$PADDING_FUNCTION_NAME"
        const val COMPOSE_PADDING_VALUES_IMPORT = "$COMPOSE_PADDING_PACKAGE.$PADDING_VALUES_FUNCTION_NAME"

        val supportedArgumentOrders = listOf(
            listOf("start", "top", "end", "bottom"),
            listOf("horizontal", "vertical")
        )
    }
}
