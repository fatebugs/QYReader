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

package top.cronos.dynamic

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import me.fycz.maple.MapleBridge
import me.fycz.maple.MapleUtils
import me.fycz.maple.MethodHook
import top.cronos.dynamic.fix.App243Fix
import top.cronos.dynamic.fix.App244Fix
import top.cronos.dynamic.fix.AppFix
import top.cronos.myreader.application.App
import top.cronos.myreader.ui.activity.MainActivity

/**
 * @author fengyue
 * @date 2022/3/29 11:59
 */
class AppLoadImpl : IAppLoader {

    companion object {
        private const val spuName = "QYReader_plugin"
        val spu: SharedPreferences =
            App.getmContext().getSharedPreferences(spuName, Context.MODE_PRIVATE)
    }

    private val fixList = listOf(
        App243Fix::class.java,
        App244Fix::class.java
    )

    override fun onLoad(appParam: AppParam) {
        val sb = StringBuilder()
        fixList.forEach {
            val annotation = it.getAnnotation(AppFix::class.java)!!
            for (version in annotation.versions) {
                if (App.getVersionCode() == version) {
                    val fix = it.newInstance()
                    val fixResult = fix.onFix(annotation.date)
                    if (!spu.getBoolean(annotation.date, false)) {
                        if (sb.isNotEmpty()) sb.append("\n")
                        sb.append("${annotation.date}\n")
                        fixResult.forEachIndexed { i, b ->
                            sb.append("${i + 1}、${annotation.fixLog[i]}：${if (b) "成功" else "失败"}\n")
                        }
                        spu.edit().run {
                            putBoolean(annotation.date, true)
                            apply()
                        }
                    }
                    break
                }
            }
        }
        if (sb.isNotEmpty()) {
            if (sb.endsWith("\n")) sb.substring(0, sb.length - 1)
            val key = "fix244"
            val hasRead = spu.getBoolean(key, false)
            if (!hasRead) {
                announce("插件更新", "更新内容：\n$sb")
                spu.edit().run {
                    putBoolean(key, true)
                    apply()
                }
            }
        }
    }

    private fun announce(title: String, msg: String) {
        try {
            MapleUtils.findAndHookMethod(
                MainActivity::class.java,
                "onCreate",
                Bundle::class.java,
                object : MethodHook() {
                    override fun afterHookedMethod(param: MapleBridge.MethodHookParam) {
                        val context = param.thisObject as Context
                        App.getHandler().postDelayed({
                            AlertDialog.Builder(context)
                                .setTitle(title)
                                .setMessage(msg)
                                .setPositiveButton("我知道了", null)
                                .create().show()
                        }, 1000)
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            MapleUtils.log(e)
        }
    }
}