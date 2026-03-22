package com.permission.controller;

import com.permission.entity.MealPlan;
import com.permission.mapper.MealPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal-plan")
public class MealPlanController {

    @Autowired
    private MealPlanMapper mealPlanMapper;

    @GetMapping("/list")
    public List<MealPlan> list() {
        return mealPlanMapper.selectAll();
    }

    @GetMapping("/list/{cardId}")
    public List<MealPlan> listByCardId(@PathVariable Integer cardId) {
        return mealPlanMapper.selectByCardId(cardId);
    }

    @GetMapping("/{id}")
    public MealPlan getById(@PathVariable Integer id) {
        return mealPlanMapper.selectById(id);
    }

    @PostMapping("/add")
    public String add(@RequestBody MealPlan mealPlan) {
        mealPlanMapper.insert(mealPlan);
        return "success";
    }

    @PutMapping("/update")
    public String update(@RequestBody MealPlan mealPlan) {
        mealPlanMapper.update(mealPlan);
        return "success";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        mealPlanMapper.deleteById(id);
        return "success";
    }
}
