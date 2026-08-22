const userAjaxUrl = "admin/users/";

const ctx = {
    ajaxUrl: userAjaxUrl
};

$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "rowId": "id",
            "columns": [
                {"data": "name"},
                {"data": "email"},
                {"data": "roles"},
                {"data": "enabled"},
                {"data": "registered"},
                {
                    "data": null,
                    "orderable": false,
                    "render": function () {
                        return '<a><span class="fa fa-pencil"></span></a>';
                    }
                },
                {
                    "data": null,
                    "orderable": false,
                    "render": function () {
                        return '<a class="delete"><span class="fa fa-remove"></span></a>';
                    }
                }
            ],
            "order": [[0, "asc"]]
        })
    );
});

function enable(checkbox, id) {
    const active = checkbox.checked;
    $.ajax({
        url: ctx.ajaxUrl + id + "?active=" + active,
        type: "PATCH"
    }).done(function () {
        $(checkbox).closest("tr").toggleClass("disabled-user", !active);
        successNoty(active ? "Enabled" : "Disabled");
    }).fail(function () {
        checkbox.checked = !active;
    });
}