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
package org.isoron.uhabits.core.models

data class PaletteColor(val paletteIndex: Int) {
    fun toCsvColor(): String {
        return arrayOf(
            "#D32F2F", //  0 red_700
            "#B71C1C", //  1 red_900
            "#D81B60", //  2 pink_600
            "#880E4F", //  3 pink_800
            "#E64A19", //  4 deep_orange_700
            "#BF360C", //  5 deep_orange_900
            "#F57C00", //  6 orange_700
            "#E65100", //  7 orange_900
            "#FF8F00", //  8 amber_800
            "#F9A825", //  9 yellow_800
            "#F57F17", // 10 yellow_900
            "#AFB42B", // 11 lime_700
            "#827717", // 12 lime_900
            "#7CB342", // 13 light_green_600
            "#558B2F", // 14 light_green_800
            "#388E3C", // 15 green_700
            "#1B5E20", // 16 green_900
            "#009688", // 17 teal_500
            "#00897B", // 18 teal_600
            "#00796B", // 19 teal_700
            "#004D40", // 20 teal_900
            "#00ACC1", // 21 cyan_600
            "#0097A7", // 22 cyan_700
            "#006064", // 23 cyan_900
            "#039BE5", // 24 light_blue_600
            "#0277BD", // 25 light_blue_800
            "#1976D2", // 26 blue_700
            "#0D47A1", // 27 blue_900
            "#3949AB", // 28 indigo_600
            "#303F9F", // 29 indigo_700
            "#1A237E", // 30 indigo_900
            "#5E35B1", // 31 deep_purple_600
            "#4527A0", // 32 deep_purple_800
            "#8E24AA", // 33 purple_600
            "#6A1B9A", // 34 purple_800
            "#4A148C", // 35 purple_900
            "#795548", // 36 brown_500
            "#5D4037", // 37 brown_700
            "#3E2723", // 38 brown_900
            "#607D8B", // 39 blue_grey_500
            "#546E7A", // 40 blue_grey_600
            "#455A64", // 41 blue_grey_700
            "#37474F", // 42 blue_grey_800
            "#9E9E9E", // 43 grey_500
            "#757575", // 44 grey_600
            "#616161", // 45 grey_700
            "#424242", // 46 grey_800
            "#212121", // 47 grey_900
            "#212121"  // 48 grey_900
        )[paletteIndex]
    }

    fun compareTo(other: PaletteColor): Int {
        return paletteIndex.compareTo(other.paletteIndex)
    }
}
