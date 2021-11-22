let page = 0;
let endReached = false;
let selectedItem = null;
let latestData;
let searchResult = [];
let manualOrder = false;

var beepSound = new Audio('/beep-boop.mp3');

function appendNext(profile = 0) {
    if (document.getElementById('no-results') == null)
        return;
    if (page === 0)
        clearAll();

    getForJsonObject('api/items/' + getFilter('/') + (profile !== parseInt(0) ? '?circle=' + profile : ''))
        .then(function (data) {
            endReached = true;
            if (data.length === 0) {
                document.getElementById('loading').style.display = 'none';
                document.getElementById('list-end').style.display = 'none';
                document.getElementById('no-results').style.display = 'inline-block';
                return;
            }
            searchResult = data;
            data.forEach(val => addItem(val));
            document.getElementById('list-end').style.display = 'inline-block';
            document.getElementById('loading').style.display = 'none';
        });
}

function keywordFilter(val, keyword) {
    return keyword !== "" && (val.name.toLocaleLowerCase().includes(keyword)
        || val.ingredients.toLocaleLowerCase().includes(keyword)
        || val.circleName.toLocaleLowerCase().includes(keyword)
        || val.keywords.toLocaleLowerCase().includes(keyword));
}

function filterSearch() {
    const keyword = document.getElementById('search-input').value.toLocaleLowerCase();

    if (keyword.length === 0) {
        updateUrl(null);
        clearAll();
        searchResult.forEach(val => addItem(val));
        return;
    }

    updateUrl(keyword);
    clearAll();

    const keywords = keyword.split(' ');
    searchResult.filter(val => keywords.some(k => keywordFilter(val, k)))
        .forEach(val => addItem(val));
}

function getFilter(separator) {
    if (location.search.includes('?now'))
        return 'now' + separator;
    if (location.search.includes('?tomorrow'))
        return 'tomorrow' + separator;
    return '';
}

function searchSubmit() {
    // NOTE: We use client side filtering now
    //searchFor(document.getElementById('search-input').value);
}

function showLoading() {
    document.getElementById('loading').style.display = 'inline-block';
    document.getElementById('list-end').style.display = 'none';
}

function searchFor(keyword) {
    getForJsonObject('api/items/' + getFilter('/'))
        .then(function (data) {
            searchResult = data;
            document.getElementById('search-input').value = keyword;
            filterSearch();
            document.getElementById('loading').style.display = 'none';
            document.getElementById('list-end').style.display = 'block';
        });
}

function updateUrl(keyword) {
    if (keyword == null) {
        window.history.pushState({
            route: '/items/' + (getFilter('') !== '' ? ('?' + getFilter('')) : '')
        }, document.title, '/items/' + (getFilter('') !== '' ? ('?' + getFilter('')) : ''));
    } else {
        window.history.pushState({
            route: '/items/?' + (getFilter('') !== '' ? (getFilter('') + '&') : '') + 'q=' + encodeURI(keyword)
        }, document.title, '/items/?' + (getFilter('') !== '' ? (getFilter('') + '&') : '') + 'q=' + encodeURI(keyword));
    }
}

function addItem(item) {
    document.getElementById('item-set').insertAdjacentHTML('beforeend', formatItem(item));
}

function clearAll() {
    document.getElementById('no-results').style.display = 'none';
    document.getElementById('item-set').innerHTML = '';
}

const STARS = [8, 100, 20, 21, 22, 23, 69, 2];
const FLAGS = [1010, 1069];

