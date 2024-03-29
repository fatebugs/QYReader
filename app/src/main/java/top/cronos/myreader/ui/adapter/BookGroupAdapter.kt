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

package top.cronos.myreader.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.AsyncTask
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.backgroundColor
import top.cronos.myreader.R
import top.cronos.myreader.application.App
import top.cronos.myreader.base.adapter2.ItemViewHolder
import top.cronos.myreader.base.adapter2.RecyclerAdapter
import top.cronos.myreader.databinding.ItemGroupBinding
import top.cronos.myreader.greendao.entity.BookGroup
import top.cronos.myreader.greendao.service.BookGroupService
import top.cronos.myreader.ui.adapter.helper.IItemTouchHelperViewHolder
import top.cronos.myreader.ui.adapter.helper.ItemTouchCallback
import top.cronos.myreader.ui.adapter.helper.OnStartDragListener
import top.cronos.myreader.ui.dialog.BookGroupDialog
import top.cronos.myreader.ui.dialog.DialogCreator
import top.cronos.myreader.util.ToastUtils

/**
 * @author fengyue
 * @date 2021/8/30 9:25
 */
class BookGroupAdapter(
    context: Context,
    private val onStartDragListener: OnStartDragListener,
    private val groupDialog: BookGroupDialog,
) : RecyclerAdapter<BookGroup, ItemGroupBinding>(context),
    ItemTouchCallback.OnItemTouchListener {

    private var isMoved: Boolean = false

    override fun getViewBinding(parent: ViewGroup): ItemGroupBinding {
        return ItemGroupBinding.inflate(inflater, parent, false)
    }

    override fun convert(
        holder: ItemViewHolder,
        binding: ItemGroupBinding,
        item: BookGroup,
        payloads: MutableList<Any>
    ) {
        binding.run {
            tvGroupName.text = item.name
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun registerListener(holder: ItemViewHolder, binding: ItemGroupBinding) {
        binding.run {
            ivIcon.setOnClickListener {
                val item = getItem(holder.layoutPosition)
                DialogCreator.createCommonDialog(
                    context, "确定删除分组[" + item?.name + "]？",
                    "删除后，该书籍分组将永久不再显示，是否继续删除？", true, { _, _ ->
                        BookGroupService.getInstance()
                            .deleteBookGroup(item)
                        removeItem(holder.layoutPosition)
                        groupDialog.initBookGroups(false)
                        ToastUtils.showSuccess("分组[" + item?.name + "]删除成功！")
                    }, null
                )
            }
            ivMove.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    //通知ItemTouchHelper开始拖拽
                    onStartDragListener.onStartDrag(holder)
                }
                false
            }
        }
        binding.root.setOnClickListener {
            if (groupDialog.groupSize >= 50) {
                ToastUtils.showWarring("分组数量不能超过50")
                return@setOnClickListener
            }
            groupDialog.showAddOrRenameGroupDia(true, true,
                holder.layoutPosition, object : BookGroupDialog.OnGroup() {
                    override fun change() {

                    }

                    override fun addGroup(group: BookGroup?) {
                        App.getHandler().postDelayed(
                            { notifyItemChanged(holder.layoutPosition) }, 300
                        )
                    }
                })
        }
    }

    override fun onMove(srcPosition: Int, targetPosition: Int): Boolean {
        swapItem(srcPosition, targetPosition)
        isMoved = true
        return true
    }

    override fun onClearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        if (isMoved) {
            AsyncTask.execute {
                for ((index, item) in getItems().withIndex()) {
                    item.num = index
                }
                BookGroupService.getInstance().updateGroups(getItems())
                groupDialog.initBookGroups(false)
            }
        }
        isMoved = false
    }

    /*override fun onItemSelected(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.backgroundColor = context.resources.getColor(R.color.colorBackground)
    }

    override fun onItemClear(viewHolder: RecyclerView.ViewHolder) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
        val attribute = intArrayOf(android.R.attr.selectableItemBackground)
        val typedArray = context.theme.obtainStyledAttributes(typedValue.resourceId, attribute)
        viewHolder.itemView.background = typedArray.getDrawable(0)
    }*/
}