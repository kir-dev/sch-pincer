var page = 0;
var endReached = false;
var selectedItem = null;

function appendNext() {
    if (page === 0)
        clearAll();
    	
//    $.ajax({
//        dataType: "json",
//        url: URL_BASE + "api/items/" + (page++),
//        success: function(data) {
//            if (data.length === 0) {
//                endReached = true;
//                $("#loading").css({display: "none"});
//                $("#list-end").css({display: "inline-block"});
//                return;
//            }
//            for (var item in data)
//                addItem(data[item]);
//            $("#loading").css({display: "none"});
//        }
//    });
    
    $.ajax({
        dataType : "json",
        url : URL_BASE + "api/items/" + getFilter("/"),
        success : function(data) {
            endReached = true;
            if (data.length === 0) {
                $("#loading").css({
                    display : "none"
                });
                $("#list-end").css({
                    display : "none"
                });
                $("#no-results").css({
                    display : "inline-block"
                });
                return;
            }
            for (var item in data)
                addItem(data[item]);
            $("#list-end").css({
                display : "inline-block"
            });
            $("#loading").css({
                display : "none"
            });
        }
    });
}

function getFilter(separator) {
	if (location.search.includes("?now"))
		return "now" + separator;
	if (location.search.includes("?tomorrow"))
		return "tomorrow" + separator;
	return "";
}

function searchSubmit() {
    searchFor($("#search-input").val());
}

function showLoading() {
    $("#loading").css({
        display : "inline-block"
    });
    $("#list-end").css({
        display : "none"
    });
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
    
    $.ajax({
        dataType : "json",
        url : URL_BASE + "api/search/?q=" + keyword,
        success : function(data) {
            endReached = true;
            if (data.length === 0) {
                $("#loading").css({
                    display : "none"
                });
                $("#list-end").css({
                    display : "none"
                });
                $("#no-results").css({
                    display : "inline-block"
                });
                return;
            }
            for (var item in data)
                addItem(data[item]);
            $("#list-end").css({
                display : "inline-block"
            });
            $("#loading").css({
                display : "none"
            });
        }
    });
}

function updateUrl(keyword) {
    if (keyword == null) {
        window.history.pushState({
            route : "/items/" + getFilter("")
        }, document.title, "/items/" + getFilter(""));        
    } else {
        window.history.pushState({
            route : "/items/?q=" + encodeURI(keyword)
        }, document.title, "/items/?q=" + encodeURI(keyword));
    }
}

function addItem(item) {
    $("#item-set").append(formatItem(item));
}

function clearAll() {
    $("#no-results").css({display: "none"});
    $("#item-set").html("");
}

function formatItem(item) {
    return `
                <div class="item ${item.circleColor}">
                    <div class="picture" style="background-image: url('${URL_BASE}${item.imageName}');" onclick="showPopup(${item.id})">
                        <div class="overlay"></div>
                    </div>
                    <h3>${item.name}</h3>
                    <table>
                        <tr>
                            <td>${LANG['ingred']}:</td>
                            <td>${item.ingredients}</td>
                        </tr>
${appendCustom(item.detailsConfigJson)}
                        <tr>
                            <td>${LANG['price']}:</td>
                            <td>${item.price} ${LANG['currency']}</td>
                        </tr>
                    </table>
                    <span>
					    <a href="#" onclick="showPopup(${item.id}); return false">
					    <i class="material-icons">assignment</i></a>
					    ${!item.orderable ? '' : `
                        <a href="#" onclick="showPopup(${item.id}); return false">
                        <i class="material-icons">local_mall</i></a>
                        `}
                        <a class="colored-light" href="${URL_BASE}circle/${item.circleAlias}">${item.circleName}</a>
                    </span>
                </div>`;
}

