let _map;

let _service;
let _placeInfo;
let _markers = [];

function initialize() {
    initMap();
    initAutocomplete();
}

function initMap() {
    let samara = {
        lat: 53.1999856,
        lng: 50.1572578
    };

    _map = new google.maps.Map(document.getElementById('map'), {
        center: samara,
        zoom: 13
    });
    _placeInfo = new google.maps.InfoWindow();
    _service = new google.maps.places.PlacesService(_map);
}

function initAutocomplete() {
    let startPlaceInput = document.getElementById("start-place-input");
    let finalPlaceInput = document.getElementById("final-place-input");

    [startPlaceInput, finalPlaceInput].forEach(input =>
        new google.maps.places.Autocomplete(
            input,
            {types: ["(regions)"]}
        )
    );

    /*let searchBox = new google.maps.places.SearchBox(startPlaceInput);
    _map.controls[google.maps.ControlPosition.TOP_LEFT].push(startPlaceInput);

    _map.addListener("bounds_changed", function () {
        searchBox.setBounds(_map.getBounds());
    });

    searchBox.addListener("places_changed", function () {
        let places = searchBox.getPlaces();

        if (places.length === 0) {
            return;
        }

        _map.fitBounds(bounds);
    });*/
}

function createMarker(place) {
    let marker = new google.maps.Marker({
        map: _map,
        position: place.geometry.location
    });
    google.maps.event.addListener(marker, 'click', function () {
        _placeInfo.setContent(place.name);
        _placeInfo.open(_map, this);
    });
    _markers.push(marker);
}

function deleteMarkers() {
    for (let i = 0; i < _markers.length; i++) {
        _markers[i].setMap(null);
    }
    _markers = [];
}

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
            "radius": 1000,
            "lat": 53.1999856,
            "lng": 50.1572578
        },
        success: function (content) {
            console.log(content);
            deleteMarkers();
            content.results.forEach(place => createMarker(place));
        },
        error: function () {
            console.log("findPlaceByType -> no data");
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