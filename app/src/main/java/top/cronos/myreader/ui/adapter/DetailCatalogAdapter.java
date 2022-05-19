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

import top.cronos.myreader.base.adapter.IViewHolder;
import top.cronos.myreader.base.adapter.BaseListAdapter;
import top.cronos.myreader.greendao.entity.Chapter;
import top.cronos.myreader.ui.adapter.holder.CatalogHolder;

/**
 * @author fengyue
 * @date 2020/8/17 15:06
 */
public class DetailCatalogAdapter extends BaseListAdapter<Chapter> {
    @Override
    protected IViewHolder<Chapter> createViewHolder(int viewType) {
        return new CatalogHolder();
    }
}
