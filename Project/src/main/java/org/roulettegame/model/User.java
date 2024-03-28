package org.roulettegame.model;

import lombok.Data;

@Data
public class User {
    private int id;
    private String login;
    private String password;
    private int balance;

    public User() {
    }
}