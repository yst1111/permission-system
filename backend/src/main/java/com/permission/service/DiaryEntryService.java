package com.permission.service;

import com.permission.entity.DiaryEntry;

import java.time.LocalDate;
import java.util.List;

/**
 * 日记记录Service接口
 */
public interface DiaryEntryService {

    /**
     * 创建日记记录
     */
    DiaryEntry createDiaryEntry(DiaryEntry diaryEntry);

    /**
     * 更新日记记录
     */
    DiaryEntry updateDiaryEntry(DiaryEntry diaryEntry);

    /**
     * 删除日记记录
     */
    boolean deleteDiaryEntry(Long id);

    /**
     * 根据ID获取日记记录
     */
    DiaryEntry getDiaryEntryById(Long id);

    /**
     * 获取用户的日记记录列表
     */
    List<DiaryEntry> getUserDiaryEntries(Long userId);

    /**
     * 根据日期获取日记记录
     */
    DiaryEntry getDiaryEntryByDate(Long userId, LocalDate entryDate);

    /**
     * 根据日期范围获取日记记录列表
     */
    List<DiaryEntry> getDiaryEntriesByDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据心情状态获取日记记录列表
     */
    List<DiaryEntry> getDiaryEntriesByMood(Long userId, String mood);

    /**
     * 搜索日记记录
     */
    List<DiaryEntry> searchDiaryEntries(Long userId, String keyword);

    /**
     * 获取收藏的日记记录
     */
    List<DiaryEntry> getFavoriteDiaryEntries(Long userId);

    /**
     * 标记为收藏/取消收藏
     */
    boolean toggleFavorite(Long id);

    /**
     * 获取日记统计
     */
    List<DiaryEntry> getDiaryStatistics(Long userId, Integer year, Integer month);

    /**
     * 计算字数统计
     */
    int calculateWordCount(String content);
} 