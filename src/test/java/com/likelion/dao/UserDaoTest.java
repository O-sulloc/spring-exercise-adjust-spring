package com.likelion.dao;

import com.likelion.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UserDaoFactory.class)
class UserDaoTest {

    @Autowired
    ApplicationContext context;

    UserDao userDao;

    User user1;
    User user2;
    User user3;

    @BeforeEach
    void setUp(){
        this.userDao = context.getBean("awsUserDao", UserDao.class); //테스트에서 공통적으로 반복되는 코드임. 그래서 빼서 beforeEach로 만듬.
        this.user1 = new User("1", "김정현", "1234");
        this.user2 = new User("2", "kjh", "5678");
        this.user3 = new User("3", "kkk", "9012");
    }

    @Test
    void addAndGet() throws SQLException {
        User user1 = new User("1", "김정현", "1234");

        //UserDao userDao = context.getBean("awsUserDao", UserDao.class);
        userDao.deleteAll();
        assertEquals(0, userDao.getCount());

        userDao.add(user1);
        assertEquals(1, userDao.getCount());

        User user = userDao.findById(user1.getId());

        assertEquals(user1.getName(), user.getName());
        assertEquals(user1.getPassword(), user.getPassword());

        //assertEquals("name400", user.getName()); //오류 로그 볼 수 있음
        //assertEquals("pw100", user.getPassword());
    }

    @Test
    void count() throws SQLException {

        //UserDao userDao = context.getBean("awsUserDao", UserDao.class);

        userDao.deleteAll();
        assertEquals(0, userDao.getCount());

        userDao.add(user1);
        assertEquals(1, userDao.getCount());
        userDao.add(user2);
        assertEquals(2, userDao.getCount());
        userDao.add(user3);
        assertEquals(3, userDao.getCount());

    }

    @Test
    void findById(){
        assertThrows(EmptyResultDataAccessException.class, ()->{ //오류 처리

            //UserDao userDao = context.getBean("awsUserDao", UserDao.class); //beaforeEach로 만들어줘서 이제 필요없음.
            userDao.findById("23452345");
        });
    }
}