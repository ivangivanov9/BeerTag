package com.company.web.springdemo.services;

import com.company.web.springdemo.exceptions.EntityDuplicateException;
import com.company.web.springdemo.exceptions.EntityNotFoundException;
import com.company.web.springdemo.models.User;

import java.util.List;

public interface UserService {

    List<User> get();

    User get(int id);

    User get(String username);

    void delete(int id);

    User create(String username, String password, String firstName, String lastName, String email);

    void addBeerToWishList(int userId, int beerId);

    void removeFromWishList(int userId, int beerId);
}