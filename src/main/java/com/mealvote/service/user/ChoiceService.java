package com.mealvote.service.user;

import com.mealvote.model.user.Choice;
import com.mealvote.repository.restaurant.CrudRestaurantRepository;
import com.mealvote.repository.user.CrudChoiceRepository;
import com.mealvote.repository.user.CrudUserRepository;
import com.mealvote.util.exception.IllegalOperationException;
import com.mealvote.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        return checkNotFoundWithId(repository.findById(userId).orElse(null), userId);
    }

    @Transactional
    public Choice create(int restaurantId, int userId) {
        if (!repository.existsById(userId)) {
            Choice choice = new Choice(
                    userRepository.getOne(userId),
                    restaurantRepository.findById(restaurantId).orElseThrow(() -> new NotFoundException("id = " + restaurantId))
            );
            return repository.save(choice);

        } else {
            throw new IllegalOperationException("choice for user " + userId + " is already exist. " +
                    "Only one choice per user allowed");
        }
    }

    @Transactional
    public void update(int restaurantId, int userId) {
        Choice choice = checkNotFoundWithId(repository.findById(userId).orElse(null), userId);
        if (isTimeToChangeMind(choice.getDateTime())) {
            if (restaurantRepository.existsById(restaurantId)) {
                choice.setRestaurant(restaurantRepository.getOne(restaurantId));
                choice.setDateTime(LocalDateTime.now());
            } else {
                throw new NotFoundException("id = " + restaurantId);
            }
        } else {
            throw new IllegalOperationException("choice for user " + userId + " cannot be changed now");
        }
    }

}
