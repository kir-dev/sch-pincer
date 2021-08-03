function updateItem(id, status) {
    postForJsonObject('configure/order/update', {id: id, status: status})
        .then(function (data) {
            location.reload();
        }).catch(function (e) {
        console.error(e);
        console.error("Cannot send UPDATE request.");
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