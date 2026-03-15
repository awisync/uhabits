/*
 * Copyright (C) 2016-2025 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.core.ui.screens.habits.show.views

import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.ui.views.Theme
import org.isoron.uhabits.core.utils.DateUtils
import kotlin.math.min

data class OverviewCardState(
    val color: PaletteColor,
    val scoreMonthDiff: Float,
    val scoreYearDiff: Float,
    val scoreToday: Float,
    val totalCount: Long,
    val theme: Theme,
    val isNumerical: Boolean = false,
    val dayPercentage: Float = 0f,
)

class OverviewCardPresenter {
    companion object {
        fun buildState(habit: Habit, theme: Theme): OverviewCardState {
            val today = DateUtils.getTodayWithOffset()
            val lastMonth = today.minus(30)
            val lastYear = today.minus(365)
            val scores = habit.scores
            val scoreToday = scores[today].value.toFloat()
            val scoreLastMonth = scores[lastMonth].value.toFloat()
            val scoreLastYear = scores[lastYear].value.toFloat()
            val totalCount = habit.originalEntries.getKnown()
                .filter { it.value == Entry.YES_MANUAL }
                .count()
                .toLong()

            val dayPercentage = if (habit.isNumerical) {
                val target = habit.targetValue
                if (target > 0) {
                    val todayEntry = habit.originalEntries.get(today)
                    val actualValue = maxOf(0, todayEntry.value).toDouble() / 1000.0
                    min(1.0, actualValue / target).toFloat()
                } else {
                    0f
                }
            } else {
                0f
            }

            return OverviewCardState(
                color = habit.color,
                scoreToday = scoreToday,
                scoreMonthDiff = scoreToday - scoreLastMonth,
                scoreYearDiff = scoreToday - scoreLastYear,
                totalCount = totalCount,
                theme = theme,
                isNumerical = habit.isNumerical,
                dayPercentage = dayPercentage,
            )
        }
    }
}
