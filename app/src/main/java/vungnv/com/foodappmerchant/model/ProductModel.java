package vungnv.com.foodappmerchant.model;

import java.util.HashMap;

public class ProductModel {
    public int pos;
    public String id;
    public String idUser;
    public String type;
    public String img;
    public String name;
    public String description;
    public String timeDelay;
    public Double price;
    public Double discount;
    public Double rate;
    public int favourite;
    public int check;
    public int status;
    public String address;
    public String feedBack;
    public int quantity_sold;
    public int quantityTotal;

    public ProductModel() {
    }

    public ProductModel(String id,String idUser, String type, String img, String name,
                        String description, String timeDelay, Double price, Double discount,
                        Double rate, int favourite, int check, int status, String address,
                        String feedBack, int quantity_sold, int quantityTotal) {
        this.id = id;
        this.idUser = idUser;
        this.type = type;
        this.img = img;
        this.name = name;
        this.description = description;
        this.timeDelay = timeDelay;
        this.price = price;
        this.discount = discount;
        this.rate = rate;
        this.favourite = favourite;
        this.check = check;
        this.status = status;
        this.address = address;
        this.feedBack = feedBack;
        this.quantity_sold = quantity_sold;
        this.quantityTotal = quantityTotal;
    }
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("idUser", idUser);
        result.put("type", type);
        result.put("img", img);
        result.put("name", name);
        result.put("description", description);
        result.put("timeDelay", timeDelay);
        result.put("price", price);
        result.put("discount", discount);
        result.put("rate", rate);
        result.put("favourite", favourite);
        result.put("check", check);
        result.put("status", status);
        result.put("address", address);
        result.put("feedBack", feedBack);
        result.put("quantity_sold", quantity_sold);
        result.put("quantityTotal", quantityTotal);
        return result;
    }
}
