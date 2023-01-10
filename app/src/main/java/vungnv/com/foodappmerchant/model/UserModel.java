package vungnv.com.foodappmerchant.model;

public class UserModel {
    public int id;
    public String img;
    public String name;
    public String email;
    public String pass;
    public String phoneNumber;
    public String address;
    public String searchHistory;
    public String coordinates;
    public String feedback;


    public UserModel(int id, String img, String name, String email, String pass, String phoneNumber, String searchHistory, String address, String coordinates, String feedback) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.phoneNumber = phoneNumber;
        this.searchHistory = searchHistory;
        this.address = address;
        this.coordinates = coordinates;
        this.feedback = feedback;
    }
    public UserModel() {
    }
}
