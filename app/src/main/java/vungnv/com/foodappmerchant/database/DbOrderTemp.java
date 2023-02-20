package vungnv.com.foodappmerchant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodappmerchant.constant.Constant;


public class DbOrderTemp extends SQLiteOpenHelper implements Constant {
    public DbOrderTemp(@Nullable Context context) {
        super(context, DB_ORDER, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableOrder = "create table OrdersTemp(" +
                "id TEXT PRIMARY KEY," +
                "pos INTEGER not null," +
                "idUser TEXT not null," +
                "dateTime TEXT not null," +
                "waitingTime INTEGER not null," +
                "items TEXT not null," +
                "quantity INTEGER not null," +
                "status INTEGER not null," +
                "mCheck INTEGER not null," +
                "notes TEXT ," +
                "price REAL not null)";
        db.execSQL(createTableOrder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableOrder = "drop table if exists OrdersTemp";
        db.execSQL(dropTableOrder);

        onCreate(db);
    }
}
