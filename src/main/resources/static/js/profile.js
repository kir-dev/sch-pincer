function showEdit() {
    $("#room").css({display: "none"});
    $("#room-edit").css({display: "none"});
    $("#room-form").css({display: "block"});
}

function showRoom(room) {
    $("#room").text("SCH " + room);
    $("#room").css({display: "inline-block"});
    $("#room-edit").css({display: "inline-block"});
    $("#room-form").css({display: "none"});
}

function setRoom() {
	$.post({
		dataType: "text",
		url: URL_BASE + "api/user/room",
		data: {room: $("#room-setter").val()}
	}).done(function() {
    	showRoom($("#room-setter").val());
	}).fail(function(e) {
		console.error("Cannot send POST request.");
	});
}

//function cancelItem(id) {
//	$.post({
//		dataType: "text",
//		url: URL_BASE + "api/order/delete",
//        data: {id: id}
//	}).done(function() {
//    	location.reload();
//	}).fail(function(e) {
//        console.error(e);
//		console.error("Cannot send DELETE request.");
//	});
//}