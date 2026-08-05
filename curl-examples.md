# REST API curl examples

## Meals (`/rest/meals`)

```bash
curl -s http://localhost:8080/topjava/rest/meals
```

```bash
curl -s http://localhost:8080/topjava/rest/meals/100003
```

```bash
curl -s "http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-30&startTime=00:00&endDate=2020-01-31&endTime=23:00"
```

```bash
curl -si -X POST http://localhost:8080/topjava/rest/meals -H "Content-Type: application/json" -d '{"dateTime":"2020-02-01T18:00:00","description":"New dinner","calories":500}'
```

```bash
curl -si -X PUT http://localhost:8080/topjava/rest/meals/100012 -H "Content-Type: application/json" -d '{"id":100012,"dateTime":"2020-02-01T18:00:00","description":"Updated dinner","calories":600}'
```

```bash
curl -si -X DELETE http://localhost:8080/topjava/rest/meals/100012
```