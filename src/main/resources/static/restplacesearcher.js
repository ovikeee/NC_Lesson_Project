let _map;

let _service;
let _placeInfo;
let _markers = [];

let _currentPlace;
let _currentPlaceName;
let _currentPlaces = [];

let _directionsDisplay;
let _directionsService;

function initialize() {
    initMap();
    initAutocomplete();

    initDirection();
}

function initMap() {
    let samara = {
        lat: 53.1999856,
        lng: 50.1572578
    };

    _currentPlace = samara;
    _currentPlaceName = "Самара";

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

function initDirection() {
    _directionsDisplay = new google.maps.DirectionsRenderer;
    _directionsService = new google.maps.DirectionsService;
    _directionsDisplay.setMap(_map);
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
            updateCurrentPlace(place);
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
            updateCurrentPlace(place);
        });
    }
}

function updateCurrentPlace(place) {
    _currentPlace = {
        lat: place.geometry.location.lat(),
        lng: place.geometry.location.lng()
    };
    _currentPlaceName = place.name;
    document.getElementById("current-place-input").placeholder = _currentPlaceName;
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
        calculateAndDisplayRoute(start, final);
    }
}

function findBeaches() {
    findPlaces("пляж", 1000);
}

function findArchitectures() {
    findPlaces("достопримечательности", 1000);
}

function findMuseums() {
    findPlaces("музеи", 1000);
}

function findPlaceByType() {
    const placeType = document.getElementById("place-type-input").value;
    // TODO Remove hard radius
    findPlaces(placeType, 1000);
}

function calculateAndDisplayRoute(startPlace, finalPlace) {
    let selectedMode = document.getElementById("mode").value;
    _directionsService.route({
        origin: startPlace,
        destination: finalPlace,
        travelMode: google.maps.TravelMode[selectedMode]
    }, function (response, status) {
        console.log(response);
        if (status == 'OK') {
            _directionsDisplay.setDirections(response);
        } else {
            alert("Не удается построить маршрут!");
        }
    });
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
            _currentPlaces = content.results;
            _currentPlaces.forEach(place => createMarker(place));
            setPlaceListContent();
        },
        error: function () {
            console.log("findPlaces by" + type + " -> no data");
        }
    });
}

function getWeatherData() {
    $.ajax({
        url: "/getWeatherData",
        dataType: "json",
        contentType: "application/json",
        data: {
            "lat": _currentPlace.lat,
            "lng": _currentPlace.lng
        },
        success: function (content) {
            console.log(content);
            setWeatherWidgetContent(
                _currentPlaceName,

                content.dateToday,
                content.weekDay,
                content.todayNow,
                content.todayDay,
                content.todayNight,
                content.averageTemperatureDay,
                content.averageTemperatureNight,
                content.averagePrecipitation
            );
            document.getElementById("weather-widget").style.display = 'block';
        },
        error: function () {
            console.log("getWeatherData -> no data");
        }
    });
}

function setWeatherWidgetContent(city, dateToday, weekDay, todayNow, todayDay, todayNight, averageTemperatureDay, averageTemperatureNight, averagePrecipitation) {
    document.getElementById("location-city").innerHTML = city;

    document.getElementById("today-temperature").innerHTML = todayNow;
    document.getElementById("today-temperature-day").innerHTML = todayDay;
    document.getElementById("today-temperature-night").innerHTML = todayNight;
    document.getElementById("weekday").innerHTML = weekDay + ", " + dateToday;

    let $table = $("table tr");
    $("td", $table.eq(0)).eq(1).text(todayDay + 2 + "°C");
    $("td", $table.eq(0)).eq(2).text(todayNight - 2 + "°C");
}

function setPlaceListContent() {
    $("#place-list").remove();

    let placeListDiv = document.createElement("div");
    placeListDiv.id = "place-list";
    placeListDiv.className = "card";

    let headerH6 = document.createElement("h6");

    let placeListHeaderDiv = document.createElement("div");
    placeListHeaderDiv.id = "card-header";
    placeListHeaderDiv.innerText = "Найденные места";
    headerH6.appendChild(placeListHeaderDiv);

    placeListDiv.appendChild(headerH6);

    let listUL = document.createElement("ul");
    listUL.className = "list-group list-group-flush";

    if (_currentPlaces.length !== 0) {
        let index = 1;
        _currentPlaces.forEach(place => {
            let elementLI = document.createElement("span");
            elementLI.className = "label";
            elementLI.innerText = index + ". " + place.name;
            listUL.appendChild(elementLI);
            index++;
        });
    }

    placeListDiv.appendChild(listUL);
    document.body.appendChild(placeListDiv);
}

function filterByDistance() {
    const distance = document.getElementById("distance-input").value;

    if (distance) {
        alert("Places by distance " + distance + " found!");
    }
}

function filterPlaces() {
    const distanceFilter = document.getElementById("distance-checkbox").checked;

    if (distanceFilter === true) {
        filterByDistance();
    }
}