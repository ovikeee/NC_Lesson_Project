let map;
let autocomplete;

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 53.1999856, lng: 50.1572578},//Samara
        zoom: 8
    });
    initAutocomplete();
}

// Create the autocomplete object, restricting the search to geographical location types.
function initAutocomplete() {
    autocomplete = new google.maps.places.Autocomplete(
        document.getElementById('autocomplete'),
        {types: ['geocode']}
    );
}