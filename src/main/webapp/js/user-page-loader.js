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

/** Sets the Item page link to point to user ad*/
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
/** Sets the posting delete link*/
function setItemDeleteLink() {
  const url = "/item-data?user=" + parameterUsername;
  fetch(url)
    .then(function(response) {
      return response.json();
    })
    .then(posting => {
      if (posting != "No posting found") {
        const item_link = document.getElementById("item-delete");
        var aTag = document.createElement("a");
        aTag.setAttribute(
          "href",
          "/item-data?user=" + parameterUsername + "&delete=true"
        );
        aTag.innerHTML = "Delete Ad";
        item_link.appendChild(aTag);
        item_link.addEventListener("click", function() {
          alert("Deleting");
        });
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
        messageForm.action = '/messages?recipient=' + parameterUsername;
        messageForm.classList.remove("hidden");
        //fetchImageUploadUrlAndShowForm();
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

/* Returns the login status of person viewing page */
function loginStatus() {
  fetch('/login-status')
      .then((response) => {
        return Promise.resolve(response.json());
  });
}

/** Fetches convos and add them to the page. */
function fetchConvos() {
  fetch('/login-status')
      .then((response) => {
        return response.json();
  })
      .then((loginStatus) => {
        if (loginStatus.isLoggedIn &&
            loginStatus.username == parameterUsername) {
          showAllConvos();
        } else if (loginStatus.isLoggedIn) {
          showConvoWithProfileBeingVisited();
        }
      });
}

/* Shows link to all convos of logged in user */
function showAllConvos() {
  const url = "/messages?user=" + parameterUsername;
  fetch(url)
    .then(response => {
      return response.json();
    })
    .then(messages => {
      const convosContainer = document.getElementById("message-container");
      if (messages.length == 0) {
        convosContainer.innerHTML = "<p>You have no conversations</p>";
      } else {
        convosContainer.innerHTML = "";
      }
      var convos = new Set();
      messages.forEach(message => {
        convoWith = message.user;
        convos.add(convoWith)
      });
      convos.forEach(convoWith => {
        convoWith = convoWith;
        const convoDiv = buildConvoDiv(convoWith);
        convosContainer.appendChild(convoDiv);
      });
    });
}

/* Shows link to convo between logged in user and
   the user of the profile they are visiting */
function showConvoWithProfileBeingVisited() {
  const convosContainer = document.getElementById("message-container");
  convosContainer.innerHTML = "";
  const convoDiv = buildConvoDiv(parameterUsername);
  convosContainer.appendChild(convoDiv);
}

/**
 * Builds an element that displays the message.
 * @param {Message} message
 * @return {Element}
 */

/* Builds and returns div object for with a link to the
   conversation with the given user 'convoWith' */
function buildConvoDiv(convoWith) {
  const convoDiv = document.createElement("div");
  convoDiv.classList.add("message-header");
  var convoLink = createLink(
    "/conversation.html?recipient=" + convoWith,
    "Convo with " + convoWith
  );
  convoDiv.appendChild(convoLink);
  return convoDiv;
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
      profileContainer.innerHTML += `${profile.bio || ""} --${profile.name ||
        ""} (${profile.phone || ""})`;
    });
}

/** Fetches data and populates the UI of the page. */
function buildUI() {
  setPageTitle();
  fetchProfile();
  showMessageFormIfLoggedIn();
  //fetchMessages();
  fetchConvos();
  setItemLinkifHasAd();
  setItemDeleteLink();
}

function createLink(url, text) {
  const linkElement = document.createElement("a");
  linkElement.appendChild(document.createTextNode(text));
  linkElement.href = url;
  return linkElement;
}
