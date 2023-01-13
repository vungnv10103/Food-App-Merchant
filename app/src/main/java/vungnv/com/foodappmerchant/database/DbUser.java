package vungnv.com.foodappmerchant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodappmerchant.constant.Constant;

public class DbUser extends SQLiteOpenHelper implements Constant {

    public DbUser(@Nullable Context context) {
        super(context, DB_USER, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableUser = "create table User(" +
                "stt INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id TEXT not null," +
                "status INTEGER not null," +
                "img TEXT ," +
                "name TEXT not null," +
                "email TEXT not null," +
                "pass TEXT not null," +
                "phoneNumber TEXT not null," +
                "restaurantName TEXT not null," +
                "feedback TEXT ," +
                "coordinates TEXT not null," +
                "address TEXT not null)";
        db.execSQL(createTableUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableUser = "drop table if exists User";
        db.execSQL(dropTableUser);

        onCreate(db);

    }
}
