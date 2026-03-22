package com.permission.entity;

import lombok.Data;
import java.util.Date;

@Data
public class MealCard {
    private Integer id;
    private Integer totalTimes;
    private Integer usedTimes;
    private Integer remainingTimes;
    private Date createdAt;
    private Date updatedAt;
}
