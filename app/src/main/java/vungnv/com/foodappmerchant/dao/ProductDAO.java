package vungnv.com.foodappmerchant.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.database.DbProduct;
import vungnv.com.foodappmerchant.model.ProductModel;

public class ProductDAO {
    private final SQLiteDatabase db;

    public ProductDAO(Context context) {
        DbProduct dbHelper = new DbProduct(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(ProductModel obj) {
        ContentValues values = new ContentValues();
        values.put("idUser", obj.idUser);
        values.put("type", obj.type);
        values.put("img", obj.img);
        values.put("name", obj.name);
        values.put("favourite", obj.favourite);
        values.put("status", obj.status);
        values.put("description", obj.description);
        values.put("timeDelay", obj.timeDelay);
        values.put("price", obj.price);
        values.put("rate", obj.rate);
        values.put("quantityTotal", obj.quantityTotal);
        values.put("quantity_sold", obj.quantity_sold);
        values.put("address", obj.address);
        values.put("feedBack", obj.feedBack);

        return db.insert("Product", null, values);
    }

    public String getUriImg(int id) {
        String sql = "SELECT * FROM Product WHERE id=?";
        List<ProductModel> list = getData(sql, String.valueOf(id));
        return list.get(0).img;
    }

    public List<ProductModel> getALL(String type) {
        String sql = "SELECT * FROM Product WHERE type=?";
        return getData(sql, type);
    }
    public List<ProductModel> getALLDefault() {
        String sql = "SELECT * FROM Product";
        return getData(sql);
    }

    public List<ProductModel> getALLBestSellingByType(String type) {
        String sql = "SELECT * FROM Product WHERE type=? ORDER BY quantity_sold DESC";
        return getData(sql, type);
    }

    @SuppressLint("Range")
    private List<ProductModel> getData(String sql, String... selectionArgs) {
        List<ProductModel> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            ProductModel obj = new ProductModel();
            obj.stt = Integer.parseInt(cursor.getString(cursor.getColumnIndex("stt")));
            obj.idUser = cursor.getString(cursor.getColumnIndex("idUser"));
            obj.type = cursor.getString(cursor.getColumnIndex("type"));
            obj.img = cursor.getString(cursor.getColumnIndex("img"));
            obj.name = cursor.getString(cursor.getColumnIndex("name"));
            obj.favourite = Integer.parseInt(cursor.getString(cursor.getColumnIndex("favourite")));
            obj.status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("status")));
            obj.timeDelay = cursor.getString(cursor.getColumnIndex("timeDelay"));
            obj.description = cursor.getString(cursor.getColumnIndex("description"));
            obj.rate = Double.valueOf(cursor.getString(cursor.getColumnIndex("rate")));
            obj.price = Double.valueOf(cursor.getString(cursor.getColumnIndex("price")));
            obj.quantity_sold = Integer.parseInt(cursor.getString(cursor.getColumnIndex("quantity_sold")));
            obj.address = cursor.getString(cursor.getColumnIndex("address"));
            obj.feedBack = cursor.getString(cursor.getColumnIndex("feedBack"));
            obj.quantityTotal = Integer.parseInt(cursor.getString(cursor.getColumnIndex("quantityTotal")));

            list.add(obj);

        }
        return list;

    }
}
