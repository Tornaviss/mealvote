package com.mealvote.service.restaurant;

import com.mealvote.model.audition.DishHistory;
import com.mealvote.model.restaurant.Dish;
import com.mealvote.repository.history.CrudDishHistoryRepository;
import com.mealvote.repository.restaurant.CrudDishRepository;
import com.mealvote.repository.restaurant.CrudMenuRepository;
import com.mealvote.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

import static com.mealvote.util.ValidationUtil.*;

@Service
@Transactional(readOnly = true)
public class DishService {

    private final CrudDishRepository repository;

    private final CrudMenuRepository menuRepository;

    private final CrudDishHistoryRepository historyRepository;

    @Autowired
    public DishService(CrudDishRepository repository, CrudMenuRepository menuRepository, CrudDishHistoryRepository historyRepository) {
        this.repository = repository;
        this.menuRepository = menuRepository;
        this.historyRepository = historyRepository;
    }

    public Dish get(int id) {
        return checkNotFoundWithId(repository.findById(id).orElse(null), id);
    }

    public List<Dish> getAll() {
        return repository.getAll();
    }

    public List<Dish> getAllForMenu(int menuId) {
        return repository.getAllForMenu(menuId);
    }

    @Transactional
    public void delete(int id) {
        checkNotFoundWithId(repository.delete(id) != 0, id);
    }

    @Transactional
    public Dish create(Dish dish, int menuId) {
        Assert.notNull(dish, "dish must not be null");
        dish.setMenu(menuRepository.getOne(menuId));
        return repository.save(dish);
    }

    @Transactional
    public void update(Dish dish) {
        Assert.notNull(dish, "dish must not be null");
        if (!repository.existsById(dish.getId())) {
            throw new NotFoundException("id = " + dish.getId());
        }
        repository.save(dish);
    }

    public List<DishHistory> getHistory(int dishId) {
        return historyRepository.getAllByDishId(dishId);
    }

    public List<DishHistory> getAllHistory() {
        return historyRepository.getAll();
    }

    @Transactional
    public void recover(int historyId) {
        DishHistory dishHistory = checkNotFoundWithId(historyRepository.findById(historyId).orElse(null), historyId);
        Dish dish = new Dish(dishHistory.getName(), dishHistory.getPrice());
        dish.setId(dishHistory.getId());
        if (!dishHistory.isActive()) {
            historyRepository.activate(dishHistory.getId());
            dish.setMenu(menuRepository.getOne(dishHistory.getMenuId()));
        }
        repository.save(dish);
    }

    public List<DishHistory> getMenuContentByDateTime(int menuId, LocalDateTime dateTime) {
        return historyRepository.getMenuContentByDateTime(menuId, dateTime);
    }
}
