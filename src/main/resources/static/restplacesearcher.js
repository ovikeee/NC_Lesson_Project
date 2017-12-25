let map;
let autocomplete;

let info;
let markers = [];

function navigateToStartPlace() {
    const place = document.getElementById("start-place-input").value;
    if (place) {
        alert("Navigate to " + place);
    }
}

function navigateToFinalPlace() {
    const place = document.getElementById("final-place-input").value;
    if (place) {
        alert("Navigate to " + place);
    }
}

function iAmLucky() {
    alert("I'm lucky");
}

function findWay() {
    const start = document.getElementById("start-place-input").value;
    const final = document.getElementById("final-place-input").value;

    if (start && final) {
        alert("Way from" + start + " to " + final + " was found!");
    }
}

function getWaterPlaces() {
    alert("Water Places!");
}

function getArchitecturePlaces() {
    alert("Architecture Places!");
}

function getBestPlaces() {
    alert("Best Places!");
}

function findPlaceByType() {
    const placeType = document.getElementById("place-type-input").value;

    $.ajax({
        url: "/findPlaceByType",
        dataType: "json",
        contentType: "application/json",
        data: {
            "placeType": placeType,
            "radius": 1,
            "lat": 53.1999856,
            "lng": 50.1572578
        },
        success: function (content) {
            deleteMarkers();
            content.results.forEach(place => createMarker(place));
        },
        error: function () {
            console.log("error");
        }
    });
}

function filterByTemperature() {
    const from = document.getElementById("from-select").value;
    const to = document.getElementById("to-select").value;
    if (from !== "null" && to !== "null" && from < to) {
        alert("Places by temperature from " + from + " to " + to + " found!");
    } else {
        alert("Введите корректный диапазон или отключите температурный фильтр")
    }
}

function filterByDistance() {
    const distance = document.getElementById("distance-input").value;

    if (distance) {
        alert("Places by distance " + distance + " found!");
    }
}

function filterByTime() {
    const time = document.getElementById("time-input").value;

    if (time) {
        alert("Places by time " + time + " found!");
    }
}

function filterPlaces() {
    const temperatureFilter = document.getElementById("weather-checkbox").checked;
    const distanceFilter = document.getElementById("distance-checkbox").checked;
    const timeFilter = document.getElementById("time-checkbox").checked;

    if (temperatureFilter === true) {
        filterByTemperature();
    }
    if (distanceFilter === true) {
        filterByDistance();
    }
    if (timeFilter === true) {
        filterByTime();
    }
}

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 53.1999856, lng: 50.1572578},//Samara
        zoom: 8
    });
    initAutocomplete();
}

// TODO Use
function initAutocomplete() {
    autocomplete = new google.maps.places.Autocomplete(
        document.getElementById('autocomplete'),
        {types: ['geocode']}
    );
}

function createMarker(place) {
    let marker = new google.maps.Marker({
        map: map,
        position: place.geometry.location
    });
    google.maps.event.addListener(marker, 'click', function () {
        info.setContent(place.name);
        info.open(map, this);
    });
    markers.push(marker);
}

function deleteMarkers() {
    for (let i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}