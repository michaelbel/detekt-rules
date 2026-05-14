package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration

class SealedClassCanBeInterface(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Sealed class with no constructor parameters and only object/data object members " +
            "can be replaced with a sealed interface.",
        debt = Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        if (!klass.isSealed() || klass.isInterface()) return

        val hasConstructorParams = klass.primaryConstructor?.valueParameters?.isNotEmpty() == true
        if (hasConstructorParams) return

        val declarations = klass.body?.declarations ?: return
        if (declarations.isEmpty()) return

        val allObjects = declarations.all { it is KtObjectDeclaration }
        if (!allObjects) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(klass),
                message = "Sealed class '${klass.name}' has no constructor parameters and only object/data object members. " +
                    "Consider replacing it with a sealed interface."
            )
        )
    }
}
