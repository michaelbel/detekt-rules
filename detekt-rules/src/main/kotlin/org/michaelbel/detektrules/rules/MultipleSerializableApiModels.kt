package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtParameter

class MultipleSerializableApiModels(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "A file should contain only one API data model annotated with @Serializable and @SerialName.",
        debt = Debt.FIVE_MINS
    )

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)

        val apiModels = file.declarations
            .filterIsInstance<KtClass>()
            .filter { klass -> klass.isSerializableApiModel() }

        if (apiModels.size < MIN_API_MODELS_IN_FILE) return

        apiModels.drop(1).forEach { klass ->
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(klass),
                    message = "Move API model '${klass.name}' to a separate file."
                )
            )
        }
    }

    private fun KtClass.isSerializableApiModel(): Boolean =
        isData() &&
            annotationEntries.any { annotation -> annotation.shortName?.asString() == SERIALIZABLE_ANNOTATION } &&
            primaryConstructorParameters.any { parameter -> parameter.hasSerialNameAnnotation() }

    private fun KtParameter.hasSerialNameAnnotation(): Boolean =
        annotationEntries.any { annotation -> annotation.shortName?.asString() == SERIAL_NAME_ANNOTATION }

    private companion object {
        const val MIN_API_MODELS_IN_FILE = 2
        const val SERIALIZABLE_ANNOTATION = "Serializable"
        const val SERIAL_NAME_ANNOTATION = "SerialName"
    }
}
