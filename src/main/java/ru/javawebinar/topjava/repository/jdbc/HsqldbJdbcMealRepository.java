package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
@Profile(Profiles.HSQL_DB)
public class HsqldbJdbcMealRepository extends AbstractJdbcMealRepository<Timestamp> {

    private static final RowMapper<Meal> ROW_MAPPER = (rs, rowNum) -> new Meal(
            rs.getInt("id"),
            rs.getTimestamp("date_time").toLocalDateTime(),
            rs.getString("description"),
            rs.getInt("calories"));

    @Autowired
    public HsqldbJdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    protected RowMapper<Meal> getRowMapper() {
        return ROW_MAPPER;
    }

    @Override
    protected Timestamp dateTimeToParam(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }
}