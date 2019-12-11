package com.mealvote.service.restaurant;

import com.mealvote.model.restaurant.Restaurant;
import com.mealvote.repository.restaurant.CrudRestaurantRepository;
import com.mealvote.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

import static com.mealvote.util.ValidationUtil.checkNotFoundWithId;


@Service
@Transactional(readOnly = true)
public class RestaurantService {

    private CrudRestaurantRepository repository;

    @Autowired
    public RestaurantService(CrudRestaurantRepository repository) {
        this.repository = repository;
    }

    public List<Restaurant> getAll() {
        return repository.getAll();
    }

    public Restaurant get(int id) {
        return checkNotFoundWithId(repository.findById(id).orElse(null), id, "restaurant");
    }

    @Transactional
    public Restaurant create(Restaurant restaurant) {
        Assert.notNull(restaurant, "restaurant must not be null");
        return repository.save(restaurant);
    }

    @Transactional
    public void update(Restaurant restaurant, int id) {
        Assert.notNull(restaurant, "restaurant must not be null");
        Restaurant persisted = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("id = " + id));
        persisted.setName(restaurant.getName());
    }

    @Transactional
    public void delete(int id) {
        checkNotFoundWithId(repository.delete(id) != 0, id, "restaurant");
    }

    public Restaurant getWithChoices(int id) {
        return checkNotFoundWithId(repository.getWithChoices(id), id, "restaurant");
    }
}
