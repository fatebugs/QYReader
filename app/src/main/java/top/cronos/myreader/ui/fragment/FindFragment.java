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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import top.cronos.myreader.base.BaseFragment;
import top.cronos.myreader.base.BitIntentDataManager;
import top.cronos.myreader.base.adapter.BaseListAdapter;
import top.cronos.myreader.base.adapter.IViewHolder;
import top.cronos.myreader.base.observer.MyObserver;
import top.cronos.myreader.base.observer.MySingleObserver;
import top.cronos.myreader.common.APPCONST;
import top.cronos.myreader.common.URLCONST;
import top.cronos.myreader.databinding.FragmentFindBinding;
import top.cronos.myreader.entity.Quotation;
import top.cronos.myreader.greendao.entity.rule.BookSource;
import top.cronos.myreader.model.sourceAnalyzer.BookSourceManager;
import top.cronos.myreader.ui.activity.FindBookActivity;
import top.cronos.myreader.ui.activity.SourceEditActivity;
import top.cronos.myreader.ui.adapter.holder.FindSourceHolder;
import top.cronos.myreader.util.ToastUtils;
import top.cronos.myreader.util.utils.GsonExtensionsKt;
import top.cronos.myreader.util.utils.OkHttpUtils;
import top.cronos.myreader.util.utils.RxUtils;
import top.cronos.myreader.webapi.crawler.base.FindCrawler;

/**
 * @author fengyue
 * @date 2020/9/13 21:07
 */
public class FindFragment extends BaseFragment {

    private FragmentFindBinding binding;
    private BaseListAdapter<BookSource> findSourcesAdapter;

    @Override
    protected View bindView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentFindBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Observable.create((ObservableOnSubscribe<List<BookSource>>) emitter -> {
            List<BookSource> findSources = new ArrayList<>();
            initLocalFind(findSources);
            List<BookSource> sources = BookSourceManager.getEnabledBookSourceByOrderNum();
            for (BookSource source : sources) {
                if (source.getFindRule() != null && !TextUtils.isEmpty(source.getFindRule().getUrl())) {
                    findSources.add(source);
                }
            }
            emitter.onNext(findSources);
            emitter.onComplete();
        }).compose(RxUtils::toSimpleSingle).subscribe(new MyObserver<List<BookSource>>() {
            @Override
            public void onSubscribe(Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(@NotNull List<BookSource> findSources) {
                initFindSources(findSources);
            }
        });
    }

    private void initLocalFind(List<BookSource> findSources) {
        BookSource source1 = new BookSource();
        source1.setSourceName("起点中文网");
        source1.setSourceUrl("top.cronos.myreader.webapi.crawler.find.QiDianFindCrawler");
        BookSource source2 = new BookSource();
        source2.setSourceName("起点女生网");
        source2.setSourceUrl("top.cronos.myreader.webapi.crawler.find.QianDianNSFindCrawler");
        BookSource source3 = new BookSource();
        source3.setSourceName("妙笔阁");
        source3.setSourceUrl("top.cronos.myreader.webapi.crawler.find.MiaoBiGeFindCrawler");
        BookSource source4 = new BookSource();
        source4.setSourceName("全本小说");
        source4.setSourceUrl("top.cronos.myreader.webapi.crawler.find.QB5FindCrawler");
        findSources.add(source1);
        findSources.add(source2);
        findSources.add(source3);
        findSources.add(source4);
    }

    private void initFindSources(List<BookSource> findSources) {
        findSourcesAdapter = new BaseListAdapter<BookSource>() {
            @Override
            protected IViewHolder<BookSource> createViewHolder(int viewType) {
                return new FindSourceHolder();
            }
        };
        binding.rvFindSources.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFindSources.setAdapter(findSourcesAdapter);
        //设置分割线
//        binding.rvFindSources.addItemDecoration(new DividerItemDecoration(getContext()));

        findSourcesAdapter.setOnItemClickListener((view, pos) -> {
            Intent intent = new Intent(getContext(), FindBookActivity.class);
            BookSource source = findSourcesAdapter.getItem(pos);
            if (source.getFindRule() == null) {
                FindCrawler findCrawler = null;
                try {
                    findCrawler = (FindCrawler) Class.forName(source.getSourceUrl()).newInstance();
                } catch (IllegalAccessException | java.lang.InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                BitIntentDataManager.getInstance().putData(intent, findCrawler);
            } else {
                BitIntentDataManager.getInstance().putData(intent, source);
            }
            getContext().startActivity(intent);
        });

        findSourcesAdapter.setOnItemLongClickListener((view, pos) -> {
            BookSource source = findSourcesAdapter.getItem(pos);
            if (source.getFindRule() == null) {
                ToastUtils.showWarring("内置发现无法编辑");
            } else {
                Intent intent = new Intent(getContext(), SourceEditActivity.class);
                intent.putExtra(APPCONST.BOOK_SOURCE, findSourcesAdapter.getItem(pos));
                getContext().startActivity(intent);
            }
            return true;
        });
        findSourcesAdapter.refreshItems(findSources);
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {
        getQuotation();
    }

    @Override
    protected void initClick() {
        super.initClick();
        binding.findRlQuotation.setOnClickListener(v -> getQuotation());
    }

    private void getQuotation() {
        Single.create((SingleOnSubscribe<Quotation>) emitter -> {
            String json = OkHttpUtils.getHtml(URLCONST.QUOTATION);
            emitter.onSuccess(GsonExtensionsKt.getGSON().fromJson(json, Quotation.class));
        }).compose(RxUtils::toSimpleSingle).subscribe(new MySingleObserver<Quotation>() {
            @Override
            public void onSubscribe(Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onSuccess(@NotNull Quotation q) {
                binding.tvQuotation.setText(q.getHitokoto());
                binding.tvFrom.setText(String.format("--- %s", q.getFrom()));
            }
        });
    }

    public void refreshFind() {
        initData(null);
    }

    public boolean isRecreate() {
        return binding == null;
    }
}
