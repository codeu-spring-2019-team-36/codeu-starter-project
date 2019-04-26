package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Item;
import com.google.codeu.data.Profile;
import com.google.codeu.data.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Handles fetching user profile data for matches feed
 * @author Alan
 *
 */
@WebServlet("/matches")
public class MatchesFeedServlet extends HttpServlet{

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * Responds with json of all postings except those made by the user
   * who made the request
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();

    // Get all postings except for the ones made by the user making the request
    List<Item> items = datastore.getAllPostingsExcept(userEmail);
    List<Ad> ads = generateSortedAdList(items, datastore.getProfile(userEmail));

    Gson gson = new Gson();
    String json = gson.toJson(ads);

    response.getOutputStream().println(json);
  }


  /* returns a list of ads with the given 'items' and sorts them by their
   * distance from the given 'user'
   */
  private List<Ad> generateSortedAdList(List<Item> items, Profile user) {
    List<Ad> ads = new ArrayList<>();

    for (Item item: items) {
      double distance = getDistanceInMiles(user, datastore.getProfile(item.getEmail()));
      Ad ad = new Ad(item, distance);
      ads.add(ad);
    }

    // Sort by distance ascending
    ads.sort((adOne, adTwo) -> Double.compare(adOne.getDistance(), adTwo.getDistance()));

    return ads;
  }

  /* Returns the absolute distance between the given 'userOne' and 'userTwo'
     rounded to the nearest digit */
  private double getDistanceInMiles(Profile userOne, Profile userTwo) {
    /* spherical law of cosines
     *  formula from https://www.movable-type.co.uk/scripts/latlong.html */
    try {
      double lat1 = userOne.getLatitude();
      double lat2 = userTwo.getLatitude();
      double lon1 = userOne.getLongitude();
      double lon2 = userTwo.getLongitude();

      double r = 3958.8; // earth radius in miles
      double phi1 = Math.toRadians(lat1);
      double phi2 = Math.toRadians(lat2);
      double deltaLambda = Math.toRadians(lon2 - lon1);

      double distance = Math.acos(Math.sin(phi1) * Math.sin(phi2) +
                        Math.cos(phi1) * Math.cos(phi2) * Math.cos(deltaLambda) ) * r;

      if (Double.isNaN(distance)) {
        return 0;
      }

      return Math.round(distance);
    } catch (NullPointerException e) {
      //  Distance is NaN or user id not input location data
      return 0;
    }
  }

  /* Adds on functionality of Item by adding ability to keep track of item distance
   * (distance defined by any arbitrary use, e.g distance of ad from a user */
  private class Ad extends Item {
    private double distance;

    public Ad(Item item, double distance) {
      super(item.getTitle(), item.getPrice(), item.getEmail(), item.getStart(),
          item.getEnd(), item.getDescription(), item.getItemPicURL());
      this.distance = distance;
    }

    public double getDistance() {
      return distance;
    }
  }

}
