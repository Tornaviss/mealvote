package com.mealvote.service.user;

import com.mealvote.AuthorizedUser;
import com.mealvote.model.user.User;
import com.mealvote.repository.user.CrudUserRepository;
import com.mealvote.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

import static com.mealvote.util.UserUtil.prepareToSave;
import static com.mealvote.util.UserUtil.updateState;
import static com.mealvote.util.ValidationUtil.checkNotFound;
import static com.mealvote.util.ValidationUtil.checkNotFoundWithId;

@Service("userService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final CrudUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(CrudUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(User user) {
        Assert.notNull(user, "user must not be null");
        return repository.save(prepareToSave(user, passwordEncoder));
    }

    @Transactional
    public void delete(int id) {
        checkNotFoundWithId(repository.delete(id) != 0, id, "user");
    }

    public User get(int id) {
        return checkNotFoundWithId(repository.findById(id).orElse(null), id, "user");
    }

    public User getByEmail(String email) {
        Assert.notNull(email, "email must not be null");
        return checkNotFound(repository.getByEmail(email), "email=" + email, "user");
    }

    public List<User> getAll() {
        return repository.getAll();
    }

    @Transactional
    public void update(User user) {
        Assert.notNull(user, "user must not be null");
        User persisted = get(user.getId());
        User prepared = prepareToSave(user, passwordEncoder);
        updateState(persisted, prepared);
    }

    @Transactional
    public void enable(int id, boolean enabled) {
        User user = get(id);
        user.setEnabled(enabled);
    }

    @Override
    public AuthorizedUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.getByEmail(email.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException("user " + email + " is not found");
        }
        return new AuthorizedUser(user);
    }
}
