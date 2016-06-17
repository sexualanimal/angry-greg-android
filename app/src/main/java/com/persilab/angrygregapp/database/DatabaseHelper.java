package com.persilab.angrygregapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.Link;

import java.sql.SQLException;

/**
 * Created by aleksandr on 30.12.15.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private Dao<Link, Integer> linksDao = null;

    public DatabaseHelper(Context context) {
        super(context, Constants.App.DATABASE_NAME, null, Constants.App.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Link.class);
        } catch (SQLException e) {
            Log.e(TAG, "error creating DB " + Constants.App.DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer) {
        try {
            TableUtils.dropTable(connectionSource, Link.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "error upgrading db " + Constants.App.DATABASE_NAME + "from ver " + oldVer);
            throw new RuntimeException(e);
        }
    }

    public Dao<Link, Integer> getLinksDao() throws SQLException {
        if (linksDao == null) {
            linksDao = getDao(Link.class);
        }
        return linksDao;
    }

    @Override
    public void close() {
        super.close();
        linksDao = null;
    }
}