package com.permission.entity;

import lombok.Data;
import java.util.Date;

@Data
public class MealPlan {
    private Integer id;
    private Integer cardId;
    private String dishName;
    private Integer weightG;
    private Integer calories;
    private Double unitPrice;
    private Date createdAt;
}