function formatItem(item) {
    return `
                <div class="item ${item.circleColor}${item.outOfStock ? ' item-out-of-stock' : ''}" onclick="showPopup(${item.id})">
                    <div class="picture" style="background-image: url('${URL_BASE}${item.imageName}');">
                        ${item.flag === 0 ? '' : `<div class="flag" style="background-image: url('${URL_BASE}flags/flag${item.flag}.png')"></div>`}
                        <div class="overlay">${item.outOfStock ? `<span class="out-of-stock">${LANG['outOfStock']}</span>` : ''}</div>
                    </div>
                    <h3 onclick="showPopup(${item.id})">${item.name}</h3>
                    <table>
                        <tr>
                            <td>${LANG['ingred']}:</td>
                            <td>${item.ingredients}</td>
                        </tr>
${appendCustom(item.detailsConfigJson)}
${item.price !== -1 ? `
                        <tr>
                            <td>${LANG['price']}:</td>
                            <td>${item.discountPrice === 0
        ? `<em class="normal-price">${item.price} ${LANG['currency']}</em>`
        : `<em class="strike">${item.price} ${LANG['currency']}</em> <em class="discount"><i class="material-icons">trending_down</i> ${item.discountPrice} ${LANG['currency']}</em>`} 
                            </td>
                        </tr>
` : ''}
                    </table>
                    <span>
					    ${!item.orderable ? '' : `
                        <a href="#" onclick="showPopup(${item.id}); return false">
                        <i class="material-icons">local_mall</i></a>
                        `}
                        
                        ${item.flag !== '1' ? '' : `<i class="material-icons a">fiber_new</i>`}
                        ${item.flag !== '100' ? '' : `<i class="material-icons a">bug_report</i>`}
                        ${!STARS.includes(item.flag) ? '' : `<i class="material-icons a">star</i>`}
                        ${!FLAGS.includes(item.flag) ? '' : `<i class="material-icons a">flag</i>`}
                        ${item.discountPrice === 0 ? '' : `<i class="material-icons a">trending_down</i>`}
                        
                        <a class="colored-light" href="${URL_BASE}provider/${item.circleAlias}">${item.circleName}</a>
                    </span>
                </div>`;
}

function appendCustom(json) {
    let custom;
    try {
        custom = JSON.parse(json);
    } catch (e) {
        console.error(e);
        return '<tr><td colspan="2">Invalid descriptor</td></tr>';
    }

    let result = '';
    custom.forEach(element => {
        if (element.values !== undefined && !element._hide) {
            result += `
                        <tr>
                            <td>${LANG[element.name]}:</td>
                            <td>${element._display ? element._display.replace('{pieces}', LANG.pieces) : element.values.join(', ')}</td>
                        </tr>`;
        }
    });
    return result;
}

const InputType = {
    EXTRA_SELECT: 'EXTRA_SELECT',
    EXTRA_CHECKBOX: 'EXTRA_CHECKBOX',
    AMERICANO_EXTRA: 'AMERICANO_EXTRA',
    AMERICANO_SELECT: 'AMERICANO_SELECT',
    PIZZASCH_SELECT: 'PIZZASCH_SELECT',
    ITEM_COUNT: 'ITEM_COUNT',
    DO_SELECT: 'DO_SELECT',
    KB_SELECT: 'KB_SELECT',
    AB_SELECT: 'AB_SELECT',
    AB_KB_SELECT: 'AB_KB_SELECT',
    DO_CHECKBOX: 'DO_CHECKBOX',
    KB_CHECKBOX: 'KB_CHECKBOX',
    AB_CHECKBOX: 'AB_CHECKBOX',
    AB_KB_CHECKBOX: 'AB_KB_CHECKBOX'
};

const CardType = {
    DO: 'DO',
    AB: 'AB',
    KB: 'KB'
};

