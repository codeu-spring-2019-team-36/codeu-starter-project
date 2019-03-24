package com.google.codeu.data;

public class Profile {

  private String email;
  private String phone;

  public Profile(String email, String phone) {
    this.email = email;
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }
}