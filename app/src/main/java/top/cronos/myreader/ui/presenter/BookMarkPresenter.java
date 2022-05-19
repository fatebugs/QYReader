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

package top.cronos.myreader.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import top.cronos.myreader.R;
import top.cronos.myreader.application.SysManager;
import top.cronos.myreader.base.BasePresenter;
import top.cronos.myreader.common.APPCONST;
import top.cronos.myreader.greendao.entity.Book;
import top.cronos.myreader.greendao.entity.BookMark;
import top.cronos.myreader.greendao.service.BookMarkService;
import top.cronos.myreader.ui.activity.CatalogActivity;
import top.cronos.myreader.ui.adapter.BookMarkAdapter;
import top.cronos.myreader.ui.fragment.BookMarkFragment;

import java.util.ArrayList;

/**
 * @author fengyue
 * @date 2020/7/22 11:11
 */
public class BookMarkPresenter implements BasePresenter {
    private BookMarkFragment mBookMarkFragment;
    private BookMarkService mBookMarkService;
    private ArrayList<BookMark> mBookMarks = new ArrayList<>();
    private BookMarkAdapter mBookMarkAdapter;
    private Book mBook;

    public BookMarkPresenter(BookMarkFragment mBookMarkFragment) {
        this.mBookMarkFragment = mBookMarkFragment;
        mBookMarkService = new BookMarkService();
    }

    @Override
    public void start() {
        mBook = ((CatalogActivity) mBookMarkFragment.getActivity()).getmBook();;
        initBookMarkList();
        mBookMarkFragment.getLvBookmarkList().setOnItemClickListener((parent, view, position, id) -> {
            BookMark bookMark = mBookMarks.get(position);
            int chapterPos = bookMark.getBookMarkChapterNum();
            int pagePos = bookMark.getBookMarkReadPosition();
            Intent intent = new Intent();
            intent.putExtra(APPCONST.CHAPTER_PAGE, new int[]{chapterPos, pagePos});
            mBookMarkFragment.getActivity().setResult(Activity.RESULT_OK, intent);
            mBookMarkFragment.getActivity().finish();
        });

        mBookMarkFragment.getLvBookmarkList().setOnItemLongClickListener((parent, view, position, id) -> {
            if (mBookMarks.get(position) != null) {
                mBookMarkService.deleteBookMark(mBookMarks.get(position));
                initBookMarkList();
            }
            return true;
        });
    }

    private void initBookMarkList() {
        mBookMarks = (ArrayList<BookMark>) mBookMarkService.findBookAllBookMarkByBookId(mBook.getId());
        mBookMarkAdapter = new BookMarkAdapter(mBookMarkFragment.getActivity(), R.layout.listview_chapter_title_item, mBookMarks);
        mBookMarkFragment.getLvBookmarkList().setAdapter(mBookMarkAdapter);
    }

    /**
     * 搜索过滤
     * @param query
     */
    public void startSearch(String query) {
        mBookMarkAdapter.getFilter().filter(query);
        mBookMarkFragment.getLvBookmarkList().setSelection(0);
    }
}
