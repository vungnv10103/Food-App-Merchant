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


public class OrderHistoryDAO {
    private final SQLiteDatabase db;

    public OrderHistoryDAO(Context context) {
        DbOrder dbHelper = new DbOrder(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(Order obj) {
        ContentValues values = new ContentValues();
        values.put("id", obj.id);
        values.put("pos", obj.pos);
        values.put("idUser", obj.idUser);
        values.put("idMerchant", obj.idMerchant);
        values.put("dateTime", obj.dateTime);
        values.put("waitingTime", obj.waitingTime);
        values.put("items", obj.items);
        values.put("quantity", obj.quantity);
        values.put("status", obj.status);
        values.put("mCheck", obj.check);
        values.put("price", obj.price);
        values.put("notes", obj.notes);

        return db.insert("OrderHistory", null, values);
    }
    public void deleteTempDBOrderHistory() {
        db.execSQL("delete from OrderHistory");
    }

    public int getWaitingTime(String id) {
        String sql = "SELECT * FROM OrderHistory WHERE id=?";
        List<Order> order = getData(sql, id);
        if (order.size() == 0) {
            return 0;
        }
        return order.get(0).waitingTime;
    }

    public int updateWaitingTime(Order obj) {
        ContentValues values = new ContentValues();
        values.put("waitingTime", obj.waitingTime);

        return db.update("OrderHistory", values, "id=?", new String[]{obj.id});
    }
    public int updateCheck(Order obj){
        ContentValues values = new ContentValues();
        values.put("mCheck", obj.check);
        return db.update("OrderHistory", values, "id=?", new String[]{obj.id});
    }

    public int deleteOrderOutOfTime(String id) {
        return db.delete("OrderHistory", "id=?", new String[]{id});
    }


    public List<Order> getALL(String type) {
        String sql = "SELECT * FROM OrderHistory";
        return getData(sql, type);
    }


    public List<Order> getALLDefault(int check) {
        String sql = "SELECT * FROM OrderHistory WHERE mCheck=?";
        return getData(sql, String.valueOf(check));
    }

    public List<Order> getALLBestSellingByType(String type) {
        String sql = "SELECT * FROM OrderHistory WHERE type=? ORDER BY quantity_sold DESC";
        return getData(sql, type);
    }

    @SuppressLint("Range")
    private List<Order> getData(String sql, String... selectionArgs) {
        List<Order> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            Order obj = new Order();
            obj.id = cursor.getString(cursor.getColumnIndex("id"));
            obj.pos = Integer.parseInt(cursor.getString(cursor.getColumnIndex("pos")));
            obj.idUser = cursor.getString(cursor.getColumnIndex("idUser"));
            obj.idMerchant = cursor.getString(cursor.getColumnIndex("idMerchant"));
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
