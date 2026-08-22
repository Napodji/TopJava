const mealAjaxUrl = "meals/";

const ctx = {
    ajaxUrl: mealAjaxUrl,
    updateUrl: mealAjaxUrl + "filter"
};

$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "rowId": "id",
            "columns": [
                {"data": "dateTime"},
                {"data": "description"},
                {"data": "calories"},
                {
                    "data": null,
                    "orderable": false,
                    "render": function () {
                        return '<a class="delete"><span class="fa fa-remove"></span></a>';
                    }
                }
            ],
            "order": [[0, "desc"]],
            "createdRow": function (row, data) {
                $(row).attr("data-meal-excess", data.excess);
            }
        })
    );
});

function getTableParams() {
    return $("#filterForm").find(":input").filter(function () {
        return $(this).val() !== "";
    }).serialize();
}

function filterMeals() {
    updateTable();
}

function resetFilter() {
    $("#filterForm").find(":input").val("");
    updateTable();
}