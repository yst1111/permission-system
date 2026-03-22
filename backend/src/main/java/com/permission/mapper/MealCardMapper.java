package com.permission.mapper;

import com.permission.entity.MealCard;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MealCardMapper {

    @Select("SELECT * FROM meal_card ORDER BY id")
    List<MealCard> selectAll();

    @Select("SELECT * FROM meal_card WHERE id = #{id}")
    MealCard selectById(Integer id);

    @Insert("INSERT INTO meal_card (total_times, used_times, remaining_times, created_at, updated_at) " +
            "VALUES (#{totalTimes}, #{usedTimes}, #{remainingTimes}, NOW(), NOW())")
    void insert(MealCard mealCard);

    @Update("UPDATE meal_card SET total_times = #{totalTimes}, used_times = #{usedTimes}, " +
            "remaining_times = #{remainingTimes}, updated_at = NOW() WHERE id = #{id}")
    void update(MealCard mealCard);

    @Delete("DELETE FROM meal_card WHERE id = #{id}")
    void deleteById(Integer id);

    @Update("UPDATE meal_card SET used_times = used_times + 1, remaining_times = remaining_times - 1, " +
            "updated_at = NOW() WHERE id = #{id}")
    void incrementUsed(Integer id);

    @Update("UPDATE meal_card SET used_times = used_times - 1, remaining_times = remaining_times + 1, " +
            "updated_at = NOW() WHERE id = #{id} AND used_times > 0")
    void decrementUsed(Integer id);
}
