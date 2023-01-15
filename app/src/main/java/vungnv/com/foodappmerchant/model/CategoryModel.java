package vungnv.com.foodappmerchant.model;

import java.util.HashMap;

public class CategoryModel {
    public int id;
    public String img;
    public String name;

    public CategoryModel() {
    }

    public CategoryModel(int id, String img, String name) {
        this.id = id;
        this.img = img;
        this.name = name;
    }
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("img", img);
        result.put("name", name);
        return result;
    }
}
