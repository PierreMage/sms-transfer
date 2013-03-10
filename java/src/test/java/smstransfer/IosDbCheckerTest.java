package smstransfer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IosDbCheckerTest {

    private IosDbChecker iosDbChecker = new IosDbChecker();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowIllegalArgumentExceptionIfIsIos5Db() throws ClassNotFoundException {
        String dbFilePath = "src/test/resources/iOS5-SMS.db";
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("%s does not seem to be an iOS 6 SMS db", dbFilePath));

        iosDbChecker.check(dbFilePath);
    }

    @Test
    public void shouldReturnANewQueryExecutor() throws ClassNotFoundException {
        String dbFilePath = "src/test/resources/iOS6-SMS.db";
        QueryExecutor queryExecutor = iosDbChecker.check(dbFilePath);

        assertNotNull(queryExecutor);
        assertEquals(String.format("jdbc:sqlite:%s", dbFilePath), queryExecutor.getJdbcUrl());
    }
}
