<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<html>
<head>
    <title>Meals</title>
</head>
<body>
<h2>Meals</h2>

<form method="get" action="meals">
    <input type="hidden" name="action" value="filter">
    From date: <input type="date" name="startDate">
    To date: <input type="date" name="endDate">
    From time: <input type="time" name="startTime">
    To time: <input type="time" name="endTime">
    <button type="submit">Filter</button>
</form>
<br>

<table border="1" cellpadding="8">
    <tr>
        <th>Date/Time</th>
        <th>Description</th>
        <th>Calories</th>
        <th>Excess</th>
        <th>Action</th>
    </tr>
    <c:forEach var="meal" items="${meals}">
        <tr style="${meal.excess ? 'color: red' : ''}">
            <td>${fn:formatDateTime(meal.dateTime)}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td>${meal.excess}</td>
            <td>
                <a href="meals?action=update&id=${meal.id}">Edit</a>
                <a href="meals?action=delete&id=${meal.id}">Delete</a>
            </td>
        </tr>
    </c:forEach>
</table>
<br>
<a href="meals?action=create">Add new meal</a>
</body>
</html>