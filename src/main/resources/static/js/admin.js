function updateItem(id, status) {
	$.post({
		dataType: "text",
		url: URL_BASE + "configure/order/update",
        data: {id: id, status: status}
	}).done(function(data) {
		console.log(data);
    	location.reload();
	}).fail(function(e) {
        console.error(e);
		console.error("Cannot send UPDATE request.");
	});
}