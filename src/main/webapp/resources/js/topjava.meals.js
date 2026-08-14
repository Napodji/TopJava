const mealAjaxUrl = "rest/profile/meals/";

const ctx = {
    ajaxUrl: mealAjaxUrl
};

$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ]
        })
    );
});

function save() {
    let meal = {
        id: $("#id").val() || null,
        dateTime: $("#dateTime").val(),
        description: $("#description").val(),
        calories: $("#calories").val()
    };

    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl,
        contentType: "application/json",
        data: JSON.stringify(meal)
    }).done(function () {
        $("#editRow").modal("hide");
        updateTable();
        successNoty("Saved");
    });
}

function filterMeals() {
    $.ajax({
        url: ctx.ajaxUrl + "filter",
        type: "GET",
        data: $("#filterForm").serialize()
    }).done(function (data) {
        ctx.datatableApi.clear().rows.add(data).draw();
    });
}

function resetFilter() {
    $("#filterForm").find(":input").val("");
    updateTable();
}

function updateTable() {
    let filterParams = $("#filterForm").serialize();

    $.ajax({
        url: ctx.ajaxUrl + "filter",
        type: "GET",
        data: filterParams
    }).done(function (data) {
        ctx.datatableApi.clear().rows.add(data).draw();
    });
}