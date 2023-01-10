package vungnv.com.foodappmerchant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodappmerchant.constant.Constant;

public class DbDishes extends SQLiteOpenHelper implements Constant {

    public DbDishes(@Nullable Context context) {
        super(context, DB_DISHES, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String createTableDishes = "create table Dishes(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT not null)";
        db.execSQL(createTableDishes);




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableDishes = "drop table if exists Dishes";
        db.execSQL(dropTableDishes);


        onCreate(db);

    }
}
