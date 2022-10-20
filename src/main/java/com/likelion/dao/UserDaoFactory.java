package com.likelion.dao;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Configurable
public class UserDaoFactory {

    @Bean
    public UserDao aswsUserDao(){
        return new UserDao(new AwsConnectionMaker());
    }
}
