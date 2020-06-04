var page = 0;
var endReached = false;
var selectedItem = null;
var latestData;

function appendNext(profile = 0) {
    if (page === 0)
        clearAll();
    fetch(`${URL_BASE}api/items/${getFilter("/")}${(profile !== Number(0) ? '?circle=' + profile : ''}`)})
        .then(res => res.json())
        .then(data => {
            endReached = true;
            if (data.length === 0) {
                document.getElementById("loading").style.display = "none";
                document.getElementById("list-end").style.display = "none";
                document.getElementById("no-results").style.display = "inline-block";
                return;
            }
            for (var item in data)
                addItem(data[item]);
            document.getElementById("list-end").style.display = "inline-block";
            document.getElementById("loading").style.display = "none";
        })
}

function getFilter(separator) {
	if (location.search.includes("?now"))
		return `now${separator}`;
	if (location.search.includes("?tomorrow"))
		return `tomorrow${separator}`;
	return "";
}

function searchSubmit() {
    searchFor(document.getElementById("search-input").value);
}

function showLoading() {
    document.getElementById("search-input").style.display = "inline-block";
    document.getElementById("list-end").style.display = "none";
}

function searchFor(keyword) {
    if (keyword.length === 0) {
        endReached = false;
        page = 0;
        appendNext();
        showLoading();
        updateUrl(null);
        return;
    }
        
    clearAll();
    updateUrl(keyword);
    showLoading();
    fetch(`${URL_BASE}api/search/?q=${keyword}`)
        .then(res => res.json())
        .then(data => {
            endReached = true;
            if (data.length === 0) {
                document.getElementById("loading").style.display = "none";
                document.getElementById("list-end").style.display = "none";
                document.getElementById("no-results").style.display = "inline-block";
                return;
            }
            for (var item in data)
                addItem(data[item]);
            document.getElementById("list-end").style.display = "inline-block";
            document.getElementById("loading").style.display = "none";
        });
}

function updateUrl(keyword) {
    if (keyword == null) {
        window.history.pushState({
            route: `/items/${getFilter("")}`
        }, document.title, `/items/${getFilter("")}`);
    } else {
        window.history.pushState({
            route: `/items/?q=${encodeURI(keyword)}`
        }, document.title, `/items/?q=${encodeURI(keyword)}`);
    }
}

function addItem(item) {
    document.getElementById("item-set").innerHTML += formatItem(item);
}

function clearAll() {
    document.getElementById("no-results").style.display = "none";
    document.getElementById("item-set").innerHTML = "";
}

const STARS = [8, 100, 20, 21, 22, 23, 69, 2];
const FLAGS = [1010, 1069];

function formatItem(item) {
    return `
                <div class="item ${item.circleColor}">
                    <div class="picture" style="background-image: url('${URL_BASE}${item.imageName}');" onclick="showPopup(${item.id})">
                        ${item.flag === 0 ? '' : `<div class="flag" style="background-image: url('${URL_BASE}flags/flag${item.flag}.png')"></div>`}
                        <div class="overlay"></div>
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
`: ''}
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
        return "<tr><td colspan='2'>Invalid descriptor</td></tr>";
    }

    let result = "";
    custom.forEach(element => {
        if (element.values !== undefined && !element._hide) {
            result += `
                        <tr>
                            <td>${LANG[element.name]}:</td>
                            <td>${element._display ? element._display.replace("{pieces}",LANG.pieces) : element.values.join(", ")}</td>
                        </tr>`;
        }
    });
    return result;
}

