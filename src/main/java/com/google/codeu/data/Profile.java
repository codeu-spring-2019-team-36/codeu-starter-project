package com.google.codeu.data;

public class Profile {

  private String email = " ";
  private String name = " ";
  private String profilePicURL = " ";
  private String phone = " ";
  private Double latitude = 0.00;
  private Double longitude = 0.00;

  public Profile() {
  }

  public Profile(String email, String profilePicURL, String name, Double latitude, Double longitude,
      String phone) {
    this.email = email;
    this.name = name;
    this.profilePicURL = profilePicURL;
    this.latitude = latitude;
    this.longitude = longitude;
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }
  
  public String getName() {
    return name;
  }
  
  public String getProfilePicURL() {
    return profilePicURL;
  }
  
  public String getPhone() {
    return phone;
  }  

  public Double getLongitude() {
    return longitude;
  }

  public Double getLatitude() {
    return latitude;
  }
}