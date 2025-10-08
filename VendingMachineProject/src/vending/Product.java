package vending;

public class Product {
    String name;
    int price;
    int stock;

    public Product(String name, int price, int stock) {
        this.name = name.toLowerCase();
        this.price = price;
        this.stock = stock;
    }

    public String toString() {
        return capitalize(name) + " (Price: " + price + ", Stock: " + stock + ")";
    }

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public void decrementStock() {
    if (stock > 0) stock--;
}
}