function generateCustom(json, item) {
    let custom;
    try {
        custom = JSON.parse(json);
    } catch (e) {
        console.error(e);
        return;
    }
    let result = "";
    custom.forEach(element => {
        if (element.values !== undefined) {
            if (item.price < 0) {
                for (let i = 0; i < element.values.length; i++) {
                    element.prices[i] = 0;
                }
            }

            if (element.type === "EXTRA_SELECT") {
                result += generateExtraSelect(element);
            } else if (element.type === "EXTRA_CHECKBOX") {
                result += generateExtraCheckbox(element);
            } else if (element.type === "AMERICANO_SELECT") {
                result += generateExtraCheckbox(element);
            } else if (element.type === "EXTRA_SELECT") {
                result += generateExtraSelect(element);
            } else if (element.type === "PIZZASCH_SELECT") {
                result += generatePizzaschSelect(element);
            } else if (element.type === "ITEM_COUNT") {
                result += generateItemCount(element);
            } else {
                result += `UNKNOWN TYPE: ${element.type}`;
            }
        }
    });
    return result;
}

function generateTimes(timeWindows, categoryMax, extra = false) {
    let result = "";
    timeWindows.forEach(element => {
        if (element.name !== undefined) {
            result += `<option value="${element.id}">${element.name} (${extra ? element.extraItemCount : Math.min(element.normalItemCount, categoryMax)}${LANG.pieces})</option>`;
        }
    });
    return result;
}

function generateExtraSelect(element) {
    let result = "";
    result += `
        <label>${LANG[element.name]}</label>
        <select name="${element.name}" data-prices="${element.prices}" onchange="itemChanged()" class="price-changer">`;
    for (var optionId = 0; optionId < element.values.length; optionId++) {
        let value = element.values[optionId];
        let price = element.prices[optionId];
        result += `
            <option value="${optionId}">${value}${price !== 0 ? ' (+' + price + ' ' + LANG['currency'] + ')' : ''}</option>`;
    }
    result += `
        </select>`;
    if (element._comment) {
    	result += `
		<span class="comment">${element._comment}</span>`;
    }
    return result;
    
}

function generatePizzaschSelect(element) {
    let result = "";
    result += `
        <label>${LANG[element.name]}</label>
        <select name="${element.name}" data-prices="${element.prices}" onchange="itemChangedPizzasch()" class="price-changer" id="pizzasch-select">`;
    for (var optionId = 0; optionId < element.values.length; optionId++) {
        const value = element.values[optionId];
        const price = element.prices[optionId];
        result += `<option value="${optionId}">${value}${price !== 0 ? ' (+' + price + ' ' + LANG['currency'] + ')' : ''}</option>`;
    }
    result += `</select>`;
    if (element._comment) {
    	result += `<span class="comment">${element._comment}</span>`;
    }
    return result;
    
}

function generateItemCount(element) {
    let result = `
        <label>${LANG[element.name]}</label>
        <input type="number" min="${element.min}" max="${element.max}" name="${element.name}" value="${element.min}" id="popup-count" onkeypress="limitNumber(this, ${element.min}, ${element.max}); itemChanged()" onmousedown="limitNumber(this, ${element.min}, ${element.max}); itemChanged()"  onmouseup="limitNumber(this, ${element.min}, ${element.max}); itemChanged()" onchange="limitNumber(this, ${element.min}, ${element.max}); itemChanged()" />
    `;
    if (element._comment) {
        result += `<span class="comment">${element._comment}</span>`;
    }
    return result;
}

function generateExtraCheckbox(element) {
    let result = "";
    result += `<label>${LANG[element.name]}</label><div class="component">`;
    for (var optionId = 0; optionId < element.values.length; optionId++) {
        let value = element.values[optionId];
        let price = element.prices[optionId];
        result += `
            <label class="checkcontainer">${value}${price !== 0 ? ' (+' + price + ' ' + LANG['currency'] + ')' : ''}
                <input type="checkbox" name="${element.name}_${optionId}" data-price="${price}" onchange="itemChanged()" class="price-changer"/>
                <span class="checkmark"></span>
            </label>`;
    }
    if (element._comment) {
    	result += `
    		<span class="comment">${element._comment}</span>`;
    }
    result += `
        </div>`;
    return result;
}

