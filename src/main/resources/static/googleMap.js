'use-strict'

var map;
var autocomplete;


function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 53.1999856, lng: 50.1572578},
        zoom: 8
    });
    initAutocomplete();
}

function initAutocomplete() {
    // Create the autocomplete object, restricting the search to geographical
    // location types.
    autocomplete = new google.maps.places.Autocomplete(
        /** @type {!HTMLInputElement} */
        (document.getElementById('autocomplete')),
        {types: ['geocode']});

}
