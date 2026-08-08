# REST API curl examples

## Meals (`/rest/meals`)

### Get all meals of the current user (sorted by date, with excess calculated)
```bash
curl -s http://localhost:8080/topjava/rest/meals
```

### Get a single meal by id
```bash
curl -s http://localhost:8080/topjava/rest/meals/100003
```

### Get meals filtered by date and time range
```bash
curl -s "http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-30&startTime=00:00&endDate=2020-01-31&endTime=23:00"
```

### Create a new meal (id is generated automatically)
```bash
curl -si -X POST http://localhost:8080/topjava/rest/meals -H "Content-Type: application/json" -d '{"dateTime":"2020-02-01T18:00:00","description":"New dinner","calories":500}'
```

### Update an existing meal by id
```bash
curl -si -X PUT http://localhost:8080/topjava/rest/meals/100012 -H "Content-Type: application/json" -d '{"id":100012,"dateTime":"2020-02-01T18:00:00","description":"Updated dinner","calories":600}'
```

### Delete a meal by id
```bash
curl -si -X DELETE http://localhost:8080/topjava/rest/meals/100012
```