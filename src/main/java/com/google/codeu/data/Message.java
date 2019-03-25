/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.data;

import java.util.UUID;

/** A single message posted by a user. */
public class Message {

  private UUID id;
  private String user;
  private String text;
  private long timestamp;
  private String recipient;
  private String imageUrl;

  /**
   * Constructs a new {@link Message} posted by {@code user} with {@code text}
   * content, and with the given {@code recipient}. Generates a
   * random ID and uses the current system time for the creation time of {@code this}
   * message.
   * @param user The user posting {@code this} message
   * @param text The content of {@code this} message
   * @param recipient The recipient of {@code this} message
   */
  public Message(String user, String text, String recipient) {
    this(UUID.randomUUID(), user, text, System.currentTimeMillis(), recipient);
  }

  /**
   * Constructs a new {@link Message} posted by {@code user}, of the given 
   * {@code UUID} at the given {@code timestamp}, with {@code text} content,
   * and with the given {@code recipient}.
   * @param id The ID of the user sending {@code this} message
   * @param user The user posting {@code this} message
   * @param text The content of {@code this} message
   * @param timestamp The time {@code this} message was made at in milliseconds
   *                  since the Unix Epoch
   * @param recipient The recipient of {@code this} message
   */
  public Message(UUID id, String user, String text, long timestamp, String recipient) {
    this(id, user, text, timestamp, recipient, null);
  }

  /**
   * Constructs a new {@link Message} posted by {@code user}, of the given 
   * {@code UUID} at the given {@code timestamp}, with {@code text} content,
   * and with the given {@code recipient}. Image in {@code this} message is
   * found at the given 'imageUrl', if any, otherwise it must be null 
   * @param id The ID of the user sending {@code this} message
   * @param user The user posting {@code this} message
   * @param text The content of {@code this} message
   * @param timestamp The time {@code this} message was made at in milliseconds
   *                  since the Unix Epoch
   * @param recipient The recipient of {@code this} message
   * @param imageUrl The url of the image in this message, if any, otherwise set null
   */
  public Message(UUID id, String user, String text, long timestamp, String recipient, String imageUrl) {
    this.id = id;
    this.user = user;
    this.text = text;
    this.timestamp = timestamp;
    this.recipient = recipient;
    this.imageUrl = imageUrl;
  }

  public UUID getId() {
    return id;
  }

  public String getUser() {
    return user;
  }

  public String getText() {
    return text;
  }

  public long getTimestamp() {
    return timestamp;
  }
  
  public String getRecipient() {
    return recipient;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
