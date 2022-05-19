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

package top.cronos.myreader.webapi.crawler.base;


import io.reactivex.Observable;
import top.cronos.myreader.entity.StrResponse;
import top.cronos.myreader.greendao.entity.Book;

/**
 * @author fengyue
 * @date 2020/5/19 19:50
 */
public interface BookInfoCrawler {
    String getNameSpace();
    String getCharset();
    Book getBookInfo(String html, Book book);
    default Observable<Book> getBookInfo(StrResponse strResponse, Book book){
        return Observable.create(emitter -> {
           emitter.onNext(getBookInfo(strResponse.body(), book));
           emitter.onComplete();
        });
    }
}
