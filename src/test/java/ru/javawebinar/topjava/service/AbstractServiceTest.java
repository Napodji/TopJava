package ru.javawebinar.topjava.service;

import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;
import org.junit.runner.RunWith;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.ActiveDbProfileResolver;
import ru.javawebinar.topjava.TimingRules;

@ContextConfiguration("classpath:spring/spring-test-nocache.xml")
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
public abstract class AbstractServiceTest {

    @ClassRule
    public static ExternalResource summary = TimingRules.SUMMARY;

    @Rule
    public Stopwatch stopwatch = TimingRules.STOPWATCH;

    protected void assumeValidationSupported() {
    }

    protected void validateRootCause(Class<? extends Throwable> exceptionClass, Runnable runnable) {
        Assertions.assertThatThrownBy(runnable::run)
                .satisfies(ex -> {
                    if (containsException(ex, exceptionClass)) {
                        return;
                    }
                    Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(ex);
                    throw new AssertionError("Expected exception of type " + exceptionClass.getName()
                            + " somewhere in cause chain, but got: " + ex
                            + ", most specific cause: " + rootCause);
                });
    }

    private boolean containsException(Throwable throwable, Class<? extends Throwable> exceptionClass) {
        Throwable current = throwable;
        while (current != null) {
            if (exceptionClass.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}