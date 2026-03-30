package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNamedFunction

class ComposableFileOptIn(config: Config): Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Experimental annotations for @Composable functions should be declared only at file level.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (!function.isComposable()) {
            return
        }

        val experimentalAnnotations = function.annotationEntries.filter { annotation ->
            annotation.isExperimentalForComposable()
        }
        experimentalAnnotations.forEach { annotation ->
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(annotation),
                    message = "Move experimental annotations from @Composable declarations to @file:OptIn(...).",
                ),
            )
        }
    }

    private fun KtNamedFunction.isComposable(): Boolean =
        annotationEntries.any { annotation ->
            annotation.shortName?.asString() == COMPOSABLE_ANNOTATION_NAME
        }

    private fun KtAnnotationEntry.isExperimentalForComposable(): Boolean {
        val annotationName = shortName?.asString() ?: return false
        return annotationName == OPT_IN_ANNOTATION_NAME ||
            (annotationName.startsWith(EXPERIMENTAL_ANNOTATION_PREFIX) && annotationName != COMPOSABLE_ANNOTATION_NAME)
    }

    private companion object {
        const val COMPOSABLE_ANNOTATION_NAME = "Composable"
        const val OPT_IN_ANNOTATION_NAME = "OptIn"
        const val EXPERIMENTAL_ANNOTATION_PREFIX = "Experimental"
    }
}
