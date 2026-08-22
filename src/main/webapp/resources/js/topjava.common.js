let form;

function makeEditable(datatableApi) {
    ctx.datatableApi = datatableApi;
    form = $('#detailsForm');
    $("#datatable").on("click", ".delete", function () {
        if (confirm('Are you sure?')) {
            deleteRow($(this).closest('tr').attr("id"));
        }
    });

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    });

    $.ajaxSetup({cache: false});
}

function add() {
    form.find(":input").val("");
    $("#editRow").modal();
}

function deleteRow(id) {
    $.ajax({
        url: ctx.ajaxUrl + id,
        type: "DELETE"
    }).done(function () {
        updateTable();
        successNoty("Deleted");
    });
}

function fillTable(data) {
    ctx.datatableApi.clear().rows.add(data).draw();
}

function updateTable() {
    let url = ctx.updateUrl || ctx.ajaxUrl;
    let params = (typeof getTableParams === "function") ? getTableParams() : undefined;
    $.get(url, params, function (data) {
        fillTable(data);
    });
}

function save() {
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl,
        data: form.serialize()
    }).done(function () {
        $("#editRow").modal("hide");
        updateTable();
        successNoty("Saved");
    });
}

let failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(text) {
    closeNoty();
    new Noty({
        text: " " + text,
        type: 'success',
        layout: "bottomRight",
        timeout: 1000
    }).show();
}

function failNoty(jqXHR) {
    closeNoty();
    failedNote = new Noty({
        text: " Error status: " + jqXHR.status,
        type: "error",
        layout: "bottomRight"
    });
    failedNote.show();
}