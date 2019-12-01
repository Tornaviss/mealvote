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

    public static void updateState(User existing, User updated) {
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPassword(updated.getPassword());
        existing.setEnabled(updated.isEnabled());
        Set<Role> existingRoles = existing.getRoles();
        Set<Role> updatedRoles = updated.getRoles();

        Set<Role> toRemove = new HashSet<>();
        existingRoles.stream()
                .filter(role -> !updatedRoles.contains(role))
                .forEach(toRemove::add);
        existingRoles.removeAll(toRemove);

        updatedRoles.stream()
                .filter(role -> !existingRoles.contains(role))
                .forEach(existingRoles::add);
    }
}
