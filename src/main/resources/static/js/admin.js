function updateItem(id, status) {
    postForJsonObject('configure/order/update', {id: id, status: status})
        .then(function (data) {
            location.reload();
        }).catch(function (e) {
        console.error(e);
        console.error("Cannot send UPDATE request.");
    });
}

function deleteComment(id) {
    postForJsonObject('configure/order/set-comment', {id: id, comment: '[removed]'})
        .then(function (data) {
            location.reload();
        }).catch(function (e) {
        console.error(e);
        console.error("Cannot send UPDATE request.");
    });
}

function changePrice(id, price) {
    const newPrice = prompt('Mennyi legyen a termék ára?', price);
    if (newPrice != null && typeof(parseInt(newPrice)) === 'number') {
        postForJsonObject('configure/order/change-price', {id: id, price: parseInt(newPrice)})
            .then(function (data) {
                console.log(data);
                location.reload();
            }).catch(function (e) {
                console.error(e);
                console.error("Cannot send UPDATE request.");
            });
    }
}

function postForJsonObject(path, data) {
    return fetch(URL_BASE + path, {
        method: 'POST',
        cache: 'no-cache',
        credentials: 'same-origin',
        headers: {
            'Accept': 'text/plain',
            'Content-Type': 'application/json'
        },
        redirect: 'follow',
        referrerPolicy: 'no-referrer',
        body: JSON.stringify(data)
    });
}