var page = 0;
var endReached = false;
var URL_BASE = "/";

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
	'						<tr>\n' +
	'							<td>Méret:</td>\n' +
	'							<td>32</td>\n' +
	'						</tr>\n' +
	'						<tr>\n' +
	'							<td>Csípősség:</td>\n' +
	'							<td>Perem</td>\n' +
	'						</tr>\n' +
	'						<tr>\n' +
	'							<td>Ár:</td>\n' +
	'							<td>' + item.price + ' JMF</td>\n' +
	'						</tr>\n' +
	'					</table>\n' +
	'					<span>\n' +
	'						<a href="#" onclick="showPopup(' + item.id + '); return false">' +
							'<i class="material-icons">add_shopping_cart</i></a>\n' +
	'						<a href="#" onclick="showPopup(' + item.id + '); return false">' + 
							'<i class="material-icons">assignment</i></a>\n' +
	'						<a href="' + URL_BASE + 'cdn/items/' + item.circleId + '">' + item.circleName + '</a>\n' +
	'					</span>\n' +
	'				</div>';
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
			$("#popup-price").text(data.price + " JMF");
			$("#popup-window").addClass(data.circleColor);
			
			$("#popup").removeClass("inactive");
			$("#blur-section").addClass("blur");
		}
	});
}

function closePopup() {
	$("#blur-section").removeClass("blur");
	$("#popup").addClass("inactive");
	$("#popup-window").attr("class", "popup");
}

$(window).scroll(function() {
	if ($(window).scrollTop() == $(document).height()
			- $(window).height()
			&& !endReached) {
		appendNext();
	}
});

$(window).click(function() {
	if (event.target == $("#popup")[0]) {
		closePopup();
	}
});