package com.mealvote.service.user;

import com.mealvote.model.user.Choice;
import com.mealvote.repository.restaurant.CrudRestaurantRepository;
import com.mealvote.repository.user.CrudChoiceRepository;
import com.mealvote.repository.user.CrudUserRepository;
import com.mealvote.util.exception.IllegalOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.mealvote.util.DateTimeUtil.DEADLINE;
import static com.mealvote.util.DateTimeUtil.isTimeToChangeMind;
import static com.mealvote.util.ValidationUtil.checkNotFoundWithId;

@Service
@Transactional(readOnly = true)
public class ChoiceService {

    private final CrudChoiceRepository repository;

    private final CrudRestaurantRepository restaurantRepository;

    private final CrudUserRepository userRepository;

    @Autowired
    public ChoiceService(CrudChoiceRepository repository, CrudRestaurantRepository restaurantRepository,
                         CrudUserRepository userRepository) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public List<Choice> getAll() {
        return repository.getAll();
    }

    public Choice get(int userId) {
        return checkNotFoundWithId(repository.findById(userId).orElse(null), userId, "choice");
    }

    @Transactional
    public Choice create(int restaurantId, int userId) {
        Choice choice = new Choice(
                userRepository.getOne(userId),
                checkNotFoundWithId(restaurantRepository.findById(restaurantId).orElse(null), restaurantId, "restaurant")
        );
        return repository.save(choice);
    }

    @Transactional
    public void update(int restaurantId, int userId) {
        update(restaurantId, userId, DEADLINE);
    }

    @Transactional
    public void update(int restaurantId, int userId, LocalTime deadline) {
        Choice choice = checkNotFoundWithId(repository.findById(userId).orElse(null), userId, "choice");
        if (isTimeToChangeMind(choice.getDateTime(), deadline)) {
            choice.setRestaurant(restaurantRepository.getOne(restaurantId));
            choice.setDateTime(LocalDateTime.now());
        } else {
            throw new IllegalOperationException("choice for user " + userId + " cannot be changed now");
        }
    }

}
