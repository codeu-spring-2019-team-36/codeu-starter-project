function fetchItemPicURL() {
  fetch("/item-pic-upload-url")
    .then(response => {
      return response.text();
    })
    .then(itemURL => {
      const itemForm = document.getElementById("ad-form");
      itemForm.action = itemURL;
    });
}
function buildUI() {
  fetchItemPicURL();
}
