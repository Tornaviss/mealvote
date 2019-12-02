package com.mealvote;

import com.mealvote.model.user.Role;
import com.mealvote.model.user.User;
import com.mealvote.web.json.JsonUtil;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Comparator;

import static com.mealvote.model.AbstractBaseEntity.START_SEQ;

public class UserTestData {

    public static final String[] IGNORED_FIELDS = {"registered", "roles", "password"};
    public static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getName).thenComparing(User::getEmail);

    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;

    public static final User USER = new User(USER_ID, "User", "user@yandex.ru", "password", Role.ROLE_USER);
    public static final User ADMIN = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin", Role.ROLE_ADMIN, Role.ROLE_USER);

    public static User getCreated() {
        return new User(null, "New", "new@gmail.com", "password", Role.ROLE_USER);
    }

    public static User getUpdated() {
        return new User(USER_ID, "Updated", "updated@gmail.com", "updatedPass",
                USER.isEnabled(), USER.getRegistered(), USER.getRoles());
    }
    public static String jsonWithPassword(User user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }

    public static RequestPostProcessor userHttpBasic(User user) {
        return SecurityMockMvcRequestPostProcessors.httpBasic(user.getEmail(), user.getPassword());
    }

    public enum InvalidUserProperty {
        SHORT_PASSWORD("111"), SHORT_NAME("a"), MALFORMED_EMAIL("invalid@");

        private final String value;

        InvalidUserProperty(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