function itemChanged() {
    let price = Number(document.getElementById("popup-price").getAttribute("data-base"));
    $(".form-order .price-changer").each(function(index, value) {
        if ($(this).attr("data-price"))
            price += $(this).is(':checked') ? Number($(this).attr("data-price")) : 0;
        else if ($(this).attr("data-prices"))
            price += Number($(this).attr("data-prices").split(',')[Number($(this).val())]);
    });
    const countElement = document.getElementById("popup-count");
    price = countElement.length !== 0 ? (price * countElement.value) : price;

    document.getElementById("popup-price").textContent = `${price} ${LANG["currency"]}`;
}

function itemChangedPizzasch() {
	itemChanged();
//	document.getElementById("popup-timewindows").
    $("#popup-timewindows").html(generateTimes(latestData.timeWindows, latestData.categoryMax,$("#pizzasch-select").prop('selectedIndex') !== 0));
}

function showPopup(id) {
    $.ajax({
        dataType: "json",
        url: URL_BASE + "api/item/" + id,
        success: function(data) {
        	disableScroll();
        	latestData = data;
            $("#popup-title").text(data.name);
            $("#popup-header").css({"background-image": "url('" + URL_BASE + data.circleIcon + "')"});
            $("#popup-image").css({"background-image": "url('" + URL_BASE + data.imageName + "')"});
            $("#popup-description").html(data.description
                .replaceAll("#h#", "<h5>")
                .replaceAll("#/h#", "</h5><br>")
                .replaceAll("#ls#", "<li>- ")
                .replaceAll("#/ls#", "</li>")
                .replaceAll("#br#", "</br>")
                .replaceAll("#b#", "<b>")
                .replaceAll("#/b#", "</b>"));
            $("#popup-price-container").css({display: data.price !== -1 ? "inline": "none"});
            let price = data.discountPrice === 0 ? data.price : data.discountPrice;
            $("#popup-price").text(price + " " + LANG['currency']).attr("data-base", price);
            $("#popup-price-tag").text(LANG[data.discountPrice === 0 ? 'price' : 'priceDiscounted']);
            $("#popup-window").addClass(data.circleColor);
            $("#popup-custom").html(generateCustom(data.detailsConfigJson, data));
            $("#popup-timewindows").html(generateTimes(data.timeWindows, data.categoryMax)).css({display: data.timeWindows.length > 1 ? "block" : "none"});
            $("#popup-comment").val("");
            $("#popup-orderable-block").css({display: data.orderable && !data.personallyOrderable ? "block" : "none"});
            $("#popup-not-orderable").css({display: data.orderable || data.personallyOrderable ? "none" : "block"});
            $("#popup-personally").css({display: data.personallyOrderable ? "block" : "none"});

            $("#popup").removeClass("inactive");
            $("#blur-section").addClass("blur");
            selectedItem = data;
        }
    });
}

function closePopup(purchased = false) {
    if (!purchased)
    $("#blur-section").removeClass("blur");
    $("#popup").addClass("inactive");
    $("#popup-window").attr("class", "popup");
    selectedItem = null;
    enableScroll();
}

function packDetails() {
    if (selectedItem === null)
        return "{answers: []}";

    let result = [];
    const custom = JSON.parse(selectedItem.detailsConfigJson);
    custom.forEach(element => {
        if (element.values !== undefined) {
            if (element.type === "EXTRA_SELECT") {
                result.push({
            		type: "EXTRA_SELECT",
            		name: element.name,
            		selected: [document.getElementById(`select[name='${element.name}']`).value]
            	});
            } else if (element.type === "EXTRA_CHECKBOX") {
                result.push({
            		type: "EXTRA_CHECKBOX",
            		name: element.name,
                	selected: getCheckboxChecked(element.name, element.values.length)
                });
            } else if (element.type === "AMERICANO_EXTRA") {
                result.push({
            		type: "AMERICANO_EXTRA",
            		name: element.name,
                	selected: getCheckboxChecked(element.name, element.values.length)
                });
            } else if (element.type === "PIZZASCH_SELECT") {
	            result.push({
	        		type: "PIZZASCH_SELECT",
	        		name: element.name,
	        		selected: [document.getElementById(`select[name='${element.name}']`).value]
	            });
            }
        }
    });

    return JSON.stringify({answers: result});
}

