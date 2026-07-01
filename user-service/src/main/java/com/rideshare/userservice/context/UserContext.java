package com.rideshare.userservice.context;

import com.rideshare.userservice.entity.User;

/**
 * Thread-local holder for the current authenticated user context.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public final class UserContext {

    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {}

    /** Retrieves the current user from the thread-local context. */
    public static User getCurrentUser() {
        return CURRENT_USER.get();
    }

    /** Sets the current user in the thread-local context. */
    public static void setCurrentUser(User user) {
        CURRENT_USER.set(user);
    }

    /** Clears the current user from the thread-local context. */
    public static void clear() {
        CURRENT_USER.remove();
    }
}
