package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType

class MissingTransactionOnRelation(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Warning,
        description = "Methods in @Dao interfaces returning Pojo types should be annotated with @Transaction.",
        debt = Debt.FIVE_MINS
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        if (!klass.isInterface()) return
        if (!klass.hasDaoAnnotation()) return

        klass.declarations.filterIsInstance<KtNamedFunction>().forEach { function ->
            if (function.hasTransactionAnnotation()) return@forEach

            val returnTypeRef = function.typeReference ?: return@forEach
            val pojoTypeName = returnTypeRef.findPojoTypeName() ?: return@forEach

            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(function),
                    message = "Method '${function.name}' returns '$pojoTypeName' (Pojo type). Add @Transaction to ensure consistent reads."
                )
            )
        }
    }

    private fun KtClass.hasDaoAnnotation(): Boolean =
        annotationEntries.any { it.shortName?.asString() == DAO_ANNOTATION }

    private fun KtNamedFunction.hasTransactionAnnotation(): Boolean =
        annotationEntries.any { it.shortName?.asString() == TRANSACTION_ANNOTATION }

    private fun KtTypeReference.findPojoTypeName(): String? =
        findPojoTypeNameFrom(typeElement)

    private fun findPojoTypeNameFrom(typeElement: KtTypeElement?): String? {
        if (typeElement !is KtUserType) return null
        val name = typeElement.referencedName
        if (name != null && name.endsWith(POJO_POSTFIX, ignoreCase = true)) return name
        return typeElement.typeArguments
            .firstNotNullOfOrNull { findPojoTypeNameFrom(it.typeReference?.typeElement) }
    }

    private companion object {
        const val DAO_ANNOTATION = "Dao"
        const val TRANSACTION_ANNOTATION = "Transaction"
        const val POJO_POSTFIX = "Pojo"
    }
}
