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

package top.cronos.myreader.greendao;


import top.cronos.myreader.application.App;
import top.cronos.myreader.greendao.gen.DaoMaster;
import top.cronos.myreader.greendao.gen.DaoSession;
import top.cronos.myreader.greendao.util.MySQLiteOpenHelper;



public class DbManager {
    private static DbManager instance;
    private static DaoMaster daoMaster;
    private DaoSession mDaoSession;

    private static MySQLiteOpenHelper mySQLiteOpenHelper;

    public static DbManager getInstance() {
        if (instance == null) {
            instance = new DbManager();
        }
        return instance;
    }

    public DbManager(){
        mySQLiteOpenHelper = new MySQLiteOpenHelper(App.getmContext(), "read" , null);
        daoMaster = new DaoMaster(mySQLiteOpenHelper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return getInstance().mDaoSession;
    }

    public DaoSession getSession(){
       return mDaoSession;
    }

}