function generateCustom(json, item) {
    let custom;
    try {
        custom = JSON.parse(json);
    } catch (e) {
        console.error(e);
        return;
    }
    if (manualOrder) {
        card = manualOrderCard;
    }

    let result = '';
    custom.forEach(element => {
        if (typeof element.values !== 'undefined') {
            if (item.price < 0) {
                for (let i = 0; i < element.values.length; i++) {
                    element.prices[i] = 0;
                }
            }

            if (element.type === InputType.EXTRA_SELECT) {
                result += generateExtraSelect(element);
            } else if (element.type === InputType.DO_SELECT) {
                if (card === CardType.DO)
                    result += generateExtraSelect(element);
            } else if (element.type === InputType.KB_SELECT) {
                if (card === CardType.KB)
                    result += generateExtraSelect(element);
            } else if (element.type === InputType.AB_SELECT) {
                if (card === CardType.AB)
                    result += generateExtraSelect(element);
            } else if (element.type === InputType.AB_KB_SELECT) {
                if (card === CardType.KB || card === CardType.AB)
                    result += generateExtraSelect(element);
            } else if (element.type === InputType.EXTRA_CHECKBOX) {
                result += generateExtraCheckbox(element);
            } else if (element.type === InputType.DO_CHECKBOX) {
                if (card === CardType.DO)
                    result += generateExtraCheckbox(element);
            } else if (element.type === InputType.AB_CHECKBOX) {
                if (card === CardType.AB)
                    result += generateExtraCheckbox(element);
            } else if (element.type === InputType.KB_CHECKBOX) {
                if (card === CardType.KB)
                    result += generateExtraCheckbox(element);
            } else if (element.type === InputType.AB_KB_CHECKBOX) {
                if (card === CardType.KB || card === CardType.AB)
                    result += generateExtraCheckbox(element);
            } else if (element.type === InputType.AMERICANO_SELECT) {
                result += generateExtraCheckbox(element);
            } else if (element.type === InputType.PIZZASCH_SELECT) {
                result += generatePizzaschSelect(element);
            } else if (element.type === InputType.ITEM_COUNT) {
                result += generateItemCount(element);
            } else {
                result += 'UNKNOWN TYPE: ' + element.type;
            }
        }
    });
    return result;
}

function generateTimes(timeWindows, categoryMax, extra = false) {
    let result = '';
    timeWindows.forEach(element => {
        if (element.name !== undefined) {
            result += `<option value="${element.id}">${element.name} (${extra ? element.extraItemCount : Math.max(0, Math.min(element.normalItemCount, categoryMax))}${LANG.pieces})</option>`;
        }
    });
    return result;
}

function generateExtraSelect(element) {
    let result = '';
    if (element.name.length > 0)
        result += `<label>${LANG[element.name]}</label>`;
    result += `<select name="${element.name}" data-prices="${element.prices}" onchange="itemChanged()" class="price-changer">`;
    for (let optionId = 0; optionId < element.values.length; optionId++) {
        let value = element.values[optionId];
        let price = element.prices[optionId];
        result += `
            <option value="${optionId}">${value}${price !== 0 ? ' (' + (price > 0 ? '+' : '') + price + ' ' + LANG['currency'] + ')' : ''}</option>`;
    }
    result += `
        </select>`;
    if (element._comment) {
        result += `
		<span class="comment select-comment">${element._comment}</span>`;
    }
    return result;

}

function generatePizzaschSelect(element) {
    let result = '';
    if (element.name.length > 0)
        result += `<label>${LANG[element.name]}</label>`;
    result += `<select name="${element.name}" data-prices="${element.prices}" onchange="itemChangedPizzasch()" class="price-changer" id="pizzasch-select">`;
    for (let optionId = 0; optionId < element.values.length; optionId++) {
        const value = element.values[optionId];
        const price = element.prices[optionId];
        result += `<option value="${optionId}">${value}${price !== 0 ? (' (' + (price > 0 ? '+' : '') + price + ' ' + LANG['currency'] + ')') : ''}</option>`;
    }
    result += `</select>`;
    if (element._comment) {
        result += `<span class="comment select-comment">${element._comment}</span>`;
    }
    return result;

}

function generateItemCount(element) {
    let result = '';
    if (element.name.length > 0)
        result += `<label>${LANG[element.name]}</label>`;
    result += `<div class="count-wrapper">
                <button class="input-count" onclick="changeCount(-1); return false">-</button>
                <input type="text" readonly min="${element.min}" max="${element.max}" name="${element.name}" value="${element.min}" id="popup-count" 
                onkeypress="limitNumber(this, ${element.min}, ${element.max}); itemChanged()" 
                onmousedown="limitNumber(this, ${element.min}, ${element.max}); itemChanged()"
                onmouseup="limitNumber(this, ${element.min}, ${element.max}); itemChanged()" 
                onchange="limitNumber(this, ${element.min}, ${element.max}); itemChanged()" />
                <button class="input-count" onclick="changeCount(1); return false">+</button>
            </div>`;
    if (element._comment) {
        result += `<span class="comment count-comment">${element._comment}</span>`;
    }
    return result;
}

