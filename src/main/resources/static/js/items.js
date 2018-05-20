var page = 0;
var endReached = false;
var selectedItem = null;

function appendNext() {
	if (page === 0)
		clearAll();
	$.ajax({
		dataType: "json",
		url: URL_BASE + "api/items/" + (page++),
		success: function(data) {
			if (data.length === 0) {
				endReached = true;
				$("#loading").css({display: "none"});
				$("#list-end").css({display: "inline-block"});
				return;
			}
			for (var item in data)
				addItem(data[item]);
			$("#loading").css({display: "none"});
		}
	});
}

function searchSubmit() {
	searchFor($("#search-input").val());
}

function searchFor(keyword) {
	if (keyword.length === 0) {
		endReached = false;
		page = 0;
		appendNext();
		return;
	}
		
	clearAll();
	updateUrl(keyword);
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
			for ( var item in data)
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
	window.history.pushState({
		route : "/items/?q=" + encodeURI(keyword)
	}, document.title, "/items/?q=" + encodeURI(keyword));
}

function addItem(item) {
	$("#item-set").append(formatItem(item));
}

function clearAll() {
	$("#no-results").css({display: "none"});
	$("#item-set").html("");
}

function formatItem(item) {
	return '' + 
	'				<div class="item ' + item.circleColor + '">\n' +
	'					<div class="picture" style="background-image: url(\'' +
									URL_BASE + 'cdn/items/' + item.imageName + '\');" ' +
									'onclick="showPopup(' + item.id + ')">\n' +
	'						<div class="overlay"></div>\n' +
	'					</div>\n' +
	'					<h3>' + item.name + '</h3>\n' +
	'					<table>\n' +
	'						<tr>\n' +
	'							<td>Feltét:</td>\n' +
	'							<td>' + item.ingredients + '</td>\n' +
	'						</tr>\n' +
	appendCustom(item.detailsConfigJson) +
	'						<tr>\n' +
	'							<td>Ár:</td>\n' +
	'							<td>' + item.price + ' ' + LANG['currency'] + '</td>\n' +
	'						</tr>\n' +
	'					</table>\n' +
	'					<span>\n' +
	'						<a href="#" onclick="showPopup(' + item.id + '); return false">' +
							'<i class="material-icons">add_shopping_cart</i></a>\n' +
	'						<a href="#" onclick="showPopup(' + item.id + '); return false">' + 
							'<i class="material-icons">assignment</i></a>\n' +
	'						<a class="colored-light" href="' + URL_BASE + 'circle/' + item.circleId + '">' + item.circleName + '</a>\n' +
	'					</span>\n' +
	'				</div>';
}

function appendCustom(json) {
	var custom = JSON.parse(json);

	var result = "";
	custom.forEach(element => {
		if (element.values !== undefined) {
			result += '' +
			'                       <tr>\n' +
			'							<td>' + LANG[element.name] + ':</td>\n' +
			'							<td>' + element.values.join(", ") + '</td>\n' +
			'						</tr>\n';
		}
	});
	return result;
}

function generateCustom(json) {
	var custom = JSON.parse(json);

	var result = "";
	custom.forEach(element => {
		if (element.values !== undefined) {
			result += '						<label>' + LANG[element.name] + '</label>\n' +
			'						<select name="' + element.name + '">\n';
			var optionId = 0;
			element.values.forEach(option => {
				result += '							<option value="' + (optionId++) + '">' + option + '</option>\n';
			});
			result += '						</select>\n';
		}
	});
	return result;
}

function showPopup(id) {
	$.ajax({
		dataType: "json",
		url: URL_BASE + "api/item/" + id,
		success: function(data) {
			$("#popup-title").text(data.name);
			$("#popup-header").css({"background-image": "url('" + URL_BASE + "cdn/items/" + data.imageName + "')"});
			$("#popup-image").css({"background-image": "url('" + URL_BASE + "cdn/items/" + data.imageName + "')"});
			$("#popup-description").text(data.description);
			$("#popup-price").text(data.price + " " + LANG['currency']);
			$("#popup-window").addClass(data.circleColor);
			$("#popup-custom").html(generateCustom(data.detailsConfigJson));
			$("#popup-comment").val("");
			
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
		return "{}";

	var result = {};
	var custom = JSON.parse(selectedItem.detailsConfigJson);
	custom.forEach(element => {
		result[element.name] = $("select[name='" + element.name + "']").val();
	});

	return JSON.stringify(result);
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
	}).done(function() {
		closePopup(true);
		doneOrder();
	}).fail(function(e) {
		console.error("Cannot send POST request.");
	});
}

function doneOrder() {
		$(".done").css({"display": "block"});
	$(".done-circle").css({"background-size": "100%"});
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
		$(".done-circle").css({"background-size": "0%"});
		$(".done").css({"top": "50vh", "opacity": "1", "display": "none"});
	}, 3500);
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