package com.company.web.springdemo.repositories;

import com.company.web.springdemo.exceptions.EntityDuplicateException;
import com.company.web.springdemo.exceptions.EntityNotFoundException;
import com.company.web.springdemo.models.User;

import java.util.List;

public interface UserRepository {

    List<User> get();

    User get(int id);

    User get(String username);

    User create(User user) throws EntityDuplicateException;

    void update(User user);

    void delete(int id);
}