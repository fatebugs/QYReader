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

package top.cronos.myreader.ui.adapter;

import android.widget.Filter;
import android.widget.Filterable;

import top.cronos.myreader.base.adapter.BaseListAdapter;
import top.cronos.myreader.base.adapter.IViewHolder;
import top.cronos.myreader.greendao.entity.Book;
import top.cronos.myreader.greendao.entity.ReplaceRuleBean;
import top.cronos.myreader.model.sourceAnalyzer.BookSourceManager;
import top.cronos.myreader.ui.adapter.holder.SourceExchangeHolder;
import top.cronos.myreader.ui.dialog.SourceExchangeDialog;
import top.cronos.myreader.util.help.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengyue
 * @date 2020/9/30 18:42
 */
public class SourceExchangeAdapter extends BaseListAdapter<Book> {
    private SourceExchangeDialog dialog;
    private List<Book> beans;

    public SourceExchangeAdapter(SourceExchangeDialog dialog, List<Book> beans) {
        this.dialog = dialog;
        this.beans = beans;
    }

    @Override
    protected IViewHolder createViewHolder(int viewType) {
        return new SourceExchangeHolder(dialog);
    }

    /**
     * 过滤器，实现搜索
     *
     * @return
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                List<Book> mFilterList = new ArrayList<>();
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mFilterList = beans;
                } else {
                    for (Book bean : beans) {
                        //这里根据需求，添加匹配规则
                        if (BookSourceManager.getSourceNameByStr(bean.getSource())
                                .contains(charString)) {
                            mFilterList.add(bean);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilterList;
                return filterResults;
            }

            //把过滤后的值返回出来
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null)
                    refreshItems((List<Book>) results.values);
                else
                    refreshItems(beans);
            }
        };
    }
}
