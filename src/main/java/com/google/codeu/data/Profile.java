package com.google.codeu.data;

public class Profile {

  private String email = " ";
  private String name = " ";
  private String profilePicURL = " ";
  private String location = " ";
  private String phone = " ";
  private String schedule = " ";
  private Double latitude = 0.00;
  private Double longitude = 0.00;

  public Profile() {
  }

  public Profile(String email, String profilePicURL, String name, String location,
      String phone, String schedule) {
    this.email = email;
    this.name = name;
    this.profilePicURL = profilePicURL;
    this.location = location;
    this.phone = phone;
    this.schedule = schedule;
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
  
  public String getSchedule() {
    return schedule;
  }
  
  public String getLocation() {
    return location;
  }
  //TODO:
  public Double getLongitude() {
    return 0.00;
  }
  //TODO:
  public Double getLatitude() {
    return 0.00;
  }
}