// Fetches profile for all users and adds them to page
function fetchProfileForAllMatches(){
  const url = '/matches';
  fetch(url).then(response => {
    return response.json();
  }).then(allProfiles => {
    const profileContainer = document.getElementById('profile-container');
    if(allProfiles.length == 0){
     allProfiles.innerHTML = '<p>You have no matches</p>';
    }
    else{
     profileContainer.innerHTML = '';
    }
    allProfiles.forEach(profile => {
     const profileDiv = buildProfileDiv(profile);
     profileContainer.appendChild(profileDiv);
    });
  });
}

/*
* Builds div for the given profile
* @param profile The profile which the div is being made for
*/
function buildProfileDiv(profile){
 const usernameDiv = document.createElement('div');
 usernameDiv.classList.add("left-align");
 usernameDiv.appendChild(createLink('/user-page.html?user=' + profile.email, profile.name));

 const headerDiv = document.createElement('div');
 headerDiv.classList.add('profile-header');
 headerDiv.appendChild(usernameDiv);

 const bodyDiv = document.createElement('div');
 bodyDiv.classList.add('profile-body');
 bodyDiv.appendChild(document.createTextNode(profile.schedule + "\n" + "Latitude: " + profile.latitude + " Longitude: " + profile.longitude));

 const profileDiv = document.createElement('div');
 profileDiv.classList.add("profile-div");
 profileDiv.appendChild(headerDiv);
 profileDiv.appendChild(bodyDiv);

 return profileDiv;
}

// Fetch data and populate the UI of the page.
function buildUI(){
 fetchProfileForAllMatches();
}

/**
* Creates an anchor element.
* @param {string} url
* @param {string} text
* @return {Element} Anchor element
*/
function createLink(url, text) {
const linkElement = document.createElement('a');
linkElement.appendChild(document.createTextNode(text));
linkElement.href = url;
return linkElement;
}
