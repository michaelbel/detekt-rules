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

/**
 * Reports size modifiers (e.g. `.size()`, `.width()`, `.height()`) used together with
 * `.constrainAs()` in the same modifier chain. When a composable is positioned via
 * `constrainAs`, its size should be declared inside the `constrainAs` lambda using the
 * `Dimension` API so that ConstraintLayout can manage both placement and sizing in one place.
 *
 * **Non-compliant:**
 * ```kotlin
 * Box(
 *     modifier = Modifier
 *         .size(width = 50.dp, height = 58.dp)
 *         .constrainAs(ref) {
 *             start.linkTo(parent.start)
 *             top.linkTo(parent.top)
 *         }
 * )
 * ```
 *
 * **Compliant:**
 * ```kotlin
 * Box(
 *     modifier = Modifier
 *         .constrainAs(ref) {
 *             width = Dimension.value(50.dp)
 *             height = Dimension.value(58.dp)
 *             start.linkTo(parent.start)
 *             top.linkTo(parent.top)
 *         }
 * )
 * ```
 *
 * Size modifier → Dimension API equivalents:
 * - `.size(dp)` / `.width(dp)` / `.height(dp)` → `Dimension.value(dp)`
 * - `.fillMaxWidth()` / `.fillMaxHeight()` / `.fillMaxSize()` → `Dimension.fillToConstraints`
 * - `.wrapContentWidth()` / `.wrapContentHeight()` → `Dimension.wrapContent`
 * - `.requiredWidth(dp)` / `.requiredHeight(dp)` / `.requiredSize(dp)` → `Dimension.value(dp)`
 */
class SizeModifierWithConstrainAs(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Size modifiers should be declared inside the constrainAs block using the Dimension API.",
        debt = Debt.FIVE_MINS
    )

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        if (expression.parent is KtDotQualifiedExpression) return

        val calls = collectCallsInChain(expression)
        val constrainAsCall = calls.firstOrNull {
            it.calleeExpression?.text == CONSTRAIN_AS_FUNCTION_NAME
        } ?: return

        val sizeCallNames = calls
            .filter { it != constrainAsCall }
            .mapNotNull { it.calleeExpression?.text }
            .filter { it in SIZE_MODIFIER_NAMES }

        if (sizeCallNames.isNotEmpty()) {
            val dimensionHints = sizeCallNames.joinToString { name ->
                DIMENSION_HINTS[name] ?: "Dimension API"
            }
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(constrainAsCall),
                    message = "Size modifier(s) [${sizeCallNames.joinToString()}] found alongside constrainAs. " +
                        "Move the size into the constrainAs block using $dimensionHints."
                )
            )
        }
    }

    private fun collectCallsInChain(expr: KtExpression): List<KtCallExpression> {
        return when (expr) {
            is KtDotQualifiedExpression -> {
                val fromReceiver = collectCallsInChain(expr.receiverExpression)
                val fromSelector = (expr.selectorExpression as? KtCallExpression)
                    ?.let { listOf(it) } ?: emptyList()
                fromReceiver + fromSelector
            }
            is KtCallExpression -> listOf(expr)
            else -> emptyList()
        }
    }

    private companion object {
        const val CONSTRAIN_AS_FUNCTION_NAME = "constrainAs"

        val SIZE_MODIFIER_NAMES = setOf(
            "size",
            "width",
            "height",
            "fillMaxWidth",
            "fillMaxHeight",
            "fillMaxSize",
            "wrapContentWidth",
            "wrapContentHeight",
            "requiredWidth",
            "requiredHeight",
            "requiredSize"
        )

        val DIMENSION_HINTS = mapOf(
            "size" to "Dimension.value(...)",
            "width" to "Dimension.value(...)",
            "height" to "Dimension.value(...)",
            "requiredWidth" to "Dimension.value(...)",
            "requiredHeight" to "Dimension.value(...)",
            "requiredSize" to "Dimension.value(...)",
            "fillMaxWidth" to "Dimension.fillToConstraints",
            "fillMaxHeight" to "Dimension.fillToConstraints",
            "fillMaxSize" to "Dimension.fillToConstraints",
            "wrapContentWidth" to "Dimension.wrapContent",
            "wrapContentHeight" to "Dimension.wrapContent"
        )
    }
}
