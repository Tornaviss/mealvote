package com.mealvote.service.restaurant;

import com.mealvote.model.restaurant.Menu;
import com.mealvote.repository.restaurant.CrudMenuRepository;
import com.mealvote.repository.restaurant.CrudRestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

import static com.mealvote.util.MenuUtil.refreshDishes;
import static com.mealvote.util.ValidationUtil.checkNotFoundWithId;


@Service
@Transactional(readOnly = true)
public class MenuService {

    private final CrudMenuRepository repository;

    private final CrudRestaurantRepository restaurantRepository;

    @Autowired
    public MenuService(CrudMenuRepository repository, CrudRestaurantRepository restaurantRepository) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
    }

    public Menu get(int restaurantId) {
        return checkNotFoundWithId(repository.findById(restaurantId).orElse(null), restaurantId, "menu");
    }

    public List<Menu> getAll() {
        return repository.getAll();
    }

    @Transactional
    public Menu create(Menu menu, int restaurantId) {
        Assert.notNull(menu, "menu must not be null");
        menu.setRestaurant(restaurantRepository.getOne(restaurantId));
        menu.getDishes().forEach(dish -> dish.setMenu(menu));
        return repository.save(menu);
    }

    @Transactional
    public void update(Menu menu, int restaurantId) {
        Assert.notNull(menu, "menu must not be null");
        Menu persisted = get(restaurantId);
        refreshDishes(persisted, menu.getDishes());
    }

    @Transactional
    public void delete(int id) {
        checkNotFoundWithId(repository.delete(id) != 0, id, "menu");
    }



}
