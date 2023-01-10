package vungnv.com.foodappmerchant.model;

public class ProductModel {
    public int id;
    public int idUser;
    public String type;
    public String img;
    public String name;
    public int favourite;
    public int mCheck;
    public String description;
    public String timeDelay;
    public Double price;
    public Double rate;
    public Double calo;
    public int quantityTotal;
    public int quantity_sold;
    public String address;
    public String feedBack;

    public ProductModel() {
    }

    public ProductModel(int id, int idUser, String type, String img, String name, int favourite,
                        int mCheck, String description, String timeDelay, Double price,
                        Double rate, Double calo, int quantityTotal, int quantity_sold,
                        String address, String feedBack)
    {
        this.id = id;
        this.idUser = idUser;
        this.type = type;
        this.img = img;
        this.name = name;
        this.favourite = favourite;
        this.mCheck = mCheck;
        this.description = description;
        this.timeDelay = timeDelay;
        this.price = price;
        this.rate = rate;
        this.calo = calo;
        this.quantityTotal = quantityTotal;
        this.quantity_sold = quantity_sold;
        this.address = address;
        this.feedBack = feedBack;
    }
}
