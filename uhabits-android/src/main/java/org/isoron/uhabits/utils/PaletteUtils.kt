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

package org.isoron.uhabits.utils

import android.content.Context
import android.graphics.Color
import org.isoron.uhabits.core.models.PaletteColor

object PaletteUtils {
    @JvmStatic
    fun getAndroidTestColor(index: Int) = PaletteColor(index).toFixedAndroidColor()
}

fun PaletteColor.toFixedAndroidColor(): Int {
    return intArrayOf(
        Color.parseColor("#D32F2F"), //  0
        Color.parseColor("#B71C1C"), //  1
        Color.parseColor("#D81B60"), //  2
        Color.parseColor("#880E4F"), //  3
        Color.parseColor("#E64A19"), //  4
        Color.parseColor("#BF360C"), //  5
        Color.parseColor("#F57C00"), //  6
        Color.parseColor("#E65100"), //  7
        Color.parseColor("#FF8F00"), //  8
        Color.parseColor("#F9A825"), //  9
        Color.parseColor("#F57F17"), // 10
        Color.parseColor("#AFB42B"), // 11
        Color.parseColor("#827717"), // 12
        Color.parseColor("#7CB342"), // 13
        Color.parseColor("#558B2F"), // 14
        Color.parseColor("#388E3C"), // 15
        Color.parseColor("#1B5E20"), // 16
        Color.parseColor("#009688"), // 17
        Color.parseColor("#00897B"), // 18
        Color.parseColor("#00796B"), // 19
        Color.parseColor("#004D40"), // 20
        Color.parseColor("#00ACC1"), // 21
        Color.parseColor("#0097A7"), // 22
        Color.parseColor("#006064"), // 23
        Color.parseColor("#039BE5"), // 24
        Color.parseColor("#0277BD"), // 25
        Color.parseColor("#1976D2"), // 26
        Color.parseColor("#0D47A1"), // 27
        Color.parseColor("#3949AB"), // 28
        Color.parseColor("#303F9F"), // 29
        Color.parseColor("#1A237E"), // 30
        Color.parseColor("#5E35B1"), // 31
        Color.parseColor("#4527A0"), // 32
        Color.parseColor("#8E24AA"), // 33
        Color.parseColor("#6A1B9A"), // 34
        Color.parseColor("#4A148C"), // 35
        Color.parseColor("#795548"), // 36
        Color.parseColor("#5D4037"), // 37
        Color.parseColor("#3E2723"), // 38
        Color.parseColor("#607D8B"), // 39
        Color.parseColor("#546E7A"), // 40
        Color.parseColor("#455A64"), // 41
        Color.parseColor("#37474F"), // 42
        Color.parseColor("#9E9E9E"), // 43
        Color.parseColor("#757575"), // 44
        Color.parseColor("#616161"), // 45
        Color.parseColor("#424242"), // 46
        Color.parseColor("#212121"), // 47
        Color.parseColor("#212121")  // 48
    )[paletteIndex]
}

fun Int.toPaletteColor(context: Context): PaletteColor {
    val palette = StyledResources(context).getPalette()
    return PaletteColor(palette.indexOf(this))
}
