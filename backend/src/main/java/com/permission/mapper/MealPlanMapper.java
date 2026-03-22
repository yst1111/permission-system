package com.permission.mapper;

import com.permission.entity.MealPlan;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MealPlanMapper {

    @Select("SELECT * FROM meal_plan ORDER BY card_id, id")
    List<MealPlan> selectAll();

    @Select("SELECT * FROM meal_plan WHERE card_id = #{cardId} ORDER BY id")
    List<MealPlan> selectByCardId(Integer cardId);

    @Select("SELECT * FROM meal_plan WHERE id = #{id}")
    MealPlan selectById(Integer id);

    @Insert("INSERT INTO meal_plan (card_id, dish_name, weight_g, calories, unit_price, created_at) " +
            "VALUES (#{cardId}, #{dishName}, #{weightG}, #{calories}, #{unitPrice}, NOW())")
    void insert(MealPlan mealPlan);

    @Update("UPDATE meal_plan SET card_id = #{cardId}, dish_name = #{dishName}, " +
            "weight_g = #{weightG}, calories = #{calories}, unit_price = #{unitPrice} WHERE id = #{id}")
    void update(MealPlan mealPlan);

    @Delete("DELETE FROM meal_plan WHERE id = #{id}")
    void deleteById(Integer id);
}
