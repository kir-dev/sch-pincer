function showEdit() {
    document.getElementById('room').style.display = 'none';
    document.getElementById('room-edit').style.display = 'none';
    document.getElementById('room-form').style.display = 'block';
    document.getElementById('saved').style.display = 'none';
    document.getElementById('room-setter').focus();
}

function showRoom(room) {
    document.getElementById('room').innerText = room;
    document.getElementById('room').style.display = 'inline-block';
    document.getElementById('room-edit').style.display = 'inline-block';
    document.getElementById('room-form').style.display = 'none';
    document.getElementById('saved').style.display = 'inline-block';
}

function setRoom() {
    postForJsonObject("api/user/room", {room: document.getElementById('room-setter').value})
        .then(function () {
            showRoom(document.getElementById('room-setter').value);
        }).catch(function (e) {
        console.error("Cannot send POST request.");
        console.error(e);
    });
}

function cancelItem(id) {
    postForJsonObject('api/order/delete', {id: id})
        .then(function () {
            location.reload();
        }).catch(function (e) {
        console.error("Cannot send DELETE request.");
        console.error(e);
    });
}

function postForJsonObject(path, data) {
    return fetch(URL_BASE + path, {
        method: 'POST',
        cache: 'no-cache',
        credentials: 'same-origin',
        headers: {
            'Accept': 'text/plain',
            'Content-type': 'application/json;charset=UTF-8'
        },
        redirect: 'follow',
        referrerPolicy: 'no-referrer',
        body: JSON.stringify(data)
    });
}

function openModalWithOrder(orderId) {

    let order = orders.find(o => o.id === orderId);
    let priceBreakdown = priceBreakdowns.find(pb => pb.orderId === orderId);

    document.getElementById('details-popup').classList.remove('inactive');
    document.getElementById('popup-title').innerText = order.name;

    const toDisplay = [
        {
            name: 'Alapár',
            value: priceBreakdown.prices['basePrice']
        }
    ];

    for (let key in priceBreakdown.prices) {

        if (key === 'basePrice') {
            continue;
        }

        let typeName = LANG[key.split("-")[0]];
        let name = key.replace(key.split("-")[0], `${typeName} `);

        toDisplay.push({
            name: name,
            value: priceBreakdown.prices[key]
        });

    }
    document.getElementById('info').innerHTML = JSON.stringify(toDisplay);

}

function closePopup() {
    document.getElementById('details-popup').classList.add('inactive');
}