var map;
var infowindow;
var service;
var markers = [];

function initMap() {
    var pyrmont = {lat: -33.867, lng: 151.195}; //текущее положение карты

    map = new google.maps.Map(document.getElementById('map'), { //получение карты и задание параметров
        center: pyrmont,
        zoom: 15
    });

    infowindow = new google.maps.InfoWindow(); //для отображения маркеров и их описания
    service = new google.maps.places.PlacesService(map); //сервис по поиску мест
    service.nearbySearch({ //поиск ближайших мест
        location: pyrmont, //текущее положение
        radius: 500,
        type: ['bank'] //тип места
    }, callback);

    getWeatherData();

}

function callback(results, status) {//рисуем на карте все маркеры
    if (status === google.maps.places.PlacesServiceStatus.OK) {
        for (var i = 0; i < results.length; i++) {
            createMarker(results[i]);
        }
    }
}

function deleteAllMarkers() {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}

function findNearbyPlaces() { //поиск ближайших мест по заданному типу места (placeType)
    var pyrmont = {lat: -33.867, lng: 151.195};
    var placeType = document.getElementById('place-type').value;

    deleteAllMarkers();
    service.nearbySearch({
        location: pyrmont,
        radius: 500,
        type: [placeType]
    }, callback);
}

function createMarker(place) {
    var marker = new google.maps.Marker({//создаем маркер
        map: map,
        position: place.geometry.location
    });
    google.maps.event.addListener(marker, 'click', function () {//вешаем на него листенер, который при нажатии на него отображает название места
        infowindow.setContent(place.name);
        infowindow.open(map, this);
    });
    markers.push(marker);//сохраняем созданый маркер в массив маркеров
    console.log(map);

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
