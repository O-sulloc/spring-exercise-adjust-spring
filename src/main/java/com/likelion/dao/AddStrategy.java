package com.likelion.dao;

import com.likelion.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddStrategy implements StatementStrategy {
    User user;

    @Override
    public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("insert into users values (?,?,?)");
//        pstmt.setString(1, user.getId());
//        pstmt.setString(2, user.getName());
//        pstmt.setString(3, user.getPassword());
    }
}
