package vungnv.com.foodappmerchant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodappmerchant.constant.Constant;

public class DbHelper extends SQLiteOpenHelper implements Constant {

    public DbHelper(@Nullable Context context) {
        super(context, "DB_NAME", null, DB_VERSION);
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

        String createTableSystemCart = "create table SystemCart(" +
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
        db.execSQL(createTableSystemCart);

        String createTableCartTemp = "create table CartTemp(" +
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
        db.execSQL(createTableCartTemp);

        String createTableUser = "create table User(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "img TEXT ," +
                "name TEXT ," +
                "email TEXT not null," +
                "pass TEXT not null," +
                "phoneNumber TEXT ," +
                "feedback TEXT ," +
                "address TEXT not null)";
        db.execSQL(createTableUser);

        String createTableVoucher = "create table Voucher(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "img INTEGER not null," +
                "voucherTitle TEXT not null," +
                "voucherDeadline TEXT not null)";
        db.execSQL(createTableVoucher);

        String createTableVoucherSystem = "create table VoucherSystem(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "img INTEGER not null," +
                "voucherTitle TEXT not null," +
                "voucherDeadline TEXT not null)";
        db.execSQL(createTableVoucherSystem);

        String createTableCategory = "create table Category(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "img TEXT not null," +
                "name TEXT not null)";
        db.execSQL(createTableCategory);

        String createTableProduct = "create table Product(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "img TEXT not null," +
                "name TEXT not null," +
                "cost REAL not null," +
                "location TEXT not null," +
                "quantity INTEGER not null," +
                "type TEXT REFERENCES Category(name))";
        db.execSQL(createTableProduct);

        String createTableRecommended = "create table Recommend(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "img TEXT not null," +
                "favourite INTEGER not null," +
                "mCheck INTEGER not null," +
                "name TEXT not null," +
                "idUser INTEGER REFERENCES User(id)," +
                "location TEXT not null," +
                "description TEXT not null," +
                "timeDelay TEXT not null," +
                "calo REAL not null," +
                "rate REAL not null," +
                "quantity_sold INTEGER not null," +
                "cost REAL not null)";
        db.execSQL(createTableRecommended);

        String createTableOder = "create table Oder(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "code TEXT not null," +
                "listProduct TEXT not null," +
                "status TEXT not null," +
                "time TEXT not null," +
                "idUser INTEGER REFERENCES User(id)," +
                "checkStatus INTEGER not null," +
                "totalPrice REAL not null," +
                "feeTransport REAL not null," +
                "totalOder REAL not null)";
        db.execSQL(createTableOder);

        String createTableLocation = "create table Location(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT not null," +
                "email TEXT REFERENCES User(email)," +
                "phone TEXT not null," +
                "location TEXT not null)";
        db.execSQL(createTableLocation);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableItemCart = "drop table if exists ItemCart";
        db.execSQL(dropTableItemCart);
        String dropTableSystemCart = "drop table if exists SystemCart";
        db.execSQL(dropTableSystemCart);
        String dropTableCartTemp = "drop table if exists CartTemp";
        db.execSQL(dropTableCartTemp);
        String dropTableUser = "drop table if exists User";
        db.execSQL(dropTableUser);
        String dropTableVoucher = "drop table if exists Voucher";
        db.execSQL(dropTableVoucher);
        String dropTableVoucherSystem = "drop table if exists VoucherSystem";
        db.execSQL(dropTableVoucherSystem);
        String dropTableCategory = "drop table if exists Category";
        db.execSQL(dropTableCategory);
        String dropTableProduct = "drop table if exists Product";
        db.execSQL(dropTableProduct);
        String dropTableRecommend = "drop table if exists Recommend";
        db.execSQL(dropTableRecommend);
        String dropTableOder = "drop table if exists Oder";
        db.execSQL(dropTableOder);
        String dropTableOLocation = "drop table if exists Location";
        db.execSQL(dropTableOLocation);

        onCreate(db);

    }
}
