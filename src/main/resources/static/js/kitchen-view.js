
function reload(view) {
    getForJsonObject(`api/kitchen-view/${CIRCLE_ID}/${OPENING_ID}/${view}`)
        .then(data => processKitchenOrderView(data, view));
}

function processKitchenOrderView(data, view) {
    let result = "";
    data.forEach(val => {
        if (view === 'hand-over') {
            result += generateHandOverWidget(val);
        } else if (view === 'kitchen') {
            result += generateKitchenWidget(val);
        } else if (view === 'merged') {
            result += generateMergedWidget(val);
        } else if (view === 'shipping') {
            result += generateShippingWidget(val);
        }
    });
    document.getElementById('content').innerHTML = result;
}

function generateHandOverWidget(orderDto) {
    return `<div class="order status-${htmlEncode(orderDto.status)}">
        <h3>#${htmlEncode(orderDto.artificialId)} - ${htmlEncode(orderDto.name)}${orderDto.count > 1 ? (' x' + orderDto.count) : ''}</h3>
        <h4>${htmlEncode(orderDto.userName)} - ${htmlEncode(orderDto.room)}</h4>
        <h4 class="badge">${htmlEncode(orderDto.intervalMessage)} <span class="status status-${htmlEncode(orderDto.intervalStatus)}"></span></h4>
        <h4 class="badge">${orderDto.price} JMF</h4>

        <p class="ingreds">${htmlEncode(orderDto.extra)}</p>
        <div class="comment">
            <p>${htmlEncode(orderDto.comment)}</p>
            <p class="additional">${htmlEncode(orderDto.additionalComment)}</p>
            <b>${htmlEncode(orderDto.chefComment)}</b>
        </div>
        <div class="actions">
            <button onclick="setStatus(${orderDto.id}, 'HANDED_OVER', 'hand-over'); reload('hand-over')">
                    <span class="material-icons">local_shipping</span>
            </button>
            <button onclick="setStatus(${orderDto.id}, 'INTERPRETED', 'hand-over'); reload('hand-over')">
                    <span class="material-icons">engineering</span>
            </button>
            <button onclick="setChefComment(${orderDto.id}, 'hand-over'); reload('hand-over')">
                <span class="material-icons">format_quote</span>
            </button>
        </div>
    </div>`;
}

function generateKitchenWidget(orderDto) {
    return `<div class="order status-${htmlEncode(orderDto.status)}">
        <h3>#${htmlEncode(orderDto.artificialId)} - ${htmlEncode(orderDto.name)}${orderDto.count > 1 ? (' x' + orderDto.count) : ''}</h3>
        <h4>${htmlEncode(orderDto.userName)} - ${htmlEncode(orderDto.room)}</h4>
        <h4 class="badge">${htmlEncode(orderDto.intervalMessage)} <span class="status status-${htmlEncode(orderDto.intervalStatus)}"></span></h4>
        <h4 class="badge">${orderDto.price} JMF</h4>

        <p class="ingreds">${htmlEncode(orderDto.extra)}</p>
        <div class="comment">
            <p>${htmlEncode(orderDto.comment)}</p>
            <p class="additional">${htmlEncode(orderDto.additionalComment)}</p>
            <b>${htmlEncode(orderDto.chefComment)}</b>
        </div>
        <div class="actions">
            <div class="actions">
                <button onclick="setStatus(${orderDto.id}, 'COMPLETED', 'kitchen'); reload('kitchen')">
                    <span class="material-icons">downloading</span>
                </button>
                <button onclick="setStatus(${orderDto.id}, 'INTERPRETED', 'kitchen'); reload('kitchen')">
                    <span class="material-icons">engineering</span>
                </button>
                <button onclick="setStatus(${orderDto.id}, 'ACCEPTED', 'kitchen'); reload('kitchen')">
                    <span class="material-icons">shopping_cart</span>
                </button>
                <button onclick="setChefComment(${orderDto.id}, 'kitchen', '${jsEscape(orderDto.chefComment)}'); reload('kitchen')">
                    <span class="material-icons">format_quote</span>
                </button>
            </div>
        </div>
    </div>`;
}

