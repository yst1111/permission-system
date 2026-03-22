package com.permission.mapper;

import com.permission.entity.DiaryEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 日记记录Mapper接口
 */
@Mapper
public interface DiaryEntryMapper {

    /**
     * 插入日记记录
     */
    int insert(DiaryEntry diaryEntry);

    /**
     * 根据ID更新日记记录
     */
    int updateById(DiaryEntry diaryEntry);

    /**
     * 根据ID删除日记记录
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询日记记录
     */
    DiaryEntry selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询日记记录列表
     */
    List<DiaryEntry> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和日期查询日记记录
     */
    DiaryEntry selectByUserIdAndDate(@Param("userId") Long userId, @Param("entryDate") LocalDate entryDate);

    /**
     * 根据用户ID和日期范围查询日记记录列表
     */
    List<DiaryEntry> selectByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 根据用户ID和心情状态查询日记记录列表
     */
    List<DiaryEntry> selectByUserIdAndMood(@Param("userId") Long userId, @Param("mood") String mood);

    /**
     * 根据用户ID和关键词搜索日记记录
     */
    List<DiaryEntry> searchByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    /**
     * 根据用户ID查询收藏的日记记录
     */
    List<DiaryEntry> selectFavoritesByUserId(@Param("userId") Long userId);

    /**
     * 统计用户日记数据
     */
    List<DiaryEntry> selectDiaryStatistics(@Param("userId") Long userId, @Param("year") Integer year, @Param("month") Integer month);
} 