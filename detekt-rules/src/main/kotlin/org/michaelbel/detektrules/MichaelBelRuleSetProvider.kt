package org.michaelbel.detektrules

import org.michaelbel.detektrules.rules.ConstrainAsOperatorOrder
import org.michaelbel.detektrules.rules.ComposableFileOptIn
import org.michaelbel.detektrules.rules.ConstraintLayoutRefsPostfix
import org.michaelbel.detektrules.rules.ModifierPaddingArgumentOrder
import org.michaelbel.detektrules.rules.MultipleSerializableApiModels
import org.michaelbel.detektrules.rules.NoSpaceBeforeInheritanceColon
import org.michaelbel.detektrules.rules.PaddingValuesSymmetry
import org.michaelbel.detektrules.rules.TextAlignInTextStyle
import org.michaelbel.detektrules.rules.SizeModifierWithConstrainAs
import org.michaelbel.detektrules.rules.SnackbarDismissOutsideLaunch
import org.michaelbel.detektrules.rules.UseArrangementSpacedBy
import org.michaelbel.detektrules.rules.UseLastIndexInsteadOfSizeMinusOne
import org.michaelbel.detektrules.rules.UseParentHorizontalPadding
import org.michaelbel.detektrules.rules.MissingTransactionOnRelation
import org.michaelbel.detektrules.rules.PaddingValuesZeroArguments
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

const val RULE_SET_ID = "michaelbel"

class MichaelBelRuleSetProvider: RuleSetProvider {

    override val ruleSetId: String = RULE_SET_ID

    override fun instance(config: Config): RuleSet =
        RuleSet(
            id = ruleSetId,
            rules = listOf(
                ConstrainAsOperatorOrder(config),
                ComposableFileOptIn(config),
                ConstraintLayoutRefsPostfix(config),
                ModifierPaddingArgumentOrder(config),
                MultipleSerializableApiModels(config),
                NoSpaceBeforeInheritanceColon(config),
                PaddingValuesSymmetry(config),
                TextAlignInTextStyle(config),
                UseLastIndexInsteadOfSizeMinusOne(config),
                SnackbarDismissOutsideLaunch(config),
                SizeModifierWithConstrainAs(config),
                UseArrangementSpacedBy(config),
                UseParentHorizontalPadding(config),
                MissingTransactionOnRelation(config),
                PaddingValuesZeroArguments(config)
            )
        )
}
