package vungnv.com.foodappmerchant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodappmerchant.constant.Constant;

public class DbItemCart extends SQLiteOpenHelper implements Constant {

    public DbItemCart(@Nullable Context context) {
        super(context, DB_Item_Cart, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableItemCart = "create table ItemCart(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "mCheck INTEGER not null," +
                "checkSelected INTEGER not null," +
                "name TEXT not null," +
                "cost REAL not null," +
                "newCost REAL not null," +
                "idUser INTEGER REFERENCES User(id)," +
                "idRecommend INTEGER REFERENCES Recommend(id)," +
                "img TEXT REFERENCES Recommend(img)," +
                "quantitiesNew INTEGER not null," +
                "quantities INTEGER not null)";
        db.execSQL(createTableItemCart);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableItemCart = "drop table if exists ItemCart";
        db.execSQL(dropTableItemCart);

        onCreate(db);

    }
}
