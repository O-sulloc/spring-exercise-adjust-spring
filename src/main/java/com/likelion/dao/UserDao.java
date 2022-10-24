package com.likelion.dao;

import com.likelion.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class UserDao {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.dataSource = dataSource;
    }

    private ConnectionMaker cm;

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* jdbccontext로 분리
    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws  SQLException{
        //StatementStrategy를 파라미터로 받음
        //jdbc try/catch/finally 구조로 만들어진 컨텍스트 내에서 작업을 수행.
        //독립된 jdbc 작업 흐름이 담겨있다고 볼 수 있다. dao 메서드(클라이언트)들이 공유할 수 있다.
    }
     */
    public void add(User user) throws SQLException, ClassNotFoundException {
        this.jdbcTemplate.update("INSERT INTO users values (?,?,?)",
                user.getId(), user.getName(), user.getPassword());
    }

    public User findById(String id) {
        String sql = "select * from users where id =?";

        return this.jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public void deleteAll() throws SQLException, ClassNotFoundException {
        this.jdbcTemplate.update("delete from users");
    }

    public int getCount() throws SQLException {
        return this.jdbcTemplate.queryForObject("select count(*) from users;", Integer.class);
    }

    public static void main(String[] args) throws SQLException {
        //UserDao userDao = new UserDao();
        //userDao.add();
        //userDao.add(new User("id1","name1","pw1"));
        //User user = userDao.findById("id1");
        //System.out.println(user.getName());
    }

    public List<User> getAll(){
        String sql = "select * from users order by id";

        return this.jdbcTemplate.query(sql, rowMapper);
    }

    RowMapper<User> rowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User(rs.getString("id"), rs.getString("name"), rs.getString("password"));

            return user;
        }
    };

}