package com.mealvote.service.user;

import com.mealvote.model.audition.ChoiceHistory;
import com.mealvote.model.user.Choice;
import com.mealvote.repository.history.CrudChoiceHistoryRepository;
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

    private final CrudChoiceHistoryRepository historyRepository;

    @Autowired
    public ChoiceService(CrudChoiceRepository repository, CrudRestaurantRepository restaurantRepository,
                         CrudUserRepository userRepository, CrudChoiceHistoryRepository historyRepository) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
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

    public List<ChoiceHistory> getHistory(int userId) {
        return historyRepository.getAllByUserId(userId);
    }

    public List<ChoiceHistory> getAllHistory() {
        return historyRepository.getAll();
    }

    @Transactional
    public void recover(int historyId) {
        ChoiceHistory choiceHistory =
                checkNotFoundWithId(historyRepository.findById(historyId).orElse(null), historyId);
        Choice choice = new Choice();
        choice.setDateTime(choiceHistory.getDateTime());
        choice.setRestaurant(restaurantRepository.findById(choiceHistory.getRestaurantId())
                .orElseThrow(() -> new NotFoundException("id = " + choiceHistory.getRestaurantId())));
        int userId = choiceHistory.getUserId();
        choice.setUser(userRepository.getOne(userId));

        if (!choiceHistory.isActive()) {
            if (repository.existsById(userId)) {
                repository.deleteById(userId);
            }
        }

        repository.save(choice);
    }

}
