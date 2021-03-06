/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.Timestamp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/** Provides access to the data stored in Datastore. */
public class Datastore {

  private DatastoreService datastore;
  private static int longestMessage = 0;
  private static HashMap<String, Integer> postsPerUser = new HashMap<String, Integer>();
  private static HashMap<String, Integer> messageCategoryCount = new HashMap<String, Integer>();

  public Datastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** Stores the Message in Datastore. */
  public void storeMessage(Message message) {
    Entity messageEntity = new Entity("Message", message.getId().toString());
    messageEntity.setProperty("user", message.getUser());
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());
    messageEntity.setProperty("recipient", message.getRecipient());
    messageEntity.setProperty("imageUrl", message.getImageUrl());
    messageEntity.setProperty("sentimentScore", message.getSentimentScore());

    // If there are more than 20 words, perform content classification
    if (getNumWords(message.getText()) > 20) {
      messageEntity.setProperty("messageCategories", message.getMessageCategories());

      String messageCategories = message.getMessageCategories().trim();
      String[] messageCategoryList = messageCategories.split("/");

      for (String category : messageCategoryList) {
        category = category.trim();
        category = category.replace("[", "");
        category = category.replaceAll("]", "");
        if (!messageCategoryCount.containsKey(category)) {
          messageCategoryCount.put(category, 1);
        } else {
          messageCategoryCount.put(category, messageCategoryCount.get(category) + 1);
        }
      }
    } else {
      messageEntity.setProperty("messageCategories", "");
    }
    datastore.put(messageEntity);

    int messageLength = message.getText().length();

