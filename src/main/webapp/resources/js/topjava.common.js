let form;

function makeEditable(datatableApi) {
    ctx.datatableApi = datatableApi;
    form = $('#detailsForm');

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    });

    // solve problem with cache in IE: https://stackoverflow.com/a/4303862/548473
    $.ajaxSetup({cache: false});
}

function add() {
    $("#modalTitle").html(i18n["addTitle"]);
    form.find(":input").val("");
    $("#editRow").modal();
}

function updateRow(id) {
    form.find(":input").val("");
    $("#modalTitle").html(i18n["editTitle"]);
    $.get(ctx.ajaxUrl + id, function (data) {
        $.each(data, function (key, value) {
            if (key === "dateTime" && typeof value === "string") {
                value = value.replace("T", " ").substring(0, 16);
            }
            form.find(`input[name='${key}']`).val(value);
        });
        $('#editRow').modal();
    });
}

function deleteRow(id) {
    if (confirm(i18n['common.confirm'])) {
        $.ajax({
            url: ctx.ajaxUrl + id,
            type: "DELETE"
        }).done(function () {
            ctx.updateTable();
            successNoty("common.deleted");
        });
    }
}

function updateTableByData(data) {
    ctx.datatableApi.clear().rows.add(data).draw();
}

function save() {
    const dateTimeField = form.find("input[name='dateTime']");
    const uiValue = dateTimeField.val();
    if (uiValue) {
        let isoValue = uiValue.replace(" ", "T");
        if (isoValue.length === 16) {
            isoValue += ":00";
        }
        dateTimeField.val(isoValue);
    }

    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl,
        data: form.serialize()
    }).done(function () {
        $("#editRow").modal("hide");
        ctx.updateTable();
        successNoty("common.saved");
    }).always(function () {
        dateTimeField.val(uiValue);
    });
}

let failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(key) {
    closeNoty();
    new Noty({
        text: `<i class="fa fa-check"></i> ${i18n[key]}`,
        type: 'success',
        layout: "bottomRight",
        timeout: 1000
    }).show();
}

function renderEditBtn(data, type, row) {
    if (type === "display") {
        return `<a onclick="updateRow(${row.id})"><span class="fa fa-pencil"></span></a>`;
    }
}

function renderDeleteBtn(data, type, row) {
    if (type === "display") {
        return `<a onclick="deleteRow(${row.id})"><span class="fa fa-remove"></span></a>`;
    }
}

function failNoty(jqXHR) {
    closeNoty();
    failedNote = new Noty({
        text: `<i class="fa fa-warning"></i> ${i18n['common.errorStatus']}: ${jqXHR.status}${jqXHR.hasOwnProperty('responseJSON') ? ' ' + jqXHR.responseJSON : ''}`,
        type: "error",
        layout: "bottomRight"
    });
    failedNote.show();
}