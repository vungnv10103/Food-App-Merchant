package vungnv.com.foodappmerchant.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.database.DbStatusProduct;
import vungnv.com.foodappmerchant.model.StatusProductModel;

public class StatusProductDAO {
    private final SQLiteDatabase db;

    public StatusProductDAO(Context context) {
        DbStatusProduct dbHelper = new DbStatusProduct(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(StatusProductModel obj) {
        ContentValues values = new ContentValues();
        values.put("type", obj.type);
        values.put("status", obj.status);

        return db.insert("StatusProduct", null, values);
    }



    public int delete(String type) {
        return db.delete("StatusProduct", "type=?", new String[]{type});
    }


    public List<StatusProductModel> getALL() {
        String sql = "SELECT * FROM StatusProduct";
        return getData(sql);
    }


    @SuppressLint("Range")
    private List<StatusProductModel> getData(String sql, String... selectionArgs) {
        List<StatusProductModel> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            StatusProductModel obj = new StatusProductModel();
            obj.stt = Integer.parseInt(cursor.getString(cursor.getColumnIndex("stt")));
            obj.type = cursor.getString(cursor.getColumnIndex("type"));
            obj.status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("status")));

            list.add(obj);

        }
        return list;

    }
}
