package vungnv.com.foodappmerchant.model;

import java.util.HashMap;

public class Order {
    public int pos;
    public String id;
    public String idUser;
    public String idMerchant;
    public String dateTime;
    public int waitingTime;
    public String items;
    public int quantity;
    public int status;
    public int check;
    public Double price;
    public String notes;

    public Order() {
    }

    public Order(int pos, String id, String idUser, String dateTime, int waitingTime, String items,
                 int quantity, int status, int check, Double price, String notes) {
        this.pos = pos;
        this.id = id;
        this.idUser = idUser;
        this.dateTime = dateTime;
        this.items = items;
        this.status = status;
        this.check = check;
        this.price = price;
        this.notes = notes;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("idUser", idUser);
        result.put("dateTime", dateTime);
        result.put("items", items);
        result.put("quantity", quantity);
        result.put("status", status);
        result.put("price", price);
        result.put("notes", notes);

        return result;
    }
}
