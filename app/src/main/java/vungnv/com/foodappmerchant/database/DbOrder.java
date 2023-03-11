package vungnv.com.foodappmerchant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodappmerchant.constant.Constant;


public class DbOrder extends SQLiteOpenHelper implements Constant {
    public DbOrder(@Nullable Context context) {
        super(context, DB_ORDER, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableOrderHistory = "create table OrderHistory(" +
                "id TEXT PRIMARY KEY," +
                "pos INTEGER not null," +
                "idUser TEXT not null," +
                "idMerchant TEXT not null," +
                "dateTime TEXT not null," +
                "waitingTime INTEGER not null," +
                "items TEXT not null," +
                "quantity INTEGER not null," +
                "status INTEGER not null," +
                "mCheck INTEGER not null," +
                "notes TEXT ," +
                "price REAL not null)";
        db.execSQL(createTableOrderHistory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableOrderHistory = "drop table if exists OrderHistory";
        db.execSQL(dropTableOrderHistory);

        onCreate(db);
    }
}
