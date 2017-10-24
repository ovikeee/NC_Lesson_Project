let map;
let infowindow;
let service;
let markers = [];

function initMap() {
    const pyrmont = {lat: -33.867, lng: 151.195}; //текущее положение карты

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
}

function callback(results, status) {//рисуем на карте все маркеры
    if (status === google.maps.places.PlacesServiceStatus.OK) {
        results.forEach(item => {
            createMarker(item);
        });
    }
}
function deleteAllMarkers() {
    markers.forEach(item => {
        item.setMap(null);
    });
    markers = [];
}
function findNearbyPlaces(){ //поиск ближайших мест по заданному типу места (placeType)
    const pyrmont = {lat: -33.867, lng: 151.195};
    const placeType = document.getElementById('place-type').value;

    deleteAllMarkers();
    service.nearbySearch({
        location: pyrmont,
        radius: 500,
        type: [placeType]
    }, callback);
}

function createMarker(place) {
    const marker = new google.maps.Marker({//создаем маркер
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