package com.mealvote.service.restaurant;

import com.mealvote.model.restaurant.Menu;
import com.mealvote.repository.restaurant.CrudMenuRepository;
import com.mealvote.repository.restaurant.CrudRestaurantRepository;
import com.mealvote.util.exception.IllegalOperationException;
import com.mealvote.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

import static com.mealvote.util.ValidationUtil.*;

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
        return checkNotFoundWithId(repository.findById(restaurantId).orElse(null), restaurantId);
    }

    public List<Menu> getAll() {
        return repository.getAll();
    }

    @Transactional
    public Menu create(Menu menu, int restaurantId) {
        Assert.notNull(menu, "menu must not be null");
        checkNew(menu);
        if (restaurantRepository.existsById(restaurantId)) {
            if (!repository.existsById(restaurantId)) {
                menu.setRestaurant(restaurantRepository.getOne(restaurantId));
                menu.getDishes().forEach(dish -> dish.setMenu(menu));
                return repository.save(menu);
            } else {
                throw new IllegalOperationException("restaurant " + restaurantId + " already has a menu. \n" +
                        "Only one menu per restaurant allowed");
            }
        } else {
            throw new NotFoundException("id = " + restaurantId);
        }
    }

    @Transactional
    public void update(Menu menu, int restaurantId) {
        Assert.notNull(menu, "menu must not be null");
        assureIdConsistent(menu, restaurantId);
        if (restaurantRepository.existsById(restaurantId) && repository.existsById(restaurantId)) {
            menu.setRestaurant(restaurantRepository.getOne(restaurantId));
            menu.getDishes().forEach(dish -> dish.setMenu(menu));
            repository.save(menu);
        } else {
            throw new NotFoundException("id = " + restaurantId);
        }
    }

    @Transactional
    public void delete(int id) {
        checkNotFoundWithId(repository.delete(id) != 0, id);
    }

    //TODO implement with batch update/create

//    public List<Dish> refreshDishes(List<Dish> dishes, final Integer menuId) {
//        if (!CollectionUtils.isEmpty(dishes)) {
//            List<Integer> idsToKeep = dishes.stream().filter(dish -> dish.getId() != null)
//                    .map(Dish::getId)
//                    .collect(Collectors.toList());
//            if (idsToKeep.isEmpty()) {
//                dishRepository.deleteAllForMenu(menuId);
//            } else {
//                dishRepository.deleteAllForMenuExcept(menuId, idsToKeep);
//            }
//            dishes.forEach(dish -> dish = dishRepository.save(dish, menuId));
//        }
//        return dishes;
//    }

}
