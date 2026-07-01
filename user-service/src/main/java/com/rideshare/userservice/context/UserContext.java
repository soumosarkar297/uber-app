package com.rideshare.userservice.context;

import com.rideshare.userservice.entity.User;

public final class UserContext {

    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {}

    public static User getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void setCurrentUser(User user) {
        CURRENT_USER.set(user);
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