function appendCustom(json) {
    let custom = JSON.parse(json);

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

function generateCustom(json) {
    let custom;
    try {
        custom = JSON.parse(json);
    } catch (e) {
        console.error(e);
    }
    let result = "";
    custom.forEach(element => {
        if (element.values !== undefined) {
            if (element.type === "EXTRA_SELECT") {
                result += generateExtraSelect(element);
            } else if (element.type === "EXTRA_CHECKBOX") {
                result += generateExtraCheckbox(element);
            } else if (element.type === "AMERICANO_EXTRA") {
                result += generateExtraCheckbox(element);
            } else {
                result += 'UNKNOWN TYPE: ' + element.type;
            }
        }
    });
    return result;
}

function generateTimes(timeWindows) {
    let result = "";
    timeWindows.forEach(element => {
        if (element.name !== undefined) {
            result += `<option value="${element.id}">${element.name} (${element.normalItemCount}${LANG.pieces})</option>`;
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
            <option value="${optionId}">${value}${price != 0 ? ' (+' + price + ' ' + LANG['currency'] + ')' : ''}</option>`;
    }
    result += `
        </select>`;
    if (element._comment) {
    	result += `
		<span class="comment">${element._comment}</span>`;
    }
    return result;
    
}

function generateExtraCheckbox(element) {
    let result = "";
    result += `
        <label>${LANG[element.name]}</label><div class="component">`;
    for (var optionId = 0; optionId < element.values.length; optionId++) {
        let value = element.values[optionId];
        let price = element.prices[optionId];
        result += `
            <label class="checkcontainer">${value}${price != 0 ? ' (+' + price + ' ' + LANG['currency'] + ')' : ''}
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
    var price = Number($("#popup-price").attr("data-base"));
    $(".form-order .price-changer").each(function(index, value) {
        if ($(this).attr("data-price"))
            price += $(this).is(':checked') ? Number($(this).attr("data-price")) : 0;
        else if ($(this).attr("data-prices"))
            price += Number($(this).attr("data-prices").split(',')[Number($(this).val())]);
    });
    $("#popup-price").text(price + " " + LANG['currency']);
}

function showPopup(id) {
    $.ajax({
        dataType: "json",
        url: URL_BASE + "api/item/" + id,
        success: function(data) {
            $("#popup-title").text(data.name);
            $("#popup-header").css({"background-image": "url('" + URL_BASE + data.imageName + "')"});
            $("#popup-image").css({"background-image": "url('" + URL_BASE + data.imageName + "')"});
            $("#popup-description").text(data.description);
            $("#popup-price").text(data.price + " " + LANG['currency']);
            $("#popup-price").attr("data-base", data.price);
            $("#popup-window").addClass(data.circleColor);
            $("#popup-custom").html(generateCustom(data.detailsConfigJson));
            $("#popup-timewindows").html(generateTimes(data.timeWindows));
            $("#popup-comment").val("");
            $("#popup-orderable-block").css({display: data.orderable && !data.perosnallyOrderable ? "block" : "none"});
            $("#popup-not-orderable").css({display: data.orderable || data.personallyOrderable ? "none" : "block"});
            $("#popup-perosnally").css({display: data.personallyOrderable ? "block" : "none"});
            $("#popup-timewindows").css({display: data.timeWindows.length > 1 ? "block" : "none"});
            
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
}

function packDetails() {
    if (selectedItem === null)
        return "{answers: []}";

    let result = [];
    let custom = JSON.parse(selectedItem.detailsConfigJson);
    custom.forEach(element => {
        if (element.values !== undefined) {
            if (element.type === "EXTRA_SELECT") {
                result.push({
            		type: "EXTRA_SELECT",
            		name: element.name,
            		selected: [$(`select[name='${element.name}']`).val()]
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
	    } else if (data === "NO_ORDERING") {
	    	showMessageBox(LANG.alreadyClosed);
	    } else if (data === "NO_ROOM_SET") {
	    	showMessageBox(LANG.noRoom);
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
	$("#messagebox-text").text(message);
	$(".messagebox").css({display: "inline-block"});
}

$(window).scroll(function() {
    if ($(window).scrollTop() == $(document).height() - $(window).height()
            && !endReached) {
        appendNext();
    }
});

$(window).click(function() {
    if (event.target == $("#popup")[0]) {
        closePopup();
    }
});

$(document).keyup(function (e) {
    if (e.keyCode == 27) {
        closePopup();
    }
});