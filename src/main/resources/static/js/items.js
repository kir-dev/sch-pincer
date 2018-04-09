var page = 0;
var endReached = false;
var URL_BASE = "/";

function appendNext() {
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

function addItem(item) {
	$("#item-set").append(formatItem(item));
}

function formatItem(item) {
	return '' + 
	'				<div class="item ' + item.circleColor + '">\n' +
	'					<div class="picture" style="background-image: url(\'' + URL_BASE + 'cdn/items/' + item.imageName + '\');">\n' +
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
	'						<a href="#"><i class="material-icons">add_shopping_cart</i></a>\n' +
	'						<a href="#"><i class="material-icons">assignment</i></a>\n' +
	'						<a href="#" th:href="@{/circle/pizzas}">Pizzásch</a>\n' +
	'					</span>\n' +
	'				</div>';
}

$(document).ready(function() { appendNext(); });

$(window).scroll(function() {
    if ($(window).scrollTop() == $(document).height() - $(window).height() && !endReached) {
    	appendNext();
    }
});