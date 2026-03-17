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
package org.isoron.uhabits.core.models.sqlite.records

import org.isoron.uhabits.core.database.Column
import org.isoron.uhabits.core.database.Table
import org.isoron.uhabits.core.models.Reminder
import org.isoron.uhabits.core.models.WeekdayList

@Table(name = "Reminders", id = "id")
class ReminderRecord {
    @field:Column
    var id: Long? = null

    @field:Column
    var habit: Long? = null

    @field:Column
    var hour: Int? = null

    @field:Column
    var min: Int? = null

    @field:Column
    var days: Int? = null

    fun copyFrom(habitId: Long, reminder: Reminder) {
        this.habit = habitId
        this.hour = reminder.hour
        this.min = reminder.minute
        this.days = reminder.days.toInteger()
    }

    fun toReminder(): Reminder {
        return Reminder(hour!!, min!!, WeekdayList(days!!))
    }
}