function changeCount(count) {
    let popupCount = document.getElementById('popup-count');
    popupCount.value = (popupCount.value === "" ? 1 : parseInt(popupCount.value)) + parseInt(count);
    limitNumber(popupCount, parseInt(popupCount.min), parseInt(popupCount.max));
    itemChanged();
}

function generateExtraCheckbox(element) {
    let result = '';
    if (element.name.length > 0)
        result += `<label>${LANG[element.name]}</label>`;
    result += `<div class="component">`;
    for (let optionId = 0; optionId < element.values.length; optionId++) {
        const value = element.values[optionId];
        const price = element.prices[optionId];
        result += `
            <label class="checkcontainer">${value}${price !== 0 ? ' (' + (price > 0 ? '+' : '') + price + '&nbsp;' + LANG['currency'] + ')' : ''}
                <input type="checkbox" name="${element.name}_${optionId}" data-price="${price}" onchange="itemChanged()" class="price-changer"/>
                <span class="checkmark"></span>
            </label>`;
    }
    if (element._comment) {
        result += `<span class="comment checkbox-comment">${element._comment}</span>`;
    }
    result += `</div>`;
    return result;
}

function itemChanged() {
    let price = parseInt(document.getElementById('popup-price').getAttribute('data-base'));
    document.querySelectorAll('.form-order .price-changer').forEach(element => {
        if (element.getAttribute('data-price'))
            price += element.checked ? parseInt(element.getAttribute('data-price')) : 0;
        else if (element.getAttribute('data-prices'))
            price += parseInt(element.getAttribute('data-prices').split(',')[parseInt(element.value)]);
    });
    let countElement = document.getElementById('popup-count');
    price = (countElement !== null && typeof countElement !== "undefined" && countElement.value !== "") ? (price * countElement.value) : price;

    document.getElementById('popup-price').innerText = `${price} ${LANG['currency']}`;
}

function itemChangedPizzasch() {
    itemChanged();
    document.getElementById('popup-timewindows').innerHTML = generateTimes(latestData.timeWindows, latestData.categoryMax, false);
}

