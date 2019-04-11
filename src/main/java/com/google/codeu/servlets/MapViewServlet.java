package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.Item;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles fetching all messages for the public feed.
 */
@WebServlet("/all-items")
public class MapViewServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * Responds with a JSON representation of posting data for all users. Does NOT include location
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    List<Item> postings = datastore.getAllPostings();
    Gson gson = new Gson();
    String json = gson.toJson(postings);

    response.getOutputStream().println(json);
  }
}
