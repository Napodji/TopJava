<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Meal</title>
</head>
<body>
<h3>${meal.id == null ? 'Create' : 'Update'} meal</h3>
<form method="post" action="meals">
    <input type="hidden" name="id" value="${meal.id}">

    <label>Date/Time:
        <input type="datetime-local" name="dateTime"
               value="${meal.dateTime}" required>
    </label>
    <br>
    <label>Description:
        <input type="text" name="description"
               value="${meal.description}" required>
    </label>
    <br>
    <label>Calories:
        <input type="number" name="calories"
               value="${meal.calories}" required>
    </label>
    <br><br>
    <button type="submit">Save</button>
    <a href="meals">Cancel</a>
</form>
</body>
</html>