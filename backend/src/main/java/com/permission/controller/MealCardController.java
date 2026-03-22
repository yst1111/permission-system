package com.permission.controller;

import com.permission.entity.MealCard;
import com.permission.mapper.MealCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal-card")
public class MealCardController {

    @Autowired
    private MealCardMapper mealCardMapper;

    @GetMapping("/list")
    public List<MealCard> list() {
        return mealCardMapper.selectAll();
    }

    @GetMapping("/{id}")
    public MealCard getById(@PathVariable Integer id) {
        return mealCardMapper.selectById(id);
    }

    @PostMapping("/add")
    public String add(@RequestBody MealCard mealCard) {
        mealCardMapper.insert(mealCard);
        return "success";
    }

    @PutMapping("/update")
    public String update(@RequestBody MealCard mealCard) {
        mealCardMapper.update(mealCard);
        return "success";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        mealCardMapper.deleteById(id);
        return "success";
    }

    @PutMapping("/use/{id}")
    public String use(@PathVariable Integer id) {
        mealCardMapper.incrementUsed(id);
        return "success";
    }

    @PutMapping("/restore/{id}")
    public String restore(@PathVariable Integer id) {
        mealCardMapper.decrementUsed(id);
        return "success";
    }
}
