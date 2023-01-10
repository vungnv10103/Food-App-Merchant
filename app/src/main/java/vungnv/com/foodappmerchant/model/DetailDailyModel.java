package vungnv.com.foodappmerchant.model;

public class DetailDailyModel {
    public int image;
    public String name;
    public String description;
    public String rating;
    public String price;
    public String timing;

    public DetailDailyModel() {
    }

    public DetailDailyModel(int image, String name, String description, String rating, String price, String timing) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.price = price;
        this.timing = timing;
    }
}
