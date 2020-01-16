package com.mealvote.service.restaurant;

import com.mealvote.model.restaurant.Menu;
import com.mealvote.model.restaurant.Restaurant;
import com.mealvote.repository.restaurant.CrudMenuRepository;
import com.mealvote.repository.restaurant.CrudRestaurantRepository;
import com.mealvote.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mealvote.util.ValidationUtil.checkNotFoundWithId;


@Service
@Transactional(readOnly = true)
public class RestaurantService {

    private CrudRestaurantRepository repository;
    private CrudMenuRepository menuRepository;

    @Autowired
    public RestaurantService(CrudRestaurantRepository repository, CrudMenuRepository menuRepository) {
        this.repository = repository;
        this.menuRepository = menuRepository;
    }

    public List<Restaurant> getAll() {
        return repository.getAll();
    }

    public List<Restaurant> getAll(boolean includeMenu, boolean includeVotes) {
        return includeMenu ?
                        includeVotes ? getWithMenuAndVotes() : repository.getAllWithMenu()
                        :
                        includeVotes ? repository.getAllWithVotes() : repository.getAll();
    }

    public Restaurant get(int id) {
        return checkNotFoundWithId(repository.findById(id).orElse(null), id, "restaurant");
    }

    public Restaurant get(int id, boolean includeMenu, boolean includeVotes) {
        return checkNotFoundWithId(
                includeMenu ?
                        includeVotes ? repository.getWithMenuAndVotes(id) : repository.getWithMenu(id)
                        :
                        includeVotes ? repository.getWithVotes(id) : repository.findById(id).orElse(null),
                id, "restaurant");
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

    private List<Restaurant> getWithMenuAndVotes() {
        List<Restaurant> restaurants = getAll(false, true);
        Map<Integer, Menu> idMenuMap = menuRepository.getAll().stream()
                .collect(Collectors.toMap(Menu::getId, Function.identity()));
        restaurants.forEach(x -> x.setMenu(idMenuMap.getOrDefault(x.getId(), null)));
        return restaurants;
    }
}
