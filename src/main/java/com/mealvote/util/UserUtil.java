package com.mealvote.util;

import com.mealvote.model.user.Role;
import com.mealvote.model.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class UserUtil {

    public static User prepareToSave(User user, PasswordEncoder passwordEncoder) {
        String password = user.getPassword();
        user.setPassword(StringUtils.hasText(password) ? passwordEncoder.encode(password) : password);
        user.setEmail(user.getEmail().toLowerCase());
        return user;
    }

    public static void updateState(User existing, User prepared) {
        existing.setName(prepared.getName());
        existing.setEmail(prepared.getEmail());
        existing.setPassword(prepared.getPassword());
        existing.setEnabled(prepared.isEnabled());
        updateExistingRoles(existing.getRoles(), prepared.getRoles());
    }

    private static void updateExistingRoles(Set<Role> existing, Set<Role> updated) {
        Set<Role> toRemove = new HashSet<>();

        existing.stream()
                .filter(role -> !updated.contains(role))
                .forEach(toRemove::add);
        existing.removeAll(toRemove);

        updated.stream()
                .filter(role -> !existing.contains(role))
                .forEach(existing::add);
    }
}
