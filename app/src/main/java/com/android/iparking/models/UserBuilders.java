package com.android.iparking.models;

public interface UserBuilders {

    interface Id {
        UserBuilders.Email id(String id);
    }

    interface Email {
        UserBuilders.Name email(String email);
    }

    interface Name {
        UserBuilders.Optional name(String name);
    }

    interface Optional {
        UserBuilders.Optional password(String password);

        User build();
    }
}
