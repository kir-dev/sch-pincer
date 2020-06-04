function updateItem(id, status) {
    fetch(`${URL_BASE}configure/order/update`,
        method: 'POST',
        body: JSON.stringify({ id, status })
    )
    .then(() => location.reload())
    .catch(err => {
        console.error("Cannot send UPDATE request!");
        console.error(err);
    });
}