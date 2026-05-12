package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtFile

class VectorIconBooleanNamedArguments(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Boolean arguments in vector icon files must use named parameters.",
        debt = Debt.FIVE_MINS
    )

    private var isVectorIconFile: Boolean = false

    override fun visitKtFile(file: KtFile) {
        isVectorIconFile = file.importDirectives.any { directive ->
            directive.importPath?.pathStr == IMAGE_VECTOR_IMPORT
        }
        super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (!isVectorIconFile) return

        expression.valueArguments.forEach { argument ->
            val expr = argument.getArgumentExpression() as? KtConstantExpression ?: return@forEach
            if (expr.text != "true" && expr.text != "false") return@forEach
            if (argument.getArgumentName() == null) {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.from(argument),
                        message = "Boolean argument '${expr.text}' must use a named parameter in vector icon files."
                    )
                )
            }
        }
    }

    private companion object {
        const val IMAGE_VECTOR_IMPORT = "androidx.compose.ui.graphics.vector.ImageVector"
    }
}
