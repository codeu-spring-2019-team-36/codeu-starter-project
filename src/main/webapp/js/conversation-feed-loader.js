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

//Get ?recipient=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterRecipient = urlParams.get("recipient");

/**
 * Shows the message form if the user is logged in.
 */
function showMessageFormIfLoggedIn() {
  fetch("/login-status")
    .then(response => {
      return response.json();
    })
    .then(loginStatus => {
      if (loginStatus.isLoggedIn) {
        const messageForm = document.getElementById("message-form");
        messageForm.classList.remove("hidden");
        document.getElementById("profile");
        messageForm.action = '/messages?recipient=' + parameterRecipient;
        messageForm.classList.remove("hidden");
        messageForm.scrollIntoView(true)
      }
    });
}

// Fetch messages and add them to the page.
function fetchMessages() {
  const url = "/conversation?recipient=" + parameterRecipient;
  fetch(url)
    .then(response => {
      return response.json();
    })
    .then(messages => {
      const messageContainer = document.getElementById("message-container");
      if (messages.length == 0) {
        messageContainer.innerHTML = "<p>There are no posts yet.</p>";
      } else {
        messageContainer.innerHTML = "";
      }
      messages.forEach(message => {
        const messageDiv = buildMessageDiv(message);
        messageContainer.appendChild(messageDiv);
      });
    });
}

function buildMessageDiv(message) {
  const usernameDiv = document.createElement("div");
  usernameDiv.classList.add("left-align");
  usernameDiv.appendChild(document.createTextNode(message.user));

  const timeDiv = document.createElement("div");
  timeDiv.classList.add("right-align");
  timeDiv.appendChild(document.createTextNode(new Date(message.timestamp)));

  const headerDiv = document.createElement("div");
  headerDiv.classList.add("message-header");
  headerDiv.appendChild(usernameDiv);
  headerDiv.appendChild(timeDiv);

  const bodyDiv = document.createElement("div");
  bodyDiv.classList.add("message-body");
  bodyDiv.appendChild(document.createTextNode(message.text));

  const messageDiv = document.createElement("div");
  messageDiv.classList.add("message-div");
  messageDiv.appendChild(headerDiv);
  messageDiv.appendChild(bodyDiv);

  return messageDiv;
}

// Fetch data and populate the UI of the page.
function buildUI() {
  const messageContainer = document.getElementById("title");
  messageContainer.innerHTML = "<h1>Direct Message with " + parameterRecipient + "</h1>"
  fetchMessages();
  showMessageFormIfLoggedIn();
}

/**
 * Creates an anchor element.
 * @param {string} url
 * @param {string} text
 * @return {Element} Anchor element
 */
function createLink(url, text) {
  const linkElement = document.createElement("a");
  linkElement.appendChild(document.createTextNode(text));
  linkElement.href = url;
  return linkElement;
}
