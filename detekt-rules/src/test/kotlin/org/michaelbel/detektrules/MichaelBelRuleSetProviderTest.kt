package org.michaelbel.detektrules

import io.gitlab.arturbosch.detekt.api.Config
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MichaelBelRuleSetProviderTest {

    @Test
    fun `provides expected ruleset`() {
        val ruleSet = MichaelBelRuleSetProvider().instance(Config.empty)

        assertEquals(RULE_SET_ID, ruleSet.id)
        assertEquals(
            listOf(
                "ConstrainAsOperatorOrder",
                "ComposableFileOptIn",
                "ModifierPaddingArgumentOrder",
                "NoSpaceBeforeInheritanceColon",
                "PaddingValuesSymmetry",
                "TextAlignInTextStyle",
                "UseLastIndexInsteadOfSizeMinusOne",
                "SizeModifierWithConstrainAs",
            ),
            ruleSet.rules.map { it.ruleId },
        )
    }
}
