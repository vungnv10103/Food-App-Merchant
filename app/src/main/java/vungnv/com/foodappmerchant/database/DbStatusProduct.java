package vungnv.com.foodappmerchant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodappmerchant.constant.Constant;

public class DbStatusProduct extends SQLiteOpenHelper implements Constant {

    public DbStatusProduct(@Nullable Context context) {
        super(context, DB_STATUS_PRODUCT, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

            String createTableStatusProduct = "create table StatusProduct(" +
                "stt INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT not null," +
                "status INTEGER not null)";
        db.execSQL(createTableStatusProduct);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableStatusProduct = "drop table if exists StatusProduct";
        db.execSQL(dropTableStatusProduct);

        onCreate(db);

    }
}
