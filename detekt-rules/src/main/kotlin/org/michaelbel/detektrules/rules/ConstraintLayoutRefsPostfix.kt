package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty

class ConstraintLayoutRefsPostfix(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "ConstraintLayout refs created via createRef/createRefs must use the Ref postfix.",
        debt = Debt.FIVE_MINS
    )

    override fun visitDestructuringDeclaration(destructuringDeclaration: KtDestructuringDeclaration) {
        super.visitDestructuringDeclaration(destructuringDeclaration)

        if (destructuringDeclaration.initializer?.calleeName() != CREATE_REFS_FUNCTION_NAME) return

        destructuringDeclaration.entries
            .filter { entry ->
                val name = entry.name
                name != null && name != UNUSED_DESTRUCTURING_ENTRY_NAME && !name.endsWith(REF_POSTFIX)
            }
            .forEach { entry ->
                val name = entry.name ?: return@forEach
                reportMissingPostfix(Entity.from(entry), name)
            }
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)

        if (property.isLocal.not()) return
        if (property.initializer?.calleeName() != CREATE_REF_FUNCTION_NAME) return

        val name = property.name
        if (name != null && !name.endsWith(REF_POSTFIX)) {
            reportMissingPostfix(Entity.from(property), name)
        }
    }

    private fun reportMissingPostfix(entity: Entity, name: String) {
        report(
            CodeSmell(
                issue = issue,
                entity = entity,
                message = "ConstraintLayout ref \"$name\" must end with \"$REF_POSTFIX\"."
            )
        )
    }

    private fun KtExpression.calleeName(): String? {
        return when (this) {
            is KtCallExpression -> calleeExpression?.text
            is KtDotQualifiedExpression -> (selectorExpression as? KtCallExpression)?.calleeExpression?.text
            else -> null
        }
    }

    private companion object {
        const val CREATE_REF_FUNCTION_NAME = "createRef"
        const val CREATE_REFS_FUNCTION_NAME = "createRefs"
        const val REF_POSTFIX = "Ref"
        const val UNUSED_DESTRUCTURING_ENTRY_NAME = "_"
    }
}
