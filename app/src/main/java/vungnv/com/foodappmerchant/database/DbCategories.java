package vungnv.com.foodappmerchant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodappmerchant.constant.Constant;

public class DbCategories extends SQLiteOpenHelper implements Constant {

    public DbCategories(@Nullable Context context) {
        super(context, DB_Cate, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

            String createTableCategory = "create table Category(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "img TEXT not null," +
                "name TEXT not null)";
        db.execSQL(createTableCategory);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableCategory = "drop table if exists Category";
        db.execSQL(dropTableCategory);

        onCreate(db);

    }
}
