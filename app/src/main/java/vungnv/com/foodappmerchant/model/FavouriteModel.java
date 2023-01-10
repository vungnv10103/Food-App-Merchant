package vungnv.com.foodappmerchant.model;

public class FavouriteModel {
    private int id;
    private int img;
    private String title ;
    private double money;
    private int resource_image;


    public FavouriteModel(int img, String title, double money, int resource_image) {
        this.img = img;
        this.title = title;
        this.money = money;
        this.resource_image = resource_image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getResource_image() {
        return resource_image;
    }

    public void setResource_image(int resource_image) {
        this.resource_image = resource_image;
    }

    public FavouriteModel() {
    }
}

