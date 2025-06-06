package cn.yusiwen.commons.mapper.testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.extern.slf4j.Slf4j;

import org.testcontainers.delegate.AbstractDatabaseDelegate;
import org.testcontainers.exception.ConnectionCreationException;
import org.testcontainers.ext.ScriptUtils;

@Slf4j
public class CustomJdbcDatabaseDelegate extends AbstractDatabaseDelegate<Statement> {

    private final CustomJdbcDatabaseContainer container;

    private Connection connection;

    private String queryString;

    public CustomJdbcDatabaseDelegate(CustomJdbcDatabaseContainer container, String queryString) {
        this.container = container;
        this.queryString = queryString;
    }

    @Override
    protected Statement createNewConnection() {
        try {
            connection = container.createConnection(queryString);
            return connection.createStatement();
        } catch (SQLException e) {
            log.error("Could not obtain JDBC connection");
            throw new ConnectionCreationException("Could not obtain JDBC connection", e);
        }
    }

    @Override
    public void execute(String statement, String scriptPath, int lineNumber, boolean continueOnError,
        boolean ignoreFailedDrops) {
        try {
            boolean rowsAffected = getConnection().execute(statement);
            log.debug("{} returned as updateCount for SQL: {}", rowsAffected, statement);
        } catch (SQLException ex) {
            boolean dropStatement = statement.trim().toLowerCase().startsWith("drop");
            if (continueOnError || (dropStatement && ignoreFailedDrops)) {
                log.debug("Failed to execute SQL script statement at line {} of resource {}: {}", lineNumber,
                    scriptPath, statement, ex);
            } else {
                throw new ScriptUtils.ScriptStatementFailedException(statement, lineNumber, scriptPath, ex);
            }
        }
    }

    @Override
    protected void closeConnectionQuietly(Statement statement) {
        try {
            statement.close();
            connection.close();
        } catch (Exception e) {
            log.error("Could not close JDBC connection", e);
        }
    }
}
