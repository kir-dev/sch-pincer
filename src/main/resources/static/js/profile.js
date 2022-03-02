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

function changeItem(id, room, comment) {
    postForJsonObject('api/order/change', {id: id, room: room, comment: comment})
        .then(function () {
            location.reload();
        }).catch(function (e) {
        console.error("Cannot send CHANGE request.");
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

const BASE_PRICE = 'basePrice';

function openModalWithOrder(orderId) {

    let order = orders.find(o => o.id === orderId);
    let priceBreakdown = priceBreakdowns.find(pb => pb.orderId === orderId);

    document.getElementById('details-popup').classList.remove('inactive');
    document.getElementById('popup-title').innerText = order.name;
    document.getElementById('popup-header').style.backgroundImage = `url('${order.image}')`;
    document.getElementById('popup-window').className = `popup ${order.color}`;
    document.getElementById('popup-id').value = order.id;
    document.getElementById('popup-room-id').value = order.room;
    document.getElementById('popup-room-id').disabled = !order.changable;
    document.getElementById('popup-comment').value = order.comment;
    document.getElementById('popup-comment').disabled = !order.changable;

    document.getElementById('popup-edit').style.display = order.changable ? 'inline-block' : 'none';

    let data = [
        {
            name: LANG['basePrice'],
            value: priceBreakdown.prices[BASE_PRICE]
        }
    ];

    for (let key in priceBreakdown.prices) {
        if (key === 'basePrice') {
            continue;
        }

        let typeName = LANG[key.split("-")[0]];
        let name = key.replace(key.split("-")[0], `${typeName} `);

        data.push({
            name: name,
            value: priceBreakdown.prices[key]
        });
    }

    let toDisplay = `
    <table class="form full-table">
        <tr>
            <th>
                Név
            </th>
            <th style="text-align: end">
                Ár
            </th>
        </tr>`;
    for (let item of data) {
        toDisplay += `
        <tr> 
            <td>
                ${item.name}
            </td>
            <td>
                ${item.value} ${order.count > 1 ? "x " + order.count : ""}
            </td>
        </tr>
        `;
    }
    toDisplay += `
        <tr style="border-top: 4px solid #888">
            <td style="font-weight: bold">
                Összesen
            </td>
            <td style="font-weight: bold; text-align: end">
                ${order.price}            
            </td>
        </tr>
    `;
    toDisplay += "</table>";

    document.getElementById('info').innerHTML = toDisplay
}

function closePopup() {
    document.getElementById('details-popup').classList.add('inactive');
}
