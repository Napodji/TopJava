<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Meals</title>
</head>
<body>
<h3>Meals</h3>
<a href="meals?action=create">Add meal</a>
<br><br>
<table border="1" cellpadding="8">
    <tr>
        <th>Date/Time</th>
        <th>Description</th>
        <th>Calories</th>
        <th>Action</th>
    </tr>
    <c:forEach var="meal" items="${meals}">
        <tr style="background-color: ${meal.excess ? '#ffcccc' : '#ccffcc'}">
            <td>${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td>
                <a href="meals?action=update&id=${meal.id}">Edit</a>
                <a href="meals?action=delete&id=${meal.id}">Delete</a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>