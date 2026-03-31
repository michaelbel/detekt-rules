package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClassOrObject

class NoSpaceBeforeInheritanceColon(config: Config): Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Inheritance and delegation declarations should not contain whitespace before ':'.",
        debt = Debt.FIVE_MINS
    )

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)

        val firstSuperTypeEntry = classOrObject.superTypeListEntries.firstOrNull() ?: return
        val fileText = classOrObject.containingKtFile.text
        var colonOffset = firstSuperTypeEntry.textRange.startOffset - 1

        while (colonOffset >= 0 && fileText[colonOffset].isWhitespace()) {
            colonOffset--
        }

        if (colonOffset < 0 || fileText[colonOffset] != ':') {
            return
        }

        if (colonOffset > 0 && fileText[colonOffset - 1].isWhitespace()) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(firstSuperTypeEntry),
                    message = "Remove whitespace before ':' in inheritance or delegation declarations."
                )
            )
        }
    }
}
