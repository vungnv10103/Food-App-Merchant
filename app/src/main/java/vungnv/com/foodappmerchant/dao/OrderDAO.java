package vungnv.com.foodappmerchant.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.database.DbOrderTemp;
import vungnv.com.foodappmerchant.model.Order;


public class OrderDAO {
    private final SQLiteDatabase db;

    public OrderDAO(Context context) {
        DbOrderTemp dbHelper = new DbOrderTemp(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(Order obj) {
        ContentValues values = new ContentValues();
        values.put("pos", obj.pos);
        values.put("id", obj.id);
        values.put("idUser", obj.idUser);
        values.put("dateTime", obj.dateTime);
        values.put("waitingTime", obj.waitingTime);
        values.put("items", obj.items);
        values.put("quantity", obj.quantity);
        values.put("status", obj.status);
        values.put("mCheck", obj.check);
        values.put("price", obj.price);
        values.put("notes", obj.notes);

        return db.insert("OrdersTemp", null, values);
    }
    public void deleteCart() {
        db.execSQL("delete from OrdersTemp");
    }

    public int getWaitingTime(String id) {
        String sql = "SELECT * FROM OrdersTemp WHERE id=?";
        List<Order> order = getData(sql, id);
        if (order.size() == 0) {
            return 0;
        }
        return order.get(0).waitingTime;
    }

    public int updateWaitingTime(Order obj) {
        ContentValues values = new ContentValues();
        values.put("waitingTime", obj.waitingTime);

        return db.update("OrdersTemp", values, "id=?", new String[]{obj.id});
    }
    public int updateCheck(Order obj){
        ContentValues values = new ContentValues();
        values.put("mCheck", obj.check);
        return db.update("OrdersTemp", values, "id=?", new String[]{obj.id});
    }

    public int deleteOrderOutOfTime(String id) {
        return db.delete("OrdersTemp", "id=?", new String[]{id});
    }


    public List<Order> getALL(String type) {
        String sql = "SELECT * FROM OrdersTemp";
        return getData(sql, type);
    }


    public List<Order> getALLDefault(int check) {
        String sql = "SELECT * FROM OrdersTemp WHERE mCheck=?";
        return getData(sql, String.valueOf(check));
    }

    public List<Order> getALLBestSellingByType(String type) {
        String sql = "SELECT * FROM OrdersTemp WHERE type=? ORDER BY quantity_sold DESC";
        return getData(sql, type);
    }

    @SuppressLint("Range")
    private List<Order> getData(String sql, String... selectionArgs) {
        List<Order> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            Order obj = new Order();
            obj.pos = Integer.parseInt(cursor.getString(cursor.getColumnIndex("pos")));
            obj.id = cursor.getString(cursor.getColumnIndex("id"));
            obj.idUser = cursor.getString(cursor.getColumnIndex("idUser"));
            obj.dateTime = cursor.getString(cursor.getColumnIndex("dateTime"));
            obj.waitingTime = Integer.parseInt(cursor.getString(cursor.getColumnIndex("waitingTime")));
            obj.items = cursor.getString(cursor.getColumnIndex("items"));
            obj.quantity = cursor.getString(cursor.getColumnIndex("quantity"));
            obj.status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("status")));
            obj.check = Integer.parseInt(cursor.getString(cursor.getColumnIndex("mCheck")));
            obj.price = cursor.getString(cursor.getColumnIndex("price"));
            obj.notes = cursor.getString(cursor.getColumnIndex("notes"));

            list.add(obj);

        }
        return list;

    }
}
