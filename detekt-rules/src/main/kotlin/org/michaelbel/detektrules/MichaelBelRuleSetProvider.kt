package org.michaelbel.detektrules

import org.michaelbel.detektrules.rules.ModifierPaddingArgumentOrder
import org.michaelbel.detektrules.rules.PaddingValuesSymmetry
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

const val RULE_SET_ID = "michaelbel"

class MichaelBelRuleSetProvider : RuleSetProvider {

    override val ruleSetId: String = RULE_SET_ID

    override fun instance(config: Config): RuleSet =
        RuleSet(
            id = ruleSetId,
            rules = listOf(
                ModifierPaddingArgumentOrder(config),
                PaddingValuesSymmetry(config),
            ),
        )
}
