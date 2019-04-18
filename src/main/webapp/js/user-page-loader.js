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

//Get ?user=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get("user");

//URL must include ?user=XYZ parameter. If not, redirect to homepage.
if (!parameterUsername) {
  window.location.replace("/");
}

/** Sets the page title based on the URL parameter username. */
function setPageTitle() {
  document.getElementById("page-title").innerText = parameterUsername;
  document.title = parameterUsername + " - User Page";
}

/** Sets the Item page link to point to user ad if user has an ad*/
function setItemLinkifHasAd() {
  const url = "/item-data?user=" + parameterUsername;
  fetch(url)
    .then(function(response) {
      return response.json();
    })
    .then(posting => {
      if (posting != "No posting found") {
        const item_link = document.getElementById("item-link");
        var aTag = document.createElement("a");
        aTag.setAttribute("href", "/itemPage.html?user=" + parameterUsername);
        aTag.innerHTML = "Your Ad";
        item_link.appendChild(aTag);
      }
    });
}

/**
 * Shows the message form if the user is logged in and viewing their own page.
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
        fetchImageUploadUrlAndShowForm();
      }
    });
}

function fetchImageUploadUrlAndShowForm() {
  fetch("/image-upload-url")
    .then(response => {
      return response.text();
    })
    .then(imageUploadUrl => {
      const messageForm = document.getElementById("message-form");
      messageForm.action = imageUploadUrl;
      messageForm.classList.remove("hidden");
    });
}

/** Fetches messages and add them to the page. */
function fetchMessages() {
  const url = "/messages?user=" + parameterUsername;
  fetch(url)
    .then(response => {
      return response.json();
    })
    .then(messages => {
      const messagesContainer = document.getElementById("message-container");
      if (messages.length == 0) {
        messagesContainer.innerHTML = "<p>This user has no posts yet.</p>";
      } else {
        messagesContainer.innerHTML = "";
      }
      messages.forEach(message => {
        const messageDiv = buildMessageDiv(message);
        messagesContainer.appendChild(messageDiv);
      });
    });
}

/**
 * Builds an element that displays the message.
 * @param {Message} message
 * @return {Element}
 */
function buildMessageDiv(message) {
  const headerDiv = document.createElement("div");
  headerDiv.classList.add("message-header");
  headerDiv.appendChild(
    document.createTextNode(
      message.user +
        " - " +
        new Date(message.timestamp) +
        " [" +
        message.sentimentScore +
        "]"
    )
  );

  const bodyDiv = document.createElement("div");
  bodyDiv.classList.add("message-body");
  bodyDiv.innerHTML = message.text;
  if (message.imageUrl) {
    bodyDiv.innerHTML += "<br/>";
    bodyDiv.innerHTML += '<img src="' + message.imageUrl + '" />';
  }

  const messageDiv = document.createElement("div");
  messageDiv.classList.add("message-div");
  messageDiv.appendChild(headerDiv);
  messageDiv.appendChild(bodyDiv);

  return messageDiv;
}

function fetchProfile() {
  const url = "/profile?user=" + parameterUsername;
  fetch(url)
    .then(response => {
      return response.json();
    })
    .then(profile => {
      const profileContainer = document.getElementById("profile-container");

      profileContainer.innerHTML = "<br/>";

      if (profile.profilePicURL) {
        profileContainer.innerHTML +=
          '<img src="' + profile.profilePicURL + '" />';
        profileContainer.innerHTML += "<br/>";
      }
      profileContainer.innerHTML += `Name: ${profile.name ||
        ""} Latitude: ${profile.latitude ||
        ""} Longitude:  ${profile.longitude || ""}  Phone: ${profile.phone ||
        ""} Schedule: ${profile.schedule || ""}`;
    });
}

/** Fetches data and populates the UI of the page. */
function buildUI() {
  setPageTitle();
  fetchProfile();
  showMessageFormIfLoggedIn();
  fetchMessages();
  setItemLinkifHasAd();
}
