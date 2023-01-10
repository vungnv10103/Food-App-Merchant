package vungnv.com.foodappmerchant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodappmerchant.constant.Constant;


public class DbProductSlideShow extends SQLiteOpenHelper implements Constant {

    public DbProductSlideShow(@Nullable Context context) {
        super(context, DB_ProductSlideShow, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

            String createTableProductSlideShow = "create table ProductSlideShow(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "img TEXT not null," +
                "type TEXT not null)";
        db.execSQL(createTableProductSlideShow);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableProductSlideShow = "drop table if exists ProductSlideShow";
        db.execSQL(dropTableProductSlideShow);

        onCreate(db);

    }
}
