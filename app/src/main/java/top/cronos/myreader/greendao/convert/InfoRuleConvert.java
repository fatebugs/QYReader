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

package top.cronos.myreader.greendao.convert;

import org.greenrobot.greendao.converter.PropertyConverter;

import top.cronos.myreader.greendao.entity.rule.InfoRule;
import top.cronos.myreader.util.utils.GsonExtensionsKt;

/**
 * @author fengyue
 * @date 2021/2/8 18:28
 */
public class InfoRuleConvert implements PropertyConverter<InfoRule, String> {

    @Override
    public InfoRule convertToEntityProperty(String databaseValue) {
        return GsonExtensionsKt.getGSON().fromJson(databaseValue, InfoRule.class);
    }

    @Override
    public String convertToDatabaseValue(InfoRule entityProperty) {
        return GsonExtensionsKt.getGSON().toJson(entityProperty);
    }
}
