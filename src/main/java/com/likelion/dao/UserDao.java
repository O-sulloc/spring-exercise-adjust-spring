package com.likelion.dao;

import com.likelion.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;

public class UserDao {
    public DataSource dataSource;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private ConnectionMaker cm;

    public UserDao() {
        this.cm = new AwsConnectionMaker();
    }

    public UserDao(ConnectionMaker cm) {
        this.cm = cm;
    }

    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws  SQLException{
        //StatementStrategy를 파라미터로 받음
        //jdbc try/catch/finally 구조로 만들어진 컨텍스트 내에서 작업을 수행.
        //독립된 jdbc 작업 흐름이 담겨있다고 볼 수 있다. dao 메서드(클라이언트)들이 공유할 수 있다.

        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();
            ps = stmt.makePreparedStatement(c);
            //쿼리는 makePreparedStatement 메서드에 있음

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //에러가 나도 finally 블록에 있는 코드는 실행됨.

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
    public void add(User user) throws SQLException {
        //AddStrategy addStatement = new AddStrategy(user);
        //jdbcContextWithStatementStrategy(addStatement);

        StatementStrategy st = new AddStrategy(user);
        jdbcContextWithStatementStrategy(st);
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

    public void deleteAll() throws SQLException {
        //요청하는 클라이언트 메서드
        //여기서 전략 오브젝트를 만들고 컨텍스트를 호출
        jdbcContextWithStatementStrategy(new DeleteAllStrategy());
        //삭제하기 위해 deleteallstrategy 전략 클래스를 사용해야함. 해당 클래스의 오브젝트를 NEW로 생성
        //jdbc컨텍스트를 호출해서 생성한 오브젝트를 전달한다.
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
        UserDao userDao = new UserDao();
        userDao.add(new User("id1","name1","pw1"));
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