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

package top.cronos.myreader.ui.activity

import android.os.Bundle
import top.cronos.myreader.base.BaseActivity
import android.content.Intent

import android.app.Activity
import android.content.Context
import top.cronos.myreader.util.ToastUtils


/**
 * @author fengyue
 * @date 2022/1/22 8:57
 */
class RestartActivity<VB> : BaseActivity<VB>() {
    override fun bindView() {}

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RestartActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

        fun restart(context: Context) {
            ToastUtils.showError("程序发生错误，正在为您重启")
            val intent = Intent(context, MainActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }


    override fun initData(savedInstanceState: Bundle?) {
        restart(this)
        finish()
        ToastUtils.showInfo("正在重启应用")
    }


}