function fetchProfileURL() {
	console.log("IN HERE");
  fetch("/profile-pic-upload-url")
    .then(response => {
      return response.text();
    })
    .then(profileURL => {
      const profileForm = document.getElementById("profile-form");
      profileForm.action = profileURL;
    });
}

function fetchLocation() {
	document.getElementById("latitude2").value = $("#latitude").value;
	console.log($("#latitude").value);
	console.log(document.getElementById("latitude2").value);
	document.getElementById("longitude2").value = $("#longitude").value;
	console.log($("#longitude").value);
	console.log(document.getElementById("longitude").value);
}

function buildUI() {
  //fetchLocation();
  fetchProfileURL();
}