    if (messageLength > longestMessage) {
      longestMessage = messageLength;
    }
    postsPerUser.put(message.getUser(), getMessages(message.getUser()).size());

  }

  /**
   * Gets messages sent to a specific recipient.
   *
   * @return a list of messages sent to the recipient, or empty list if recipient has never received
   *         has never posted a List is sorted by time descending.
   */
  public List<Message> getMessages(String recipient) {
    Query query = new Query("Message")
        .setFilter(new Query.FilterPredicate("recipient", FilterOperator.EQUAL, recipient))
        .addSort("timestamp", SortDirection.DESCENDING);
    List<Message> messages = fetchMessages(query);

    return messages;
  }

  /* Returns a list of the messeages sent between 'user1' and 'user2'
     sorted in descending order by time */
  public List<Message> getMessagesBetween(String user1, String user2) {
    List<Message> messagesFromUser1 = getMessages(user2);
    for (int i = 0; i < messagesFromUser1.size();i++) {
      Message message = messagesFromUser1.get(i);
      if (!message.getUser().equals(user1)) {
        messagesFromUser1.remove(i);
        i--;
      }
    }
    List<Message> messagesFromUser2 = getMessages(user1);
    for (int i = 0; i < messagesFromUser2.size();i++) {
      Message message = messagesFromUser2.get(i);
      if (!message.getUser().equals(user2)) {
        messagesFromUser2.remove(i);
        i--;
      }
    }
    return combineSorted(messagesFromUser1, messagesFromUser2);
  }

  /* Merges the two given lists in sorted ascending order by time
     Assumes the two lists are already sorted descending respectively */
  private List<Message> combineSorted(List<Message> messages1, List<Message> messages2) {
    List<Message> sortedDescending = new ArrayList<>();
    int p1 = 0;
    int p2 = 0;
    // while either pointer is still within bounds of their respective list
    // Merges the two lists in sorted order
    while (p1 < messages1.size() || p2 < messages2.size()) {
      // initialize each message to its value or null if pointer out of bounds
      Message message1 = p1 < messages1.size() ? messages1.get(p1) : null;
      Message message2 = p2 < messages2.size() ? messages2.get(p2) : null;
      if (message1 == null) {
        sortedDescending.add(message2);
        p2++;
      } else if (message2 == null) {
        sortedDescending.add(message1);
        p1++;
      } else {
        boolean greaterThan2 = Long.compare(message1.getTimestamp(), message2.getTimestamp()) > 0;
        sortedDescending.add(greaterThan2 ? message1 : message2);
        p1 = greaterThan2 ? p1 + 1 : p1;
        p2 = !greaterThan2 ? p2 + 1 : p2;
      }
    }
    /* Reverse order */
    List<Message> sortedAscending = new ArrayList<>();
    for (int i = sortedDescending.size() - 1; i >= 0; i--) {
      sortedAscending.add(sortedDescending.get(i));
    }
    return sortedAscending;
  }

  /**
   * Gets messages posted by all users.
   *
   * @return a list of messages posted by all users, or an empty list if no user has posted a
   *         message. List is sorted by time descending.
   */
  public List<Message> getAllMessages() {
    Query query = new Query("Message").addSort("timestamp", SortDirection.DESCENDING);
    List<Message> messages = fetchMessages(query);

    return messages;
  }

  /**
   * Gets postings made by all users. Needs refactoring after MVP
   *
   * @return a list of postings posted by all users, or an empty list if no user has posted an item.
   *         List is sorted by time descending.
   */
  public List<Item> getAllPostings() {
    Query query = new Query("Posting");
    return fetchPostings(query);
  }

  /**
   * Gets postings made by all users except the user with the given 'userEmail'
   *
   * @return a list of postings posted by all users except the user with the given 'userEmail'.
   *         Empty list if no other user has posted an item. List is sorted by time descending.
   */
  public List<Item> getAllPostingsExcept(String userEmail) {
    Query query = new Query("Posting")
        .setFilter(new Query.FilterPredicate("email", FilterOperator.NOT_EQUAL, userEmail));
    return fetchPostings(query);
  }

  /**
   * Retrieves list of messages for a specific user.
   *
   * @return a list of results, or empty list if no results found
   */
  public List<Message> fetchMessages(Query query) {
    PreparedQuery results = datastore.prepare(query);
    List<Message> messages = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();

        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");
        String recipient = (String) entity.getProperty("recipient");
        String imageUrl = (String) entity.getProperty("imageUrl");

        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        // sentimentScore casted to Double from float first to avoid it being saved as a 0
        float sentimentScore = entity.getProperty("sentimentScore") == null ? (float) 0.0
            : ((Double) entity.getProperty("sentimentScore")).floatValue();
        String messageCategories = (String) entity.getProperty("messageCategories");

        // Replace all image URLS in message with proper image HTML tags
        String regex = "(https?://\\S+\\.(png|jpg))";
        String replacement = "<img src=\"$1\" />";
        String textWithImagesReplaced = text.replaceAll(regex, replacement);

        Message message = new Message(id, user, textWithImagesReplaced, timestamp, recipient,
            sentimentScore, messageCategories, imageUrl);

        messages.add(message);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
    return messages;
  }

  /**
   * Retrieves list of postings based on specified query.
   *
   * @return a list of results, or empty list if no results found
   */
  public List<Item> fetchPostings(Query query) {
    PreparedQuery results = datastore.prepare(query);
    List<Item> postings = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      Item item = new Item((String) entity.getProperty("title"),
          (Double) entity.getProperty("price"),
          (String) entity.getProperty("email"),
          (String) entity.getProperty("start"),
          (String) entity.getProperty("end"),
          (String) entity.getProperty("description"), 
          (String) entity.getProperty("item_pic"));
      postings.add(item);
    }
    return (postings);
  }

  /**
   * Deletes user posting. Does nothing if user has no posting. User is identified by email
   */
  public void deletePosting(String email) {
    Query query = new Query("Posting")
        .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity posting = results.asSingleEntity();
    datastore.delete(posting.getKey());
  }

  /** Returns the total number of messages for all users. */
  public int getTotalMessageCount() {
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withLimit(1000));

  }

  /** Stores the User in Datastore. */
  public void storeUser(User user) {
    Entity userEntity = new Entity("User", user.getEmail());
    userEntity.setProperty("email", user.getEmail());

    Entity profileEntity = new Entity("Profile", user.getEmail(), userEntity.getKey());
    profileEntity.setProperty("email", user.getEmail());
    datastore.put(profileEntity);
  }

  /** Stores the Profile in Datastore. */
  public void storeProfile(Profile profile) {
    Key user = KeyFactory.createKey("User", profile.getEmail());

    Entity profileEntity = new Entity("Profile", profile.getEmail(), user);
    profileEntity.setProperty("email", profile.getEmail());
    profileEntity.setProperty("name", profile.getName());
    if (profile.getProfilePicURL() != null) {
      profileEntity.setProperty("profile_pic", profile.getProfilePicURL());
    }
    profileEntity.setProperty("latitude", profile.getLatitude());
    profileEntity.setProperty("longitude", profile.getLongitude());
    profileEntity.setProperty("phone", profile.getPhone());
    profileEntity.setProperty("bio", profile.getBio());
    datastore.put(profileEntity);
  }


  /** Stores a posting in Datastore. */
  public void storePosting(Item item) {
    Entity postingEntity = new Entity("Posting", item.getEmail());
    postingEntity.setProperty("email", item.getEmail());
    postingEntity.setProperty("title", item.getTitle());
    if (item.getItemPicURL() != null) {
      postingEntity.setProperty("item_pic", item.getItemPicURL());
    }
    postingEntity.setProperty("price", item.getPrice());
    postingEntity.setProperty("start", item.getStart());
    postingEntity.setProperty("end", item.getEnd());
    postingEntity.setProperty("description", item.getDescription());
    datastore.put(postingEntity);
  }

  /**
   * Returns the User owned by the email address, or null if no matching User was found.
   */
  public User getUser(String email) {

    Query query = new Query("User")
        .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity userEntity = results.asSingleEntity();
    if (userEntity == null) {
      return null;
    }

    User user = new User(email);

    return user;
  }

  /**
   * Returns the Profile owned by the email address, or null if no matching Profile was found.
   */

  public Profile getProfile(String email) {
    Query query = new Query("Profile")
        .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity profileEntity = results.asSingleEntity();
    if (profileEntity == null) {
      return null;
    }

    Profile profile = new Profile((String) profileEntity.getProperty("email"),
        (String) profileEntity.getProperty("profile_pic"),
        (String) profileEntity.getProperty("name"),
        (Double) profileEntity.getProperty("latitude"),
        (Double) profileEntity.getProperty("longitude"),
        (String) profileEntity.getProperty("phone"),
        (String) profileEntity.getProperty("bio"));

    return profile;
  }

  /**
   * Returns the Item owned by the email address, or null if no matching Item was found. TODO: an
   * item is uniquely identified by an email. change later so it is by a unique random ID
   */

  public Item getPosting(String email) {
    Query query = new Query("Posting")
        .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
    return fetchPostings(query).get(0);
  }

  /** Returns the longest message length of all users. */
  public int getLongestMessageCount() {
    return longestMessage;
  }

  /**
<<<<<<< HEAD
   *
=======
   * 
>>>>>>> refs/remotes/origin/master
   */
  public List<Profile> getAllProfiles() {
    Query query = new Query("Profile");
    PreparedQuery results = datastore.prepare(query);
    List<Profile> allProfiles = new ArrayList<>();
    for (Entity profileEntity : results.asIterable()) {
      Profile profile = new Profile((String) profileEntity.getProperty("email"),
          (String) profileEntity.getProperty("profile_pic"),
          (String) profileEntity.getProperty("name"),
          (Double) profileEntity.getProperty("latitude"),
          (Double) profileEntity.getProperty("longitude"),
          (String) profileEntity.getProperty("phone"),
          (String) profileEntity.getProperty("bio"));
      allProfiles.add(profile);
    }
    return allProfiles;
  }

  /** Returns the total number of users that have posted. */
  public int getTotalUserCount() {
    return postsPerUser.size();
  }

  /** Returns the top three users that have posted on the website. */
  public ArrayList<String> getTopUsers() {
    ArrayList<String> topUsers = new ArrayList<String>(3);
    int numTopUsers = 3;
    String currTopUser = "";

    // Find the three users with the most posts
    for (int i = numTopUsers; i > 0; i--) {
      int maxPosts = 0;
      currTopUser = "";
      for (String user : postsPerUser.keySet()) {
        if (postsPerUser.get(user) > maxPosts && !topUsers.contains(user)) {
          maxPosts = postsPerUser.get(user);
          currTopUser = user;
        }
      }
      topUsers.add(currTopUser);
    }
    return topUsers;
  }

  /** Returns the categories and their counts of all of the messages. */
  public String getMessageCategories() {
    String messageCategories = "";
    for (String category : messageCategoryCount.keySet()) {
      messageCategories = messageCategories + "(" + category + " "
          + messageCategoryCount.get(category) + ")" + " ; ";
    }
    return messageCategories;
  }

  /** Returns the number of words in a given string. */
  public int getNumWords(String text) {
    if (text == null) {
      return 0;
    }
    String trimmedText = text.trim();
    String[] textWords = trimmedText.split("\\s+");

    return textWords.length;
  }
}
