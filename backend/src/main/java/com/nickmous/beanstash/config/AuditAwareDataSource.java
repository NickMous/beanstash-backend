package com.nickmous.beanstash.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.datasource.DelegatingDataSource;

public class AuditAwareDataSource extends DelegatingDataSource {

    public AuditAwareDataSource(DataSource targetDataSource) {
        super(targetDataSource);
    }

    @Override
    public @NonNull Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        setAuditUser(connection);
        return connection;
    }

    @Override
    public @NonNull Connection getConnection(@NonNull String username, @NonNull String password) throws SQLException {
        Connection connection = super.getConnection(username, password);
        setAuditUser(connection);
        return connection;
    }

    private void setAuditUser(Connection connection) throws SQLException {
        String userId = UserContextHolder.getUserId();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT set_config('app.current_user_id', ?, false)")) {
            stmt.setString(1, userId != null ? userId : "");
            stmt.execute();
        }
    }
}