function showPopup(id) {
    getForJsonObject('api/item/' + id + (manualOrder ? ('?explicitOpening=' + OPENING_ID) : ''))
        .then(function (data) {
            disableScroll();
            latestData = data;
            document.getElementById('popup-title').innerText = data.name;
            document.getElementById('popup-header').style.backgroundImage = `url('${URL_BASE}${data.circleIcon}')`;
            document.getElementById('popup-image').style.backgroundImage = `url('${URL_BASE}${data.imageName}')`;
            document.getElementById('popup-description').innerHTML = (data.description
                .replaceAll('#h#', '<h5>')
                .replaceAll('#/h#', '</h5><br>')
                .replaceAll('#ls#', '<li>- ')
                .replaceAll('#/ls#', '</li>')
                .replaceAll('#br#', '</br>')
                .replaceAll('#b#', '<b>')
                .replaceAll('#/b#', '</b>'));
            document.getElementById('popup-price-container').style.display = data.price !== -1 ? 'inline' : 'none';
            const price = data.discountPrice === 0 ? data.price : data.discountPrice;

            const priceElement = document.getElementById('popup-price');
            priceElement.innerText = `${price} ${LANG['currency']}`;
            priceElement.setAttribute('data-base', price);

            document.getElementById('popup-price-tag').innerText = LANG[data.discountPrice === 0 ? 'price' : 'priceDiscounted'];
            document.getElementById('popup-window').classList.add(data.circleColor);
            document.getElementById('popup-custom').innerHTML = generateCustom(data.detailsConfigJson, data);

            const timeWindowsElement = document.getElementById('popup-timewindows');
            timeWindowsElement.innerHTML = generateTimes(data.timeWindows, data.categoryMax);
            timeWindowsElement.style.display = data.timeWindows.length > 1 ? 'block' : 'none';

            document.getElementById('popup-comment').value = '';
            document.getElementById('popup-orderable-block').style.display = (manualOrder || (data.orderable && !data.personallyOrderable)) ? 'block' : 'none';
            if (document.getElementById('popup-not-orderable'))
                document.getElementById('popup-not-orderable').style.display = (manualOrder || data.orderable || data.personallyOrderable) ? 'none' : 'block';
            if (document.getElementById('popup-personally'))
                document.getElementById('popup-personally').style.display = (manualOrder || !data.personallyOrderable) ? 'none' : 'block';

            if (manualOrder) {
                document.getElementById('noManualOrderRoom').style.display = 'none';
                document.getElementById('manualOrderRoom').style.display = 'block';
                document.getElementById('manualOrderRoom').innerHTML = LANG.manualOrderRoomMessage.replace('$1', manualOrderUser).replace('$2', manualOrderRoom);
            } else {
                document.getElementById('noManualOrderRoom').style.display = 'block';
                document.getElementById('manualOrderRoom').style.display = 'none';
            }

            document.getElementById('popup').classList.remove('inactive');
            document.getElementById('blur-section').classList.add('blur');
            selectedItem = data;
            if (document.getElementById('submit-order-button') != null)
                document.getElementById('submit-order-button').disabled = false;
        });
}

function closePopup(purchased = false) {
    if (!purchased)
        document.getElementById('blur-section').classList.remove('blur');
    document.getElementById('popup').classList.add('inactive');
    document.getElementById('popup-window').classList = 'popup';
    selectedItem = null;
    enableScroll();
}

function packDetails() {
    if (selectedItem === null)
        return '{answers: []}';

    let result = [];
    let custom = JSON.parse(selectedItem.detailsConfigJson);
    if (manualOrder) {
        card = manualOrderCard;
    }
    custom.forEach(element => {
        if (element.values !== undefined) {
            if (element.type === InputType.EXTRA_SELECT) {
                result.push({
                    type: InputType.EXTRA_SELECT,
                    name: element.name,
                    selected: [document.querySelector(`select[name='${element.name}']`).value]
                });
            } else if (element.type === InputType.EXTRA_CHECKBOX) {
                result.push({
                    type: InputType.EXTRA_CHECKBOX,
                    name: element.name,
                    selected: getCheckboxChecked(element.name, element.values.length)
                });
            } else if (element.type === InputType.AMERICANO_EXTRA) {
                result.push({
                    type: InputType.AMERICANO_EXTRA,
                    name: element.name,
                    selected: getCheckboxChecked(element.name, element.values.length)
                });
            } else if (element.type === InputType.PIZZASCH_SELECT) {
                result.push({
                    type: InputType.PIZZASCH_SELECT,
                    name: element.name,
                    selected: [document.querySelector(`select[name='${element.name}']`).value]
                });
            } else if (element.type === InputType.AB_SELECT) {
                if (card === CardType.AB) {
                    result.push({
                        type: InputType.AB_SELECT,
                        name: element.name,
                        selected: [document.querySelector(`select[name='${element.name}']`).value]
                    });
                }
            } else if (element.type === InputType.KB_SELECT) {
                if (card === CardType.KB) {
                    result.push({
                        type: InputType.KB_SELECT,
                        name: element.name,
                        selected: [document.querySelector(`select[name='${element.name}']`).value]
                    });
                }
            } else if (element.type === InputType.DO_SELECT) {
                if (card === CardType.DO) {
                    result.push({
                        type: InputType.DO_SELECT,
                        name: element.name,
                        selected: [document.querySelector(`select[name='${element.name}']`).value]
                    });
                }
            } else if (element.type === InputType.AB_KB_SELECT) {
                if (card === CardType.KB || card === CardType.AB) {
                    result.push({
                        type: InputType.AB_KB_SELECT,
                        name: element.name,
                        selected: [document.querySelector(`select[name='${element.name}']`).value]
                    });
                }
            } else if (element.type === InputType.AB_CHECKBOX) {
                if (card === CardType.AB) {
                    result.push({
                        type: InputType.AB_CHECKBOX,
                        name: element.name,
                        selected: getCheckboxChecked(element.name, element.values.length)
                    });
                }
            } else if (element.type === InputType.KB_CHECKBOX) {
                if (card === CardType.KB) {
                    result.push({
                        type: InputType.KB_CHECKBOX,
                        name: element.name,
                        selected: getCheckboxChecked(element.name, element.values.length)
                    });
                }
            } else if (element.type === InputType.DO_CHECKBOX) {
                if (card === CardType.DO) {
                    result.push({
                        type: InputType.DO_CHECKBOX,
                        name: element.name,
                        selected: getCheckboxChecked(element.name, element.values.length)
                    });
                }
            } else if (element.type === InputType.AB_KB_CHECKBOX) {
                if (card === CardType.KB || card === CardType.AB) {
                    result.push({
                        type: InputType.AB_KB_CHECKBOX,
                        name: element.name,
                        selected: getCheckboxChecked(element.name, element.values.length)
                    });
                }
            }
        }
    });

    return JSON.stringify({answers: result});
}

