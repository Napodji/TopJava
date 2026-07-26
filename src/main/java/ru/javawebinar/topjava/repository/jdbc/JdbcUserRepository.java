package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.javawebinar.topjava.util.ValidationUtil.validate;

@Repository
public class JdbcUserRepository implements UserRepository {

    private static final String SELECT_ALL =
            "SELECT u.id, u.name, u.email, u.password, u.registered, u.enabled, u.calories_per_day, ur.role " +
                    "FROM users u LEFT JOIN user_role ur ON u.id = ur.user_id " +
                    "ORDER BY u.name, u.email";

    private static final String SELECT_BY_ID =
            "SELECT u.id, u.name, u.email, u.password, u.registered, u.enabled, u.calories_per_day, ur.role " +
                    "FROM users u LEFT JOIN user_role ur ON u.id = ur.user_id " +
                    "WHERE u.id=?";

    private static final String SELECT_BY_EMAIL =
            "SELECT u.id, u.name, u.email, u.password, u.registered, u.enabled, u.calories_per_day, ur.role " +
                    "FROM users u LEFT JOIN user_role ur ON u.id = ur.user_id " +
                    "WHERE u.email=?";

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertUser;

    private final ResultSetExtractor<List<User>> usersExtractor = rs -> {
        Map<Integer, User> users = new LinkedHashMap<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            User user = users.get(id);

            if (user == null) {
                user = ROW_MAPPER.mapRow(rs, rs.getRow());
                user.setRoles(EnumSet.noneOf(Role.class));
                users.put(id, user);
            }

            String role = rs.getString("role");
            if (role != null) {
                user.getRoles().add(Role.valueOf(role));
            }
        }

        return new ArrayList<>(users.values());
    };

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate,
                              NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    @Transactional
    public User save(User user) {
        validate(user);

        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else {
            int updated = namedParameterJdbcTemplate.update("""
                    UPDATE users
                    SET name=:name,
                        email=:email,
                        password=:password,
                        registered=:registered,
                        enabled=:enabled,
                        calories_per_day=:caloriesPerDay
                    WHERE id=:id
                    """, parameterSource);
            if (updated == 0) {
                return null;
            }
            jdbcTemplate.update("DELETE FROM user_role WHERE user_id=?", user.id());
        }

        List<Object[]> batchArgs = user.getRoles().stream()
                .map(role -> new Object[]{user.id(), role.name()})
                .toList();

        jdbcTemplate.batchUpdate(
                "INSERT INTO user_role (user_id, role) VALUES (?, ?)",
                batchArgs
        );

        return user;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        return DataAccessUtils.singleResult(
                jdbcTemplate.query(SELECT_BY_ID, usersExtractor, id)
        );
    }

    @Override
    public User getByEmail(String email) {
        return DataAccessUtils.singleResult(
                jdbcTemplate.query(SELECT_BY_EMAIL, usersExtractor, email)
        );
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(SELECT_ALL, usersExtractor);
    }
}