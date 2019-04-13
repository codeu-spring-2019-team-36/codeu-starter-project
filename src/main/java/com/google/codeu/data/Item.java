package com.google.codeu.data;

public class Item {

  private String title;
  private Double price;
  private String email;
  private String description;
  private String itemPicURL;

  /** Item class. Stores information about a specific item posted by a user */
  public Item(String title, Double price, String email, String description,
      String itemPicURL) {
    this.title = title;
    this.price = price;
    this.email = email;
    this.description = description;
    this.itemPicURL = itemPicURL;
  }

  public Item() {}

  public String getEmail() {
    return email;
  }

  public String getTitle() {
    return title;
  }

  public Double getPrice() {
    return price;
  }

  public String getDescription() {
    return description;
  }
  
  public String getItemPicURL() {
    return itemPicURL;
  }

}