function getCheckboxChecked(name, count) {
	let result = [];
	for (let i = 0; i < count; i++) {
		if ($(`input[name=${name}_${i}]`).is(':checked')){
			result.push(Number(i));
		}
	}
	return result;
}

function buySelectedItem() {
    $.post({
        dataType: "text",
        url: URL_BASE + "api/order",
        data: {
            id: selectedItem.id,
            time: $("select[name='time']").val(),
            comment: $("#popup-comment").val(),
            count: $('#popup-count').length ? $('#popup-count').val() : 1,
            detailsJson: packDetails()
        }
    }).done(function(data) {
    	if (data === "ACK") {
	        closePopup(true);
	        doneOrder();
	    } else if (data === "INTERNAL_ERROR") {
	    	showMessageBox(LANG.internal);
	    } else if (data === "OVERALL_MAX_REACHED") {
	    	showMessageBox(LANG.orderFull);
	    } else if (data === "MAX_REACHED") {
	    	showMessageBox(LANG.intervalFull);
	    } else if (data === "MAX_REACHED_EXTRA") {
	    	showMessageBox(LANG.intervalFullExtra);
	    } else if (data === "NO_ORDERING") {
	    	showMessageBox(LANG.alreadyClosed);
	    } else if (data === "NO_ROOM_SET") {
	    	showMessageBox(LANG.noRoom);
        } else if (data === "CATEGORY_FULL") {
            showMessageBox(LANG.categoryFull);
	    } else {
	    	showMessageBox(data);
	    }
	    
    }).fail(function(e) {
    	showMessageBox(LANG.noInternetConnection);
        console.error("Cannot send POST request.");
    });
}

function doneOrder() {
    $(".done").css({"display": "block"});
    setTimeout(() => {
        $(".done-circle").css({"background-size": "100%"});
    }, 10);
    setTimeout(() => {
        $(".done-tick").css({"-webkit-clip-path": "polygon(0 0, 100% 0, 100% 100%, 0 100%)", 
                "clip-path": "polygon(0 0, 100% 0, 100% 100%, 0 100%)"});
    }, 100);
    setTimeout(() => {
        $(".done").css({"top": "20vh", "opacity": "0"});
        $(".done-tick").css({"-webkit-clip-path": "polygon(0 0, 0 0, 0 100%, 0% 100%)", 
                "clip-path": "polygon(0 0, 0 0, 0 100%, 0% 100%)"});
        $("#blur-section").removeClass("blur");
    }, 2000);
    setTimeout(() => {
        $(".done-circle").css({"background-size": "0.1%"});
        $(".done").css({"top": "50vh", "opacity": "1", "display": "none"});
    }, 3500);
}

function closeMessageBox() {
	$(".messagebox").css({display: "none"});
}

function showMessageBox(message) {
	document.getElementById("messagebox-text").textContent = message;
	$(".messagebox").css({display: "inline-block"});
}

function disableScroll() {
    document.getElementsByTagName("body")[0].style.overflow-y = "hidden";
}

function enableScroll() {
    document.getElementsByTagName("body")[0].style.overflow-y = "scroll";
}

function limitNumber(element, min, max) {
    if (element.value > max)
        element.value = max;
    else if (element.value < min)
        element.value = min;
}

window.addEventListener("scroll", function() {
    if ($(window).scrollTop() === $(document).height() - $(window).height() && !endReached) {
        appendNext();
    }
});

window.addEventListener("click", function() {
    if (event.target === document.getElementById("popup")) {
        closePopup();
    }
});

document.addEventListener("keyup", e => {
    if (e.keyCode === 27) {
        closePopup();
    }
});

String.prototype.replaceAll = function(search, replacement) {
    const target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};