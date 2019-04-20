// Fetches all postings the user has matched with
function fetchAllPostingMatches() {
  const url = "/matches";
  fetch(url)
    .then(response => {
      return response.json();
    })
    .then(allPostings => {
      const postingContainer = document.getElementById("posting-container");
      if (allPostings.length == 0) {
        allPostings.innerHTML = "<p>You have no matches</p>";
      } else {
        postingContainer.innerHTML = "";
      }
      allPostings.forEach(posting => {
        const postingDiv = buildPostingDiv(posting);
        postingContainer.appendChild(postingDiv);
      });
    });
}

/*
 * Builds div for the given profile
 * @param profile The profile which the div is being made for
 * */
function buildPostingDiv(posting) {
  const postTitleDiv = document.createElement("div");
  postTitleDiv.appendChild(document.createTextNode(posting.title));

  const usernameDiv = document.createElement("div");
  usernameDiv.classList.add("left-align");
  var postingLink = createLink(
    "/user-page.html?user=" + posting.email,
    posting.email
  );
  postingLink.classList.add("posting-link");
  usernameDiv.appendChild(postingLink);

  const headerDiv = document.createElement("div");
  headerDiv.classList.add("posting-header");
  headerDiv.appendChild(postTitleDiv);
  headerDiv.appendChild(usernameDiv);

  const bodyDiv = document.createElement("div");
  bodyDiv.classList.add("posting-body");
  bodyDiv.appendChild(
    document.createTextNode("Description: " + posting.description)
  );
  bodyDiv.appendChild(document.createElement("br"));
  bodyDiv.appendChild(document.createTextNode("Price: " + posting.price));

  const postingDiv = document.createElement("div");
  postingDiv.classList.add("posting-div");
  postingDiv.appendChild(headerDiv);
  postingDiv.appendChild(bodyDiv);

  return postingDiv;
}

// Fetch data and populate the UI of the page.
// Data here is all the postings the user has matched with
function buildUI() {
  fetchAllPostingMatches();
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
