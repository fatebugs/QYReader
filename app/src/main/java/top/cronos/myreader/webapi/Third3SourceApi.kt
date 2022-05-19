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

package top.cronos.myreader.webapi

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import top.cronos.myreader.application.App
import top.cronos.myreader.entity.SearchBookBean
import top.cronos.myreader.greendao.entity.Book
import top.cronos.myreader.greendao.entity.Chapter
import top.cronos.myreader.model.mulvalmap.ConMVMap
import top.cronos.myreader.model.third3.NoStackTraceException
import top.cronos.myreader.model.third3.webBook.WebBook
import top.cronos.myreader.webapi.crawler.source.Third3Crawler
import top.cronos.myreader.webapi.crawler.source.find.Third3FindCrawler
import java.util.concurrent.ExecutorService

/**
 * @author fengyue
 * @date 2022/1/20 11:46
 */
object Third3SourceApi : AndroidViewModel(App.getApplication()) {
    val scope = viewModelScope

    fun searchByT3C(
        key: String,
        rc: Third3Crawler,
        searchPool: ExecutorService? = null
    ): Observable<ConMVMap<SearchBookBean, Book>> {
        return Observable.create { emitter ->
            WebBook.searchBook(
                scope,
                rc.source,
                key,
                1,
                searchPool?.asCoroutineDispatcher() ?: Dispatchers.IO
            ).timeout(30000L)
                .onSuccess {
                    emitter.onNext(rc.getBooks(it))
                }.onError {
                    emitter.onError(it)
                }.onFinally {
                    emitter.onComplete()
                }
        }
    }

    fun getBookInfoByT3C(book: Book, rc: Third3Crawler): Observable<Book> {
        return Observable.create { emitter ->
            WebBook.getBookInfo(
                scope,
                rc.source,
                book,
            ).onSuccess {
                emitter.onNext(it)
            }.onError {
                emitter.onError(it)
            }.onFinally {
                emitter.onComplete()
            }
        }
    }

    fun getBookChaptersByT3C(book: Book, rc: Third3Crawler): Observable<List<Chapter>> {
        return Observable.create { emitter ->
            WebBook.getChapterList(
                scope,
                rc.source,
                book,
            ).onSuccess {
                emitter.onNext(it)
            }.onError {
                emitter.onError(it)
            }.onFinally {
                emitter.onComplete()
            }
        }
    }

    fun getChapterContentByT3C(
        chapter: Chapter,
        book: Book,
        rc: Third3Crawler
    ): Observable<String> {
        return Observable.create { emitter ->
            WebBook.getContent(
                scope,
                rc.source,
                book,
                chapter
            ).onSuccess {
                emitter.onNext(it)
            }.onError {
                emitter.onError(it)
            }.onFinally {
                emitter.onComplete()
            }
        }
    }

    fun findBook(url: String, fc: Third3FindCrawler, page: Int): Observable<List<Book>> {
        return Observable.create { emitter ->
            if (!url.contains("page") && page > 1){
                emitter.onError(NoStackTraceException("没有下一页"))
                return@create
            }
            WebBook.exploreBook(
                scope,
                fc.source,
                url,
                page
            ).onSuccess {
                emitter.onNext(it)
            }.onError {
                emitter.onError(it)
            }.onFinally {
                emitter.onComplete()
            }
        }
    }
}