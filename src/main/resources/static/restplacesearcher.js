let _map;

let _service;
let _placeInfo;
let _markers = [];

let _currentPlace;

function initialize() {
    initMap();
    initAutocomplete();
    getWeatherData();
}

function initMap() {
    let samara = {
        lat: 53.1999856,
        lng: 50.1572578
    };

    _currentPlace = samara;

    _map = new google.maps.Map(document.getElementById('map'), {
        center: _currentPlace,
        zoom: 13
    });
    _placeInfo = new google.maps.InfoWindow();
    _service = new google.maps.places.PlacesService(_map);
}

function initAutocomplete() {
    const startPlaceInput = document.getElementById("start-place-input");
    const finalPlaceInput = document.getElementById("final-place-input");

    [startPlaceInput, finalPlaceInput].forEach(input =>
        new google.maps.places.Autocomplete(
            input,
            {types: ["(regions)"]}
        )
    );
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
    const startPlaceInput = document.getElementById("start-place-input");
    const startPlaceButton = document.getElementById("start-place-button");

    let searchBox = new google.maps.places.SearchBox(startPlaceInput);

    // TODO Fix error for click on button without selecting place
    if (startPlaceInput.value) {
        startPlaceButton.addEventListener("click", function () {
            let place = searchBox.getPlaces()[searchBox.getPlaces().length - 1];
            let bounds = new google.maps.LatLngBounds();
            bounds.extend(place.geometry.location);
            _map.fitBounds(bounds);
            _map.setZoom(13);
            _currentPlace = {
                lat: place.geometry.location.lat(),
                lng: place.geometry.location.lng()
            };
        });
    }
}

function navigateToFinalPlace() {
    const finalPlaceInput = document.getElementById("final-place-input");
    const finalPlaceButton = document.getElementById("final-place-button");

    let searchBox = new google.maps.places.SearchBox(finalPlaceInput);

    // TODO Fix error for click on button without selecting place
    if (finalPlaceInput.value) {
        finalPlaceButton.addEventListener("click", function () {
            let place = searchBox.getPlaces()[searchBox.getPlaces().length - 1];
            let bounds = new google.maps.LatLngBounds();
            bounds.extend(place.geometry.location);
            _map.fitBounds(bounds);
            _map.setZoom(13);
            _currentPlace = {
                lat: place.geometry.location.lat(),
                lng: place.geometry.location.lng()
            };
        });
    }
}

function iAmLucky() {
    const biggestCities = ["Шанхай", "Карачи", "Пекин", "Дели", "Лагос", "Тяньцзинь", "Стамбул", "Токио", "Гуанчжоу", "Мумбаи", "Москва", "Сан-Паулу", "Шэньчжэнь", "Джакарта", "Лахор", "Сеул", "Киншаса", "Каир", "Мехико", "Лима", "Лондон", "Нью-Йорк", "Бангалор", "Бангкок", "Хошимин", "Дунгуань", "Чунцин", "Нанкин", "Тегеран", "Шэньян", "Богота", "Нинбо", "Гонконг", "Ханой", "Багдад", "Чанша", "Дакка", "Ухань", "Хайдарабад", "Ченнаи", "Рио-де-Жанейро", "Фейсалабад", "Фошань", "Цзуньи", "Сантьяго", "Эр-Рияд", "Ахмадабад", "Сингапур", "Шаньтоу", "Янгон", "Санкт-Петербург"];

    const rand = Math.floor(Math.random() * biggestCities.length);
    const finalPlaceInput = document.getElementById("final-place-input");
    finalPlaceInput.value = biggestCities[rand];
}

function findWay() {
    const start = document.getElementById("start-place-input").value;
    const final = document.getElementById("final-place-input").value;

    if (start && final) {
        alert("Way from" + start + " to " + final + " was found!");
    }
}

function getBeachPlaces() {
    findPlaces("пляжи", 1000);
}

function getArchitecturePlaces() {
    findPlaces("достопримечательности", 1000);
}

function getMuseumsPlaces() {
    findPlaces("музеи", 1000);
}

function findPlaceByType() {
    const placeType = document.getElementById("place-type-input").value;
    // TODO Remove hard radius
    findPlaces(placeType, 1000);
}

function findPlaces(type, radius) {
    $.ajax({
        url: "/findPlaceByType",
        dataType: "json",
        contentType: "application/json",
        data: {
            "placeType": type,
            "radius": radius,
            "lat": _currentPlace.lat,
            "lng": _currentPlace.lng
        },
        success: function (content) {
            console.log(content);
            deleteMarkers();
            content.results.forEach(place => createMarker(place));
        },
        error: function () {
            console.log("findPlaces by" + type + " -> no data");
        }
    });
}

function getWeatherData() {
    const currentPlace = { //Samara
        lat: 53.1999856,
        lng: 50.1572578
    };

    $.ajax({
        url: "/getWeatherData",
        dataType: "json",
        contentType: "application/json",
        data: {
            "currentPlace": currentPlace
        },
        success: function (content) {
            console.log(content);
        },
        error: function () {
            console.log("getWeatherData -> no data");
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

function setWidgetData(data) {
    if (typeof(data) != 'undefined' && data.results.length > 0) {
        for (var i = 0; i < data.results.length; ++i) {
            var objMainBlock = document.getElementById('m-booked-bl-simple-week-vertical-65350');
            if (objMainBlock !== null) {
                var copyBlock = document.getElementById('m-bookew-weather-copy-' + data.results[i].widget_type);
                objMainBlock.innerHTML = data.results[i].html_code;
                if (copyBlock !== null) objMainBlock.appendChild(copyBlock);
            }
        }
    } else {
        alert('data=undefined||data.results is empty');
    }
}


function getWeatherData() {
    const currentPlace = 'Samara'

    $.ajax({
        url: "/getWeatherData",
        dataType: "json",
        contentType: "application/json",
        data: {
            "currentPlace": currentPlace
        },
        success: function (content) {
            console.log(content);
        },
        error: function () {
            console.log("getWeatherData -> no data");
        }
    });
}
