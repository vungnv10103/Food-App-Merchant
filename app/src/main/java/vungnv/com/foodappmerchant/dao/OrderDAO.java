package vungnv.com.foodappmerchant.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.database.DbOrder;
import vungnv.com.foodappmerchant.model.Order;
import vungnv.com.foodappmerchant.model.UserModel;


public class OrderDAO {
    private final SQLiteDatabase db;

    public OrderDAO(Context context) {
        DbOrder dbHelper = new DbOrder(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(Order obj) {
        ContentValues values = new ContentValues();
        values.put("id", obj.id);
        values.put("idUser", obj.idUser);
        values.put("dateTime", obj.dateTime);
        values.put("waitingTime", obj.waitingTime);
        values.put("items", obj.items);
        values.put("quantity", obj.quantity);
        values.put("status", obj.status);
        values.put("price", obj.price);
        values.put("notes", obj.notes);

        return db.insert("Orders", null, values);
    }

    public int getWaitingTime(String id) {
        String sql = "SELECT * FROM Orders WHERE id=?";
        List<Order> order = getData(sql, id);
        if (order.size() == 0) {
            return 0;
        }
        return order.get(0).waitingTime;
    }

    public int updateWaitingTime(Order obj) {
        ContentValues values = new ContentValues();
        values.put("waitingTime", obj.waitingTime);

        return db.update("Orders", values, "id=?", new String[]{obj.id});
    }

    public int deleteOrderOutOfTime(String id) {
        return db.delete("Orders", "id=?", new String[]{id});
    }


    public List<Order> getALL(String type) {
        String sql = "SELECT * FROM Orders";
        return getData(sql, type);
    }


    public List<Order> getALLDefault() {
        String sql = "SELECT * FROM Orders";
        return getData(sql);
    }

    public List<Order> getALLBestSellingByType(String type) {
        String sql = "SELECT * FROM Orders WHERE type=? ORDER BY quantity_sold DESC";
        return getData(sql, type);
    }

    @SuppressLint("Range")
    private List<Order> getData(String sql, String... selectionArgs) {
        List<Order> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            Order obj = new Order();
            obj.id = cursor.getString(cursor.getColumnIndex("id"));
            obj.idUser = cursor.getString(cursor.getColumnIndex("idUser"));
            obj.dateTime = cursor.getString(cursor.getColumnIndex("dateTime"));
            obj.waitingTime = Integer.parseInt(cursor.getString(cursor.getColumnIndex("waitingTime")));
            obj.items = cursor.getString(cursor.getColumnIndex("items"));
            obj.quantity = Integer.parseInt(cursor.getString(cursor.getColumnIndex("quantity")));
            obj.status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("status")));
            obj.price = Double.valueOf(cursor.getString(cursor.getColumnIndex("price")));
            obj.notes = cursor.getString(cursor.getColumnIndex("notes"));

            list.add(obj);

        }
        return list;

    }
}
