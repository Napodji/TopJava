package ru.javawebinar.topjava.service;

import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.ActiveDbProfileResolver;
import ru.javawebinar.topjava.TimingRules;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static ru.javawebinar.topjava.Profiles.JDBC;

@ContextConfiguration("classpath:spring/spring-test-nocache.xml")
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
public abstract class AbstractServiceTest {

    @ClassRule
    public static ExternalResource summary = TimingRules.SUMMARY;

    @Rule
    public Stopwatch stopwatch = TimingRules.STOPWATCH;

    @Autowired
    private Environment environment;

    protected void assumeValidationSupported() {
        Assume.assumeFalse(isActiveProfile(JDBC));
    }

    private boolean isActiveProfile(String profile) {
        for (String activeProfile : environment.getActiveProfiles()) {
            if (profile.equals(activeProfile)) {
                return true;
            }
        }
        return false;
    }

    protected void validateRootCause(Class rootExceptionClass, Runnable runnable) {
        assertThatExceptionOfType(Throwable.class)
                .isThrownBy(runnable::run)
                .withRootCauseInstanceOf(rootExceptionClass);
    }
}