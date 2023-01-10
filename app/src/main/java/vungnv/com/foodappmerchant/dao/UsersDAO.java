package vungnv.com.foodappmerchant.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.database.DbUser;
import vungnv.com.foodappmerchant.model.UserModel;

public class UsersDAO {
    private final SQLiteDatabase db;

    public UsersDAO(Context context) {
        DbUser dbHelper = new DbUser(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(UserModel obj) {
        ContentValues values = new ContentValues();
        values.put("img", obj.img);
        values.put("name", obj.name);
        values.put("email", obj.email);
        values.put("pass", obj.pass);
        values.put("phoneNumber", obj.phoneNumber);
        values.put("searchHistory", obj.searchHistory);
        values.put("address", obj.address);
        values.put("coordinates", obj.coordinates);
        values.put("feedback", obj.feedback);

        return db.insert("User", null, values);
    }


    public int updateProfile( UserModel obj) {
        ContentValues values = new ContentValues();
        values.put("name", obj.name);
        values.put("phoneNumber", obj.phoneNumber);
        values.put("address", obj.address);

        return db.update("User", values, "email=?", new String[]{obj.email});
    }
    public int updateAll( UserModel obj) {
        ContentValues values = new ContentValues();
        values.put("img", obj.img);
        values.put("name", obj.name);
        values.put("pass", obj.pass);
        values.put("phoneNumber", obj.phoneNumber);
        values.put("coordinates", obj.coordinates);
        values.put("address", obj.address);

        return db.update("User", values, "email=?", new String[]{obj.email});
    }

    public int updateImg(UserModel obj) {
        ContentValues values = new ContentValues();
        values.put("img", obj.img);
        return db.update("User", values, "email=?", new String[]{obj.email});
    }

    public int updateFeedBack(UserModel obj) {
        ContentValues values = new ContentValues();
        values.put("feedback", obj.feedback);
        return db.update("User", values, "email=?", new String[]{obj.email});
    }
    public int updatePass(UserModel obj){
        ContentValues values = new ContentValues();
        values.put("pass", obj.pass);
        return db.update("User", values, "email=?", new String[]{obj.email});
    }
    public int updateSearchHistory(UserModel obj) {
        ContentValues values = new ContentValues();
        values.put("searchHistory", obj.searchHistory);
        return db.update("User", values, "email=?", new String[]{obj.email});
    }

    public String getSearchHistory(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<UserModel> list = getData(sql, email);
        return list.get(0).searchHistory;
    }
    public String getNameUser(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<UserModel> list = getData(sql, email);
        return list.get(0).name;
    }
    public String getPhone(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<UserModel> list = getData(sql, email);
        return list.get(0).phoneNumber;
    }
    public String getAddress(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<UserModel> list = getData(sql, email);
        return list.get(0).address;
    }
    public String getFeedback(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<UserModel> list = getData(sql, email);
        return list.get(0).feedback;
    }
    public String autoFillPassWord(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<UserModel> list = getData(sql, email);
        return list.get(0).pass;
    }

    public String getUriImg(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<UserModel> list = getData(sql, email);
        if (list.size() != 0){
            return list.get(0).img;
        }
       return "";
    }
    public int getIDUser(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<UserModel> list = getData(sql, email);
        return list.get(0).id;
    }

    public String getCurrentPass(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        List<UserModel> list = getData(sql, email);
        return list.get(0).pass;
    }

    public int delete(int id) {
        return db.delete("User", "id=?", new String[]{String.valueOf(id)});
    }


    public List<UserModel> getALL() {
        String sql = "SELECT * FROM User";
        return getData(sql);
    }
    public List<UserModel> getALLByEmail(String email) {
        String sql = "SELECT * FROM User WHERE email=?";
        return getData(sql, email);
    }


    @SuppressLint("Range")
    private List<UserModel> getData(String sql, String... selectionArgs) {
        List<UserModel> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            UserModel obj = new UserModel();
            obj.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            obj.img = cursor.getString(cursor.getColumnIndex("img"));
            obj.name = cursor.getString(cursor.getColumnIndex("name"));
            obj.email = cursor.getString(cursor.getColumnIndex("email"));
            obj.pass = cursor.getString(cursor.getColumnIndex("pass"));
            obj.phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
            obj.address = cursor.getString(cursor.getColumnIndex("address"));
            obj.searchHistory = cursor.getString(cursor.getColumnIndex("searchHistory"));
            obj.coordinates = cursor.getString(cursor.getColumnIndex("coordinates"));
            obj.feedback = cursor.getString(cursor.getColumnIndex("feedback"));

            list.add(obj);

        }
        return list;

    }
    // check login in sqlite
    public boolean checkAccountExist(String email, String pass){
        String sql = "SELECT * FROM User WHERE email=? AND pass=?";
        List<UserModel> list = getData(sql,email,pass);
        return list.size() != 0;

    }
}