function generateMergedWidget(orderDto) {
    return `<div class="order status-${htmlEncode(orderDto.status)}">
        <h3>#${htmlEncode(orderDto.artificialId)} - ${htmlEncode(orderDto.name)}${orderDto.count > 1 ? (' x' + orderDto.count) : ''}</h3>
        <h4>${htmlEncode(orderDto.userName)} - ${htmlEncode(orderDto.room)}</h4>
        <h4 class="badge">${htmlEncode(orderDto.intervalMessage)} <span class="status status-${htmlEncode(orderDto.intervalStatus)}"></span></h4>
        <h4 class="badge">${orderDto.price} JMF</h4>

        <p class="ingreds">${htmlEncode(orderDto.extra)}</p>
        <div class="comment">
            <p>${htmlEncode(orderDto.comment)}</p>
            <p class="additional">${htmlEncode(orderDto.additionalComment)}</p>
            <b>${htmlEncode(orderDto.chefComment)}</b>
        </div>
        <div class="actions">
            <div class="actions">
                <button onclick="setStatus(${orderDto.id}, 'HANDED_OVER', 'merged'); reload('merged')">
                    <span class="material-icons">local_shipping</span>
                </button>
                <button onclick="setStatus(${orderDto.id}, 'COMPLETED', 'merged'); reload('merged')">
                    <span class="material-icons">downloading</span>
                </button>
                <button onclick="setStatus(${orderDto.id}, 'INTERPRETED', 'merged'); reload('merged')">
                    <span class="material-icons">engineering</span>
                </button>
                <button onclick="setStatus(${orderDto.id}, 'ACCEPTED', 'merged'); reload('merged')">
                    <span class="material-icons">shopping_cart</span>
                </button>
                <button onclick="setChefComment(${orderDto.id}, 'merged', '${jsEscape(orderDto.chefComment)}'); reload('merged')">
                    <span class="material-icons">format_quote</span>
                </button>
            </div>
        </div>
    </div>`;
}

function generateShippingWidget(orderDto) {
    return `<div class="order status-${htmlEncode(orderDto.status)}">
        <h3>#${htmlEncode(orderDto.artificialId)} - ${htmlEncode(orderDto.name)}${orderDto.count > 1 ? (' x' + orderDto.count) : ''}</h3>
        <h4>${htmlEncode(orderDto.userName)} - ${htmlEncode(orderDto.room)}</h4>
        <h4 class="badge">${htmlEncode(orderDto.intervalMessage)} <span class="status status-${htmlEncode(orderDto.intervalStatus)}"></span></h4>
        <h4 class="badge">${orderDto.price} JMF</h4>

        <p class="ingreds">${htmlEncode(orderDto.extra)}</p>
        <div class="comment">
            <p>${htmlEncode(orderDto.comment)}</p>
            <p class="additional">${htmlEncode(orderDto.additionalComment)}</p>
            <b>${htmlEncode(orderDto.chefComment)}</b>
        </div>
        <div class="actions">
            <div class="actions">
                <button onclick="setStatus(${orderDto.id}, 'SHIPPED', 'shipping'); reload('shipping')">
                    <span class="material-icons">done</span>
                </button>
                <button onclick="setStatus(${orderDto.id}, 'COMPLETED', 'shipping'); reload('shipping')">
                    <span class="material-icons">close</span>
                </button>
            </div>
        </div>
    </div>`;
}

function setStatus(orderId, status, view) {
    postForJsonObject(`api/kitchen-view/${CIRCLE_ID}/${OPENING_ID}/view/${view}/${orderId}/status/${status}`, {})
        .then(data => processKitchenOrderView(data, view));
}

function setChefComment(orderId, view, defaultValue) {
    const commentValue = prompt('Mire szeretnéd állítani a megjegyzést?', defaultValue);
    if (commentValue != null) {
        postForJsonObject(`api/kitchen-view/${CIRCLE_ID}/${OPENING_ID}/view/${view}/${orderId}/chef-comment`, { comment: commentValue })
            .then(data => processKitchenOrderView(data, view));
    }
}

function setLiveStatus() {
    if (document.getElementById('live').checked) {
        localStorage.setItem('kitchen-view-update', 'true');
    } else {
        localStorage.setItem('kitchen-view-update', 'false');
    }
}

