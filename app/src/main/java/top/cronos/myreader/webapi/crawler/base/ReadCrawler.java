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
import top.cronos.myreader.entity.SearchBookBean;
import top.cronos.myreader.entity.StrResponse;
import top.cronos.myreader.greendao.entity.Book;
import top.cronos.myreader.greendao.entity.Chapter;
import top.cronos.myreader.model.mulvalmap.ConMVMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fengyue
 * @date 2020/4/28 16:18
 */
public interface ReadCrawler {
    String getSearchLink();  // 书源的搜索url
    String getCharset(); // 书源的字符编码
    String getSearchCharset(); // 书源搜索关键字的字符编码，和书源的字符编码就行
    String getNameSpace(); // 书源主页地址
    Boolean isPost(); // 是否以post请求搜索
    Map<String, String> getHeaders();// 自定义请求头，可添加cookie等

    // 旧版本
    ConMVMap<SearchBookBean, Book> getBooksFromSearchHtml(String html); // 搜索书籍规则
    ArrayList<Chapter> getChaptersFromHtml(String html); // 获取书籍章节列表规则
    String getContentFormHtml(String html); // 获取书籍内容规则

    // 新版本
    Observable<ConMVMap<SearchBookBean, Book>> getBooksFromStrResponse(StrResponse response); // 搜索书籍规则
    Observable<List<Chapter>> getChaptersFromStrResponse(StrResponse response); // 获取书籍章节列表规则
    Observable<String> getContentFormStrResponse(StrResponse response); // 获取书籍内容规则
}