function getCheckboxChecked(name, count) {
    let result = [];
    for (let i = 0; i < count; i++) {
        if (document.querySelector(`input[name=${name}_${i}]`).checked) {
            result.push(parseInt(i));
        }
    }
    return result;
}

const ResponseType = {
    ACK: 'ACK',
    INTERNAL_ERROR: 'INTERNAL_ERROR',
    OVERALL_MAX_REACHED: 'OVERALL_MAX_REACHED',
    MAX_REACHED: 'MAX_REACHED',
    MAX_REACHED_EXTRA: 'MAX_REACHED_EXTRA',
    NO_ORDERING: 'NO_ORDERING',
    NO_ROOM_SET: 'NO_ROOM_SET',
    CATEGORY_FULL: 'CATEGORY_FULL',
    TIME_WINDOW_INVALID: 'TIME_WINDOW_INVALID'
};

function packManualOrderDetails() {
    if (!manualOrder
        || typeof(manualOrderUserId) == 'undefined'
        || manualOrderUserId == null
        || typeof(manualOrderUser) == 'undefined'
        || manualOrderUser == null
        || typeof(manualOrderRoom) == 'undefined'
        || manualOrderRoom == null
        || typeof(manualOrderCard) == 'undefined'
        || manualOrderCard == null
    ) {
        return null;
    }
    return {
        id: manualOrderUserId,
        name: manualOrderUser,
        room: manualOrderRoom,
        card: manualOrderCard
    }
}

function buySelectedItem() {
    document.getElementById('submit-order-button').disabled = true;
    postForString('api/order', {
        id: selectedItem.id,
        time: document.querySelector('select[name="time"]').value,
        comment: document.getElementById('popup-comment').value,
        count: document.getElementById('popup-count') !== null ? document.getElementById('popup-count').value : 1,
        detailsJson: packDetails(),
        manualOrderDetails: packManualOrderDetails()
    }).then(function (data) {
        if (data === ResponseType.ACK) {
            closePopup(true);
            doneOrder();
        } else {
            console.error(data);
            if (data === ResponseType.INTERNAL_ERROR) {
                showMessageBox(LANG.internal);
            } else if (data === ResponseType.OVERALL_MAX_REACHED) {
                showMessageBox(LANG.orderFull);
            } else if (data === ResponseType.MAX_REACHED) {
                showMessageBox(LANG.intervalFull);
            } else if (data === ResponseType.MAX_REACHED_EXTRA) {
                showMessageBox(LANG.intervalFullExtra);
            } else if (data === ResponseType.NO_ORDERING) {
                showMessageBox(LANG.alreadyClosed);
            } else if (data === ResponseType.NO_ROOM_SET) {
                showMessageBox(LANG.noRoom);
            } else if (data === ResponseType.CATEGORY_FULL) {
                showMessageBox(LANG.categoryFull);
            } else if (data === ResponseType.TIME_WINDOW_INVALID) {
                showMessageBox(LANG.internal);
            } else {
                showMessageBox(data);
            }
        }

    }).catch(function (e) {
        showMessageBox(LANG.noInternetConnection);
        console.error('Cannot send POST request.');
        console.error(e)
    });
}

