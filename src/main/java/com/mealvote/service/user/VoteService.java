package com.mealvote.service.user;

import com.mealvote.model.user.Vote;
import com.mealvote.repository.restaurant.CrudRestaurantRepository;
import com.mealvote.repository.user.CrudVoteRepository;
import com.mealvote.repository.user.CrudUserRepository;
import com.mealvote.util.exception.TemporaryUnavailableOperationException;
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
public class VoteService {

    private final CrudVoteRepository repository;

    private final CrudRestaurantRepository restaurantRepository;

    private final CrudUserRepository userRepository;

    @Autowired
    public VoteService(CrudVoteRepository repository, CrudRestaurantRepository restaurantRepository,
                       CrudUserRepository userRepository) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public List<Vote> getAll() {
        return repository.getAll();
    }

    public Vote get(int userId) {
        return checkNotFoundWithId(repository.findById(userId).orElse(null), userId, "vote");
    }

    @Transactional
    public Vote create(int restaurantId, int userId) {
        Vote vote = new Vote(
                userRepository.getOne(userId),
                checkNotFoundWithId(restaurantRepository.findById(restaurantId).orElse(null), restaurantId, "restaurant")
        );
        return repository.save(vote);
    }

    @Transactional
    public void update(int restaurantId, int userId) {
        update(restaurantId, userId, DEADLINE);
    }

    @Transactional
    public void update(int restaurantId, int userId, LocalTime deadline) {
        Vote vote = checkNotFoundWithId(repository.findById(userId).orElse(null), userId, "vote");
        if (isTimeToChangeMind(vote.getDateTime(), deadline)) {
            vote.setRestaurant(restaurantRepository.getOne(restaurantId));
            vote.setDateTime(LocalDateTime.now());
        } else {
            throw new TemporaryUnavailableOperationException("voting is over for today, try again tomorrow");
        }
    }

}
