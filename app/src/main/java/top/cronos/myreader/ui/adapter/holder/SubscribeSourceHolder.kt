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

package top.cronos.myreader.ui.adapter.holder

import android.content.Intent
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import top.cronos.myreader.R
import top.cronos.myreader.base.adapter.ViewHolderImpl
import top.cronos.myreader.base.adapter2.onClick
import top.cronos.myreader.base.observer.MyObserver
import top.cronos.myreader.common.APPCONST
import top.cronos.myreader.greendao.DbManager
import top.cronos.myreader.greendao.entity.rule.BookSource
import top.cronos.myreader.model.sourceAnalyzer.BookSourceManager
import top.cronos.myreader.ui.activity.SourceEditActivity
import top.cronos.myreader.ui.adapter.SubscribeSourceAdapter
import top.cronos.myreader.util.ToastUtils
import top.cronos.myreader.util.help.StringHelper
import top.cronos.myreader.util.utils.RxUtils
import java.util.*

/**
 * @author fengyue
 * @date 2022/3/3 12:11
 */
class SubscribeSourceHolder(
    val fragment: Fragment,
    private val mCheckMap: HashMap<BookSource, Boolean>,
    private val onDelListener: SubscribeSourceAdapter.OnDelListener
) : ViewHolderImpl<BookSource>() {

    private var cbSource: CheckBox? = null
    private var tvEdit: TextView? = null
    private var tvEnOrDisable: TextView? = null
    private var tvDelete: TextView? = null

    override fun getItemLayoutId(): Int {
        return R.layout.item_subscribe_source
    }

    override fun initView() {
        cbSource = findById(R.id.cb_source)
        tvEdit = findById(R.id.tv_edit)
        tvEnOrDisable = findById(R.id.tv_en_or_disable)
        tvDelete = findById(R.id.tv_delete)
    }

    override fun onBind(holder: RecyclerView.ViewHolder, data: BookSource, pos: Int) {
        banOrUse(data)
        cbSource?.isChecked = mCheckMap[data] == true
        tvEdit?.onClick {
            val intent = Intent(fragment.context, SourceEditActivity::class.java)
            intent.putExtra(APPCONST.BOOK_SOURCE, data)
            fragment.startActivityForResult(intent, APPCONST.REQUEST_EDIT_BOOK_SOURCE)
        }
        tvEnOrDisable?.onClick {
            data.enable = !data.enable
            banOrUse(data)
            DbManager.getDaoSession().bookSourceDao.insertOrReplace(data)
        }
        tvDelete?.onClick {
            Observable.create { e: ObservableEmitter<Boolean?> ->
                BookSourceManager.removeBookSource(data)
                e.onNext(true)
            }.compose { RxUtils.toSimpleSingle(it) }
                .subscribe(object : MyObserver<Boolean?>() {
                    override fun onNext(aBoolean: Boolean) {
                        onDelListener.onDel(pos, data)
                    }

                    override fun onError(e: Throwable) {
                        ToastUtils.showError("删除失败")
                    }
                })
        }
    }

    private fun banOrUse(data: BookSource) {
        if (data.enable) {
            cbSource?.setTextColor(context.resources.getColor(R.color.textPrimary))
            if (!StringHelper.isEmpty(data.sourceGroup)) {
                cbSource?.text = String.format("%s [%s]", data.sourceName, data.sourceGroup)
            } else {
                cbSource?.text = data.sourceName
            }
            tvEnOrDisable?.setText(R.string.ban)
        } else {
            cbSource?.setTextColor(context.resources.getColor(R.color.textSecondary))
            if (!StringHelper.isEmpty(data.sourceGroup)) {
                cbSource?.text = String.format("(禁用中)%s [%s]", data.sourceName, data.sourceGroup)
            } else {
                cbSource?.text = String.format("(禁用中)%s", data.sourceName)
            }
            tvEnOrDisable?.setText(R.string.enable_use)
        }
    }
}