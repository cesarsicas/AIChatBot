package br.com.cesarsicas.aichatbot.presentation.theme

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import br.com.cesarsicas.aichatbot.R
import br.com.cesarsicas.aichatbot.domain.model.Character
import br.com.cesarsicas.aichatbot.ui.theme.MarcusAccent
import br.com.cesarsicas.aichatbot.ui.theme.MarcusDeep
import br.com.cesarsicas.aichatbot.ui.theme.MarcusTint
import br.com.cesarsicas.aichatbot.ui.theme.SherlockAccent
import br.com.cesarsicas.aichatbot.ui.theme.SherlockDeep
import br.com.cesarsicas.aichatbot.ui.theme.SherlockTint

data class CharacterTheme(
    val accent: Color,
    val deep: Color,
    val tint: Color,
    val era: String,
    val subtitle: String,
    val blurb: String,
    val strengths: List<String>,
    val docs: Int,
    val chunks: Int,
    val suggestedPrompts: List<String>,
    val sourceWorks: List<String>,
    @DrawableRes val avatarRes: Int,
    @DrawableRes val bustRes: Int,
    @DrawableRes val fullRes: Int,
)

val Character.theme: CharacterTheme
    get() = when (this) {
        Character.SHERLOCK_HOLMES -> CharacterTheme(
            accent    = SherlockAccent,
            deep      = SherlockDeep,
            tint      = SherlockTint,
            era       = "London · 1891",
            subtitle  = "Consulting Detective",
            blurb     = "The world's first and foremost consulting detective. Reasons from the " +
                "smallest of traces — a frayed cuff, a speck of clay — to the whole of a hidden truth.",
            strengths = listOf("Deductive reasoning", "Forensic observation", "Disguise & deception", "Chemistry"),
            docs      = 4,
            chunks    = 1840,
            suggestedPrompts = listOf(
                "Read my character from my appearance",
                "Tell me of an unsolved case",
                "How do you reach a deduction?",
            ),
            sourceWorks = listOf(
                "A Scandal in Bohemia",
                "The Adventure of the Dancing Men",
                "The Sign of Four",
            ),
            avatarRes = R.drawable.sherlock_avatar,
            bustRes   = R.drawable.sherlock_bust,
            fullRes   = R.drawable.sherlock_full,
        )
        Character.MARCUS_AURELIUS -> CharacterTheme(
            accent    = MarcusAccent,
            deep      = MarcusDeep,
            tint      = MarcusTint,
            era       = "Rome · 170 AD",
            subtitle  = "Emperor & Stoic",
            blurb     = "Last of the Five Good Emperors and a steadfast Stoic. Counsels on duty, " +
                "mortality and the discipline of the mind from the pages of his private Meditations.",
            strengths = listOf("Stoic philosophy", "Ethics & virtue", "Statesmanship", "Self-reflection"),
            docs      = 3,
            chunks    = 1210,
            suggestedPrompts = listOf(
                "How should I meet adversity?",
                "What did you write at dawn?",
                "Counsel me on anger",
            ),
            sourceWorks = listOf(
                "Meditations · Book V",
                "Meditations · Book VIII",
                "Letters to Fronto",
            ),
            avatarRes = R.drawable.marcus_avatar,
            bustRes   = R.drawable.marcus_bust,
            fullRes   = R.drawable.marcus_full,
        )
    }
