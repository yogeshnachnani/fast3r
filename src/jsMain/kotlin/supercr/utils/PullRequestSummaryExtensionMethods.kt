package supercr.utils

import git.provider.PullRequestSummary
import kotlinx.css.Color
import supercr.css.EditorThemeColors

fun PullRequestSummary.pickAgeRibbonColor(): Color {
    val ageInHours = this.created_at.ageInHoursFromNow()
    return when {
        ageInHours < 1 -> EditorThemeColors.tokenBlue.withAlpha(0.7)
        ageInHours < 24 -> EditorThemeColors.tokenGreen.withAlpha(0.7)
        ageInHours <= 72 -> EditorThemeColors.tokenOrange.withAlpha(0.7)
        else -> EditorThemeColors.tokenRed.withAlpha(0.7)
    }
}
