package com.likelion.dao;

import com.likelion.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;

public class UserDao {
    private DataSource dataSource;
    private JdbcContext jdbcContext;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcContext = new JdbcContext(dataSource);
    }

    private ConnectionMaker cm;

    public UserDao() {
        this.cm = new AwsConnectionMaker();
    }

    public UserDao(ConnectionMaker cm) {
        this.cm = cm;
    }

    /* jdbccontext로 분리
    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws  SQLException{
        //StatementStrategy를 파라미터로 받음
        //jdbc try/catch/finally 구조로 만들어진 컨텍스트 내에서 작업을 수행.
        //독립된 jdbc 작업 흐름이 담겨있다고 볼 수 있다. dao 메서드(클라이언트)들이 공유할 수 있다.
    }
     */
    public void add(User user) throws SQLException, ClassNotFoundException {
        //AddStrategy addStatement = new AddStrategy(user);
        //jdbcContextWithStatementStrategy(addStatement);

        //StatementStrategy st = new AddStrategy(user);
        //jdbcContextWithStatementStrategy(st); jdbccontext클래스로 분리해서 쓸모없어짐.

        jdbcContext.workWithStatementStrategy(new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO users values (?,?,?)");

                pstmt.setString(1, user.getId());
                pstmt.setString(2, user.getName());
                pstmt.setString(3, user.getPassword());

                return pstmt;
            }
        });
    }

    public User findById(String id) {
        Map<String, String> env = System.getenv();
        Connection c;
        try {
            // DB접속 (ex sql workbeanch실행)
            c = cm.makeConnection();

            // Query문 작성
            PreparedStatement pstmt = c.prepareStatement("SELECT * FROM users WHERE id = ?");
            pstmt.setString(1, id);

            // Query문 실행
             ResultSet rs = pstmt.executeQuery();

            User user = null; //user 일단 Null로 초기화

            if (rs.next()) {
                user = new User(rs.getString("id"), rs.getString("name"),
                        rs.getString("password"));
            }

            rs.close();
            pstmt.close();
            c.close();

            if (user == null) throw new EmptyResultDataAccessException(1);

            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() throws SQLException, ClassNotFoundException {
        //1024월
        jdbcContext.workWithStatementStrategy(new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
                return connection.prepareStatement("delete from users");
            }
        });
    }

    public int getCount() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = cm.makeConnection();
            ps = c.prepareStatement("select count(*) from users");
            rs = ps.executeQuery();

            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (ps != null) {
                //null이 아니면 ps의 리소스 반환. 이것도 예외 처리
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }

            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        //UserDao userDao = new UserDao();
        //userDao.add();
        //userDao.add(new User("id1","name1","pw1"));
        //User user = userDao.findById("id1");
        //System.out.println(user.getName());
    }

    public void deleteAll22() throws SQLException {
        Connection c = cm.makeConnection();

        PreparedStatement ps = c.prepareStatement("delete from users");

        ps.executeUpdate();

        ps.close();
        c.close();
    }

}