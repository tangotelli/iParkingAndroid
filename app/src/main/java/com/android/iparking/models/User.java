package com.android.iparking.models;

import com.android.iparking.pojo.UserPojo;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String email;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static User fromPojo(UserPojo userPojo) {
        return new UserBuilder()
                .id(userPojo.getId())
                .email(userPojo.getEmail())
                .name(userPojo.getName())
                .build();
    }

    public static class UserBuilder implements UserBuilders.Id, UserBuilders.Email,
            UserBuilders.Name, UserBuilders.Optional {

        private final User user;

        public UserBuilder() {
            this.user = new User();
        }

        @Override
        public UserBuilders.Email id(String id) {
            this.user.setId(id);
            return this;
        }

        @Override
        public UserBuilders.Name email(String email) {
            this.user.setEmail(email);
            return this;
        }

        @Override
        public UserBuilders.Optional name(String name) {
            this.user.setName(name);
            return this;
        }

        @Override
        public User build() {
            return this.user;
        }
    }
}
