package com.likelion.dao;

import com.likelion.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UserDaoFactory.class)
class UserDaoTest {

    @Autowired
    ApplicationContext context;

    @Test
    void addAndGet() {
        UserDao userDao = context.getBean("awsUserDao", UserDao.class);

        String id = "id200";
        userDao.add(new User(id, "name200", "pw300"));

        User user = userDao.findById(id);
        //assertEquals("name400", user.getName()); //오류 로그 볼 수 있음
        //assertEquals("pw100", user.getPassword());
    }
}