package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Item;
import com.google.codeu.data.Profile;
import com.google.codeu.data.User;
import com.google.gson.Gson;

import java.io.IOException;
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
    List<Item> postings = datastore.getAllPostingsExcept(userEmail);
    
    // Sort postings by their distance to the user who made the request
    sort(postings, datastore.getProfile(userEmail));
    
    Gson gson = new Gson();
    String json = gson.toJson(postings);
    
    response.getOutputStream().println(json);
  }
  
  /*
   * Sorts the given list of 'postings' by the absolute distance of the users 
   * who made a posting to the given 'user'
   */
  private void sort(List<Item> postings, Profile user) {
    postings.sort(new Comparator<Item>() {
      
      @Override
      public int compare(Item postingAlpha, Item postingBeta) {
        try {
          Profile alphaUser = datastore.getProfile(postingAlpha.getEmail());
          Profile betaUser = datastore.getProfile(postingBeta.getEmail());
          
          double alphaLatDistFromCur = alphaUser.getLatitude() - user.getLatitude();
          double alphaLongDistFromCur = alphaUser.getLongitude() - user.getLongitude();
          double alphaSquaredDistFromCur = alphaLatDistFromCur * alphaLatDistFromCur + alphaLongDistFromCur * alphaLongDistFromCur;
          
          double betaLatDistFromCur = betaUser.getLatitude() - user.getLatitude();
          double betaLongDistFromCur = betaUser.getLongitude() - user.getLongitude();
          double betaSquaredDistFromCur = betaLatDistFromCur * betaLatDistFromCur + betaLongDistFromCur * betaLongDistFromCur;
  
          return Double.compare(alphaSquaredDistFromCur, betaSquaredDistFromCur);
        } catch(NullPointerException e) {
          // User did not fill out all proper profile fields
          return 0;
        }
      }
    });
  }
}
