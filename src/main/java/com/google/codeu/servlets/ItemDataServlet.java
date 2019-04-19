package com.google.codeu.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Item;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Handles fetching postings.
 */
@WebServlet("/item-data")
public class ItemDataServlet extends HttpServlet {
  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String user = request.getParameter("user");
    Item postingData;
    try {
      postingData = datastore.getPosting(user);
    } catch (Exception e) {
      System.out.println("in ItemDataServlet.java - No posting found for user:" + user);
      response.getWriter().println("No posting found");
      return;
    }

    Gson gson = new Gson();
    String json = gson.toJson(postingData);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // redirect user if they not logged in
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    // get parameters about post
    String userEmail = userService.getCurrentUser().getEmail();
    String title = Jsoup.clean(request.getParameter("title"), Whitelist.none());
    String price = request.getParameter("price");
    String description = Jsoup.clean(request.getParameter("description"), Whitelist.none());
    
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("item_pic");

    String itemPicURL = "";
    
    if (blobKeys != null && !blobKeys.isEmpty()) {
      BlobKey blobKey = blobKeys.get(0);
      ImagesService imagesService = ImagesServiceFactory.getImagesService();
      ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
      itemPicURL = imagesService.getServingUrl(options);
    }
    
    // create an item and store in Datastore
    Item item = new Item(title, Double.parseDouble(price), userEmail, description, itemPicURL);
    datastore.storePosting(item);
    response.sendRedirect("/user-page.html?user=" + userEmail);
  }
}