function doneOrder() {
    document.querySelector('.done').style.display = 'block';
    setTimeout(() => {
        document.querySelector('.done-circle').style.backgroundSize = '100%';
    }, 10);
    setTimeout(() => {
        document.querySelector('.done-tick').style.clipPath = 'polygon(0 0, 100% 0, 100% 100%, 0 100%)';
    }, 100);
    setTimeout(() => {
        beepSound.play();
    }, 300);
    setTimeout(() => {
        let doneElement = document.querySelector('.done');
        doneElement.style.top = '20vh';
        doneElement.style.opacity = '0';
        document.querySelector('.done-tick').style.clipPath = 'polygon(0 0, 0 0, 0 100%, 0% 100%)';
        document.getElementById('blur-section').classList.remove('blur');
    }, 2000);
    setTimeout(() => {
        document.querySelector('.done-circle').style.backgroundSize = '0.1%';
        let doneElement = document.querySelector('.done');
        doneElement.style.top = '50vh';
        doneElement.style.opacity = '1';
        doneElement.style.display = 'none';
    }, 3500);
}

function closeMessageBox() {
    document.querySelector('.messagebox').style.display = 'none';
}

function showMessageBox(message) {
    document.getElementById('messagebox-text').innerText = message;
    document.querySelector('.messagebox').style.display = 'inline-block';
    if (document.getElementById('submit-order-button') != null)
        document.getElementById('submit-order-button').disabled = false;
}

function disableScroll() {
    document.body.style.overflowY = 'hidden';
}

function enableScroll() {
    document.body.style.overflowY = 'scroll';
}

function limitNumber(element, min, max) {
    if (element.value === "")
        return;
    if (element.value > max)
        element.value = max;
    else if (element.value < min)
        element.value = min;
}

function getForJsonObject(path) {
    return fetch(URL_BASE + path, {
        method: 'GET',
        mode: 'no-cors',
        cache: 'no-cache',
        credentials: 'same-origin',
        headers: {
            'Accept': 'application/json'
        },
        redirect: 'follow',
        referrerPolicy: 'no-referrer',
    }).then(response => response.json());
}

function postForJsonObject(path, data) {
    return fetch(URL_BASE + path, {
        method: 'POST',
        cache: 'no-cache',
        credentials: 'same-origin',
        headers: {
            'Accept': 'application/json',
            'Content-type': 'application/json;charset=UTF-8'
        },
        redirect: 'follow',
        referrerPolicy: 'no-referrer',
        body: JSON.stringify(data)
    }).then(response => response.json());
}

function postForString(path, data) {
    return fetch(URL_BASE + path, {
        method: 'POST',
        cache: 'no-cache',
        credentials: 'same-origin',
        headers: {
            'Accept': 'text/plain;charset=UTF-8',
            'Content-type': 'application/json;charset=UTF-8'
        },
        redirect: 'follow',
        referrerPolicy: 'no-referrer',
        body: JSON.stringify(data)
    }).then(response => response.text());
}

document.addEventListener('DOMContentLoaded', function () {
    window.addEventListener('click', event => {
        if (event.target === document.getElementById('popup'))
            closePopup();
    });
    document.addEventListener('keyup', event => {
        if (event.keyCode === 27)
            closePopup();
    });
});

String.prototype.replaceAll = function (search, replacement) {
    const target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};