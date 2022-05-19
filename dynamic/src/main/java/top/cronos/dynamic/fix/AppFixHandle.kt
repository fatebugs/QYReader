/*
 * This file is part of QYReader.
 * QYReader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QYReader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QYReader.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2020 - 2022 fengyuecanzhu
 */

package top.cronos.dynamic.fix

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import me.fycz.maple.MapleBridge
import me.fycz.maple.MapleUtils
import me.fycz.maple.MethodHook
import top.cronos.dynamic.AppLoadImpl.Companion.spu
import top.cronos.myreader.ui.activity.MainActivity
import top.cronos.myreader.util.utils.AdUtils

/**
 * @author fengyue
 * @date 2022/4/25 21:49
 */
interface AppFixHandle {
    
    fun onFix(key: String): BooleanArray

    fun fixResult(key: String, name: String, success: Boolean) {
        val res = if (success) "Success" else "Failed"
        if (!spu.getBoolean("$key-$name-$res", false)) {
            AdUtils.adRecord(name, "fx$res")
            spu.edit().run {
                putBoolean("$key-$name-$res", true)
                apply()
            }
        }
    }
}