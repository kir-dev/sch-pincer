function showEdit() {
    document.getElementById("room").style.display = "none";
    document.getElementById("room-edit").style.display = "none";
    document.getElementById("room-form").style.display = "block";
    document.getElementById("saved").style.display = "none";
}

function showRoom(room) {
    const roomEl = document.getElementById("room");
    roomEl.textContent = room;
    roomEl.style.display = "inline-block";
    document.getElementById("room").style.display = "inline-block";
    document.getElementById("room-form").style.display = "none";
    document.getElementById("saved").style.display = "inline-block";
}

function setRoom() {
    const room = document.getElementById("room-setter").value;
    fetch(`${URL_BASE}api/user/room`,
        method: "POST",
        body: JSON.stringify({ room })
    )
    .then(() => showRoom(room))
    .catch(e => {
        console.error("Cannot send POST request.");
        console.error(e);
    });
}

function cancelItem(id) {
    fetch(`${URL_BASE}api/order/delete`,
        method: "POST",
        body: JSON.stringify({ id })
    )
    .then(() => location.reload())
    .catch(e => {
        console.error("Cannot send DELETE request.");
        console.error(e);
    });
}