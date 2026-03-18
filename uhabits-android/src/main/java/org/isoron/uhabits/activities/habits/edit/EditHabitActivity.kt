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

package org.isoron.uhabits.activities.habits.edit

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.android.datetimepicker.time.RadialPickerLayout
import com.android.datetimepicker.time.TimePickerDialog
import org.isoron.platform.gui.toInt
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.R
import org.isoron.uhabits.activities.AndroidThemeSwitcher
import org.isoron.uhabits.activities.common.dialogs.ColorPickerDialogFactory
import org.isoron.uhabits.activities.common.dialogs.FrequencyPickerDialog
import org.isoron.uhabits.core.commands.CommandRunner
import org.isoron.uhabits.core.commands.CreateHabitCommand
import org.isoron.uhabits.core.commands.EditHabitCommand
import org.isoron.uhabits.core.models.Frequency
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.HabitType
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.models.Reminder
import org.isoron.uhabits.core.models.WeekdayList
import org.isoron.uhabits.databinding.ActivityEditHabitBinding
import org.isoron.uhabits.utils.applyRootViewInsets
import org.isoron.uhabits.utils.applyToolbarInsets
import org.isoron.uhabits.utils.dismissCurrentAndShow
import org.isoron.uhabits.utils.formatTime

fun formatFrequency(freqNum: Int, freqDen: Int, resources: Resources) = when {
    freqNum == 1 && (freqDen == 30 || freqDen == 31) -> resources.getString(R.string.every_month)
    freqDen == 30 || freqDen == 31 -> resources.getString(R.string.x_times_per_month, freqNum)
    freqNum == 1 && freqDen == 1 -> resources.getString(R.string.every_day)
    freqNum == 1 && freqDen == 7 -> resources.getString(R.string.every_week)
    freqNum == 1 && freqDen > 1 -> resources.getString(R.string.every_x_days, freqDen)
    freqDen == 7 -> resources.getString(R.string.x_times_per_week, freqNum)
    else -> resources.getString(R.string.x_times_per_y_days, freqNum, freqDen)
}

class EditHabitActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: AndroidThemeSwitcher
    private lateinit var binding: ActivityEditHabitBinding
    private lateinit var commandRunner: CommandRunner

    var habitId = -1L
    lateinit var habitType: HabitType
    var unit = ""
    var color = PaletteColor(11)
    var androidColor = 0
    var freqNum = 1
    var freqDen = 1
    var targetType = NumericalHabitType.AT_LEAST

    val reminders: MutableList<Reminder> = mutableListOf()

    private val dayNames = listOf("Sa", "Su", "M", "T", "W", "Th", "F")

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val component = (application as HabitsApplication).component
        themeSwitcher = AndroidThemeSwitcher(this, component.preferences)
        themeSwitcher.apply()

        binding = ActivityEditHabitBinding.inflate(layoutInflater)
        binding.root.applyRootViewInsets()
        binding.toolbar.applyToolbarInsets()
        setContentView(binding.root)

        if (intent.hasExtra("habitId")) {
            binding.toolbar.title = getString(R.string.edit_habit)
            habitId = intent.getLongExtra("habitId", -1)
            val habit = component.habitList.getById(habitId)!!
            habitType = habit.type
            color = habit.color
            freqNum = habit.frequency.numerator
            freqDen = habit.frequency.denominator
            targetType = habit.targetType
            reminders.addAll(habit.reminders)
            binding.nameInput.setText(habit.name)
            binding.questionInput.setText(habit.question)
            binding.notesInput.setText(habit.description)
            binding.unitInput.setText(habit.unit)
            binding.targetInput.setText(habit.targetValue.toString())
        } else {
            habitType = HabitType.fromInt(intent.getIntExtra("habitType", HabitType.YES_NO.value))
        }

        if (state != null) {
            habitId = state.getLong("habitId")
            habitType = HabitType.fromInt(state.getInt("habitType"))
            color = PaletteColor(state.getInt("paletteColor"))
            freqNum = state.getInt("freqNum")
            freqDen = state.getInt("freqDen")
        }

        updateColors()

        when (habitType) {
            HabitType.YES_NO -> {
                binding.unitOuterBox.visibility = View.GONE
                binding.targetOuterBox.visibility = View.GONE
                binding.targetTypeOuterBox.visibility = View.GONE
            }
            HabitType.NUMERICAL -> {
                binding.nameInput.hint = getString(R.string.measurable_short_example)
                binding.questionInput.hint = getString(R.string.measurable_question_example)
                binding.frequencyOuterBox.visibility = View.GONE
            }
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.elevation = 10.0f

        val colorPickerDialogFactory = ColorPickerDialogFactory(this)
        binding.colorButton.setOnClickListener {
            val picker = colorPickerDialogFactory.create(color, themeSwitcher.currentTheme)
            picker.setListener { paletteColor ->
                this.color = paletteColor
                updateColors()
            }
            picker.dismissCurrentAndShow(supportFragmentManager, "colorPicker")
        }

        populateFrequency()
        binding.booleanFrequencyPicker.setOnClickListener {
            val picker = FrequencyPickerDialog(freqNum, freqDen)
            picker.onFrequencyPicked = { num, den ->
                freqNum = num
                freqDen = den
                populateFrequency()
            }
            picker.dismissCurrentAndShow(supportFragmentManager, "frequencyPicker")
        }

        populateTargetType()
        binding.targetTypePicker.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item)
            arrayAdapter.add(getString(R.string.target_type_at_least))
            arrayAdapter.add(getString(R.string.target_type_at_most))
            builder.setAdapter(arrayAdapter) { dialog, which ->
                targetType = when (which) {
                    0 -> NumericalHabitType.AT_LEAST
                    else -> NumericalHabitType.AT_MOST
                }
                populateTargetType()
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.dismissCurrentAndShow()
        }

        binding.numericalFrequencyPicker.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item)
            arrayAdapter.add(getString(R.string.every_day))
            arrayAdapter.add(getString(R.string.every_week))
            arrayAdapter.add(getString(R.string.every_month))
            builder.setAdapter(arrayAdapter) { dialog, which ->
                freqDen = when (which) {
                    1 -> 7
                    2 -> 30
                    else -> 1
                }
                populateFrequency()
                dialog.dismiss()
            }
            builder.show()
        }

        populateReminders()

        binding.addReminderButton.setOnClickListener {
            showTimePickerForNewReminder()
        }

        binding.buttonSave.setOnClickListener {
            if (validate()) save()
        }

        for (fragment in supportFragmentManager.fragments) {
            (fragment as DialogFragment).dismiss()
        }
    }

    private fun showTimePickerForNewReminder() {
        val is24HourMode = DateFormat.is24HourFormat(this)
        val dialog = TimePickerDialog.newInstance(
            object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int) {
                    val reminder = Reminder(hourOfDay, minute, WeekdayList.EVERY_DAY)
                    reminders.add(reminder)
                    populateReminders()
                }
                override fun onTimeCleared(view: RadialPickerLayout?) {}
            },
            8, 0, is24HourMode, androidColor
        )
        dialog.dismissCurrentAndShow(supportFragmentManager, "timePicker_new")
    }

    private fun populateReminders() {
        binding.remindersContainer.removeAllViews()
        reminders.forEachIndexed { index, reminder ->

            // Main row
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8, 0, 8)
            }

            // Time row
            val timeRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Time text — click to edit
            val timeText = TextView(this).apply {
                text = formatTime(this@EditHabitActivity, reminder.hour, reminder.minute)
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(48, 16, 16, 16)
                setTextColor(currentThemeTextColor())
                setOnClickListener {
                    val is24HourMode = DateFormat.is24HourFormat(this@EditHabitActivity)
                    val dialog = TimePickerDialog.newInstance(
                        object : TimePickerDialog.OnTimeSetListener {
                            override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int) {
                                reminders[index] = Reminder(hourOfDay, minute, reminders[index].days)
                                populateReminders()
                            }
                            override fun onTimeCleared(view: RadialPickerLayout?) {}
                        },
                        reminder.hour, reminder.minute, is24HourMode, androidColor
                    )
                    dialog.dismissCurrentAndShow(supportFragmentManager, "timePicker_$index")
                }
            }

            // Delete button
            val deleteBtn = TextView(this).apply {
                text = "✕"
                textSize = 16f
                setPadding(16, 16, 48, 16)
                setTextColor(currentThemeTextColor())
                setOnClickListener {
                    reminders.removeAt(index)
                    populateReminders()
                }
            }

            timeRow.addView(timeText)
            timeRow.addView(deleteBtn)

            // Days row — S M T W T F S toggle buttons
            val daysRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(48, 4, 48, 8)
            }

            val daysArray = reminder.days.toArray()

            for (dayIndex in 0..6) {
                val dayBtn = TextView(this).apply {
                    text = dayNames[dayIndex]
                    textSize = 12f
                    gravity = Gravity.CENTER
                    val size = (32 * resources.displayMetrics.density).toInt()
                    val margin = (4 * resources.displayMetrics.density).toInt()
                    layoutParams = LinearLayout.LayoutParams(size, size).apply {
                        setMargins(margin, 0, margin, 0)
                    }
                    isSelected = daysArray[dayIndex]
                    updateDayButton(this, daysArray[dayIndex])
                    setOnClickListener {
                        val newDays = reminders[index].days.toArray().clone()
                        newDays[dayIndex] = !newDays[dayIndex]
                        // At least one day must be selected
                        if (newDays.any { it }) {
                            reminders[index] = Reminder(
                                reminders[index].hour,
                                reminders[index].minute,
                                WeekdayList(newDays)
                            )
                            populateReminders()
                        }
                    }
                }
                daysRow.addView(dayBtn)
            }

            row.addView(timeRow)
            row.addView(daysRow)
            binding.remindersContainer.addView(row)

            // Divider
            if (index < reminders.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                    ).apply { setMargins(48, 4, 48, 4) }
                    setBackgroundColor(currentThemeDividerColor())
                }
                binding.remindersContainer.addView(divider)
            }
        }
    }

    private fun updateDayButton(btn: TextView, selected: Boolean) {
        if (selected) {
            btn.setBackgroundColor(androidColor)
            btn.setTextColor(0xFFFFFFFF.toInt())
            btn.setTypeface(null, Typeface.BOLD)
        } else {
            btn.setBackgroundColor(0x22888888.toInt())
            btn.setTextColor(currentThemeTextColor())
            btn.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun currentThemeTextColor(): Int {
        val attrs = intArrayOf(android.R.attr.textColorPrimary)
        val ta = obtainStyledAttributes(attrs)
        val color = ta.getColor(0, 0xFF000000.toInt())
        ta.recycle()
        return color
    }

    private fun currentThemeDividerColor(): Int {
        val attrs = intArrayOf(android.R.attr.listDivider)
        val ta = obtainStyledAttributes(attrs)
        val color = ta.getColor(0, 0x22000000.toInt())
        ta.recycle()
        return color
    }

    private fun save() {
        val component = (application as HabitsApplication).component
        val habit = component.modelFactory.buildHabit()

        var original: Habit? = null
        if (habitId >= 0) {
            original = component.habitList.getById(habitId)!!
            habit.copyFrom(original)
        }

        habit.name = binding.nameInput.text.trim().toString()
        habit.question = binding.questionInput.text.trim().toString()
        habit.description = binding.notesInput.text.trim().toString()
        habit.color = color
        habit.reminders.clear()
        habit.reminders.addAll(reminders)

        habit.frequency = Frequency(freqNum, freqDen)
        if (habitType == HabitType.NUMERICAL) {
            habit.targetValue = binding.targetInput.text.toString().toDouble()
            habit.targetType = targetType
            habit.unit = binding.unitInput.text.trim().toString()
        }
        habit.type = habitType

        val command = if (habitId >= 0) {
            EditHabitCommand(component.habitList, habitId, habit)
        } else {
            CreateHabitCommand(component.modelFactory, component.habitList, habit)
        }
        component.commandRunner.run(command)
        finish()
    }

    private fun validate(): Boolean {
        var isValid = true
        if (binding.nameInput.text.isEmpty()) {
            binding.nameInput.error = getFormattedValidationError(R.string.validation_cannot_be_blank)
            isValid = false
        }
        if (habitType == HabitType.NUMERICAL) {
            if (binding.targetInput.text.isEmpty()) {
                binding.targetInput.error = getString(R.string.validation_cannot_be_blank)
                isValid = false
            }
        }
        return isValid
    }

    @SuppressLint("StringFormatMatches")
    private fun populateFrequency() {
        binding.booleanFrequencyPicker.text = formatFrequency(freqNum, freqDen, resources)
        binding.numericalFrequencyPicker.text = when (freqDen) {
            1 -> getString(R.string.every_day)
            7 -> getString(R.string.every_week)
            30 -> getString(R.string.every_month)
            else -> "$freqNum/$freqDen"
        }
    }

    private fun populateTargetType() {
        binding.targetTypePicker.text = when (targetType) {
            NumericalHabitType.AT_MOST -> getString(R.string.target_type_at_most)
            else -> getString(R.string.target_type_at_least)
        }
    }

    private fun updateColors() {
        androidColor = themeSwitcher.currentTheme.color(color).toInt()
        binding.colorButton.backgroundTintList = ColorStateList.valueOf(androidColor)
        if (!themeSwitcher.isNightMode) {
            window.statusBarColor = androidColor
            binding.toolbar.setBackgroundColor(androidColor)
        }
    }

    private fun getFormattedValidationError(@StringRes resId: Int): Spanned {
        val html = "<font color=#FFFFFF>${getString(resId)}</font>"
        return Html.fromHtml(html)
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        with(state) {
            putLong("habitId", habitId)
            putInt("habitType", habitType.value)
            putInt("paletteColor", color.paletteIndex)
            putInt("androidColor", androidColor)
            putInt("freqNum", freqNum)
            putInt("freqDen", freqDen)
        }
    }
}
