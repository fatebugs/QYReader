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

package top.cronos.myreader.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.disposables.Disposable;
import top.cronos.myreader.base.BitIntentDataManager;
import top.cronos.myreader.base.LazyFragment;
import top.cronos.myreader.base.adapter.BaseListAdapter;
import top.cronos.myreader.base.adapter.IViewHolder;
import top.cronos.myreader.base.observer.MyObserver;
import top.cronos.myreader.base.observer.MySingleObserver;
import top.cronos.myreader.common.APPCONST;
import top.cronos.myreader.databinding.FragmentFindBook2Binding;
import top.cronos.myreader.entity.FindKind;
import top.cronos.myreader.entity.ad.AdBean;
import top.cronos.myreader.greendao.entity.Book;
import top.cronos.myreader.greendao.service.BookService;
import top.cronos.myreader.ui.activity.BookDetailedActivity;
import top.cronos.myreader.ui.adapter.holder.FindBookHolder;
import top.cronos.myreader.ui.dialog.SourceExchangeDialog;
import top.cronos.myreader.util.ToastUtils;
import top.cronos.myreader.util.help.StringHelper;
import top.cronos.myreader.util.utils.AdUtils;
import top.cronos.myreader.util.utils.RxUtils;
import top.cronos.myreader.webapi.BookApi;
import top.cronos.myreader.webapi.crawler.base.FindCrawler;

/**
 * @author fengyue
 * @date 2021/7/21 23:06
 */
public class FindBook2Fragment extends LazyFragment {
    private FragmentFindBook2Binding binding;
    private FindKind kind;
    private FindCrawler findCrawler;
    private int page = 1;
    private BaseListAdapter<Book> findBookAdapter;
    private SourceExchangeDialog mSourceDia;

    private static final String KIND = "kind";
    private static final String FIND_CRAWLER = "findCrawler";
    private AdBean adBean;
    private View flow;

    public FindBook2Fragment() {
    }

    public FindBook2Fragment(FindKind kind, FindCrawler findCrawler) {
        this.kind = kind;
        this.findCrawler = findCrawler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String dataKey = savedInstanceState.getString(APPCONST.DATA_KEY);
            kind = (FindKind) BitIntentDataManager.getInstance().getData(KIND + dataKey);
            findCrawler = (FindCrawler) BitIntentDataManager.getInstance().getData(FIND_CRAWLER + dataKey);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        String dataKey = StringHelper.getStringRandom(25);
        BitIntentDataManager.getInstance().putData(KIND + dataKey, kind);
        BitIntentDataManager.getInstance().putData(FIND_CRAWLER + dataKey, findCrawler);
        outState.putString(APPCONST.DATA_KEY, dataKey);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void lazyInit() {
        initData();
        initWidget();
    }

    @Nullable
    @Override
    public View bindView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentFindBook2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    protected void initData() {
        adBean = AdUtils.getAdConfig().getFind();
        getFlow();
        findBookAdapter = new BaseListAdapter<Book>() {
            @Override
            protected IViewHolder<Book> createViewHolder(int viewType) {
                return new FindBookHolder();
            }
        };
        binding.rvFindBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFindBooks.setAdapter(findBookAdapter);
        page = 1;
        loadBooks();
    }

    private void getFlow() {
        AdUtils.checkHasAd().subscribe(new MySingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                if (aBoolean && AdUtils.adTime("find", adBean)) {
                    if (adBean.getStatus() > 0) {
                        AdUtils.getFlowAd(getActivity(), 1, view -> flow = view, "find");
                    }
                }
            }
        });
    }

    protected void initWidget() {
        binding.loading.setOnReloadingListener(() -> {
            page = 1;
            loadBooks();
        });
        //小说列表下拉加载更多事件
        binding.srlFindBooks.setOnLoadMoreListener(refreshLayout -> loadBooks());

        //小说列表上拉刷新事件
        binding.srlFindBooks.setOnRefreshListener(refreshLayout -> {
            page = 1;
            loadBooks();
        });

        findBookAdapter.setOnItemClickListener((view, pos) -> {
            Book book = findBookAdapter.getItem(pos);
            if (!findCrawler.needSearch()) {
                goToBookDetail(book);
            } else {
                if (BookService.getInstance().isBookCollected(book)) {
                    goToBookDetail(book);
                    return;
                }
                mSourceDia = new SourceExchangeDialog(getActivity(), book);
                mSourceDia.setOnSourceChangeListener((bean, pos1) -> {
                    Intent intent = new Intent(getContext(), BookDetailedActivity.class);
                    BitIntentDataManager.getInstance().putData(intent, mSourceDia.getaBooks());
                    intent.putExtra(APPCONST.SOURCE_INDEX, pos1);
                    getActivity().startActivity(intent);
                    mSourceDia.dismiss();
                });
                mSourceDia.show();
            }
        });
    }

    private void loadBooks() {
        BookApi.findBooks(kind, findCrawler, page).compose(RxUtils::toSimpleSingle).subscribe(new MyObserver<List<Book>>() {
            @Override
            public void onSubscribe(Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(@NotNull List<Book> books) {
                binding.loading.showFinish();
                if (page == 1) {
                    if (books.size() == 0) {
                        binding.srlFindBooks.finishRefreshWithNoMoreData();
                    } else {
                        findBookAdapter.refreshItems(books);
                        binding.srlFindBooks.finishRefresh();
                        if (flow != null) {
                            int index = findBookAdapter.getItemCount() - books.size() + 5;
                            index = Math.min(findBookAdapter.getItemCount() - 1, index);
                            findBookAdapter.addOther(index, flow);
                        }
                    }
                } else {
                    if (books.size() == 0) {
                        binding.srlFindBooks.finishLoadMoreWithNoMoreData();
                    } else {
                        findBookAdapter.addItems(books);
                        binding.srlFindBooks.finishLoadMore();
                        if (flow != null) {
                            int index = findBookAdapter.getItemCount() - books.size() + 5;
                            index = Math.min(findBookAdapter.getItemCount() - 1, index);
                            findBookAdapter.addOther(index, flow);
                        }
                    }
                }
                page++;
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if (page == 1) {
                    ToastUtils.showError("数据加载失败\n" + e.getLocalizedMessage());
                    binding.loading.showError();
                    binding.srlFindBooks.finishRefresh();
                } else {
                    if (e.getMessage() != null && e.getMessage().contains("没有下一页")) {
                        binding.srlFindBooks.finishLoadMoreWithNoMoreData();
                    } else {
                        ToastUtils.showError("数据加载失败\n" + e.getLocalizedMessage());
                        binding.srlFindBooks.finishLoadMore();
                    }
                }
            }
        });
    }

    /**
     * 前往书籍详情
     *
     * @param book
     */
    private void goToBookDetail(Book book) {
        Intent intent = new Intent(getContext(), BookDetailedActivity.class);
        BitIntentDataManager.getInstance().putData(intent, book);
        getActivity().startActivity(intent);
    }


}