function startLiveStatus(view) {
    document.getElementById('live').checked = (localStorage.getItem('kitchen-view-update') === 'true');
    if (document.getElementById('content') != null) {
        setInterval(() => {
            if (localStorage.getItem('kitchen-view-update') === 'true') {
                reload(view);
            }
        }, 5000);
    }
    if (document.getElementById('statContent') != null) {
        setInterval(() => {
            if (localStorage.getItem('kitchen-view-update') === 'true') {
                reloadStats();
            }
        }, 5000);
    }
}

function reloadStats() {
    postForJsonObject(`api/kitchen-view/${CIRCLE_ID}/${OPENING_ID}/stats`, {})
        .then(data => {
            let result = "";
            data.forEach(val => result += generateStatWidget(val));
            document.getElementById('statContent').innerHTML = result;
        });
}

function generateStatWidget(statDto) {
    return `<tr>
        <td>${htmlEncode(statDto.name)}</td>
        <td>${statDto.acceptedCount}</td>
        <td>${statDto.interpretedCount}</td>
        <td>${statDto.completedCount}</td>
        <td>${statDto.handedOverCount}</td>
        <td>${statDto.shippedCount}</td>
        <td>${statDto.rejectedCount}</td>
        <td>${statDto.sum}</td>
    </tr>`;
}

function searchByName() {
    postForJsonObject(`api/kitchen-view/${CIRCLE_ID}/${OPENING_ID}/search`, {
        name: document.getElementById('user').value,
        room: ''
    }).then(populateResultTable)
}

function searchByRoom() {
    postForJsonObject(`api/kitchen-view/${CIRCLE_ID}/${OPENING_ID}/search`, {
        name: '',
        room: document.getElementById('room').value
    }).then(populateResultTable)
}

function populateResultTable(data) {
    let result = "";
    data.forEach(val => result += `
            <tr>
                <td>${htmlEncode(val.name)}</td>
                <td>${htmlEncode(val.room)}</td>
                <td>${htmlEncode(val.uid)}</td>
                <td>${htmlEncode(val.card)}</td>
                <td>
                    <button onclick="selectUser('${jsEscape(val.id)}', '${jsEscape(val.name)}', '${jsEscape(val.room)}', '${jsEscape(val.card)}')">
                        <span class="material-icons">done</span>
                    </button>
                </td>
            </tr>
        `)
    document.getElementById('searchResult').innerHTML = result;

    let responsiveResult = "";
    data.forEach(val => responsiveResult += `
            <tr>
                <td>${htmlEncode(val.name)}</td>
            </tr>
            <tr>
                <td>${htmlEncode(val.room)}</td>
            </tr>
            <tr>
                <td>${htmlEncode(val.uid)}</td>
            </tr>
            <tr>
                <td>${htmlEncode(val.card)}</td>
            </tr>
            <tr class="last">
                <td>
                    <button onclick="selectUser('${jsEscape(val.id)}', '${jsEscape(val.name)}', '${jsEscape(val.room)}', '${jsEscape(val.card)}')">
                        <span class="material-icons">done</span>
                    </button>
                </td>
            </tr>
        `)
    document.getElementById('responsiveSearchResult').innerHTML = responsiveResult;
}

function selectUser(uid, name, room, card) {
    manualOrderUser = name;
    manualOrderRoom = room;
    manualOrderUserId = uid;
    manualOrderCard = card;

    document.getElementById('selectedUser').value = manualOrderUser;
    document.getElementById('selectedRoom').value = manualOrderRoom;
    document.getElementById('selectedUserValid').style.display = 'inline-block';
    document.getElementById('selectedUserInvalid').style.display = 'none';
}

function updateSelectedUser() {
    manualOrderUser = document.getElementById('selectedUser').value;
    manualOrderUserId = 0;
    manualOrderCard = 'DO';
    document.getElementById('selectedUserValid').style.display = 'none';
    document.getElementById('selectedUserInvalid').style.display = 'inline-block';
}

function updateSelectedRoom() {
    manualOrderRoom = document.getElementById('selectedRoom').value;
}

function htmlEncode(str){
    return String(str).replace(/[^\w. ]/gi, function(c){
        return '&#'+c.charCodeAt(0)+';';
    });
}

function jsEscape(str){
    return String(str).replace(/[^\w. ]/gi, function(c){
        return '\\u'+('0000'+c.charCodeAt(0).toString(16)).slice(-4);
    });
}