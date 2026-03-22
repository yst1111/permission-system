package com.permission.service;

import com.permission.entity.ReadingNote;

import java.util.List;

/**
 * 读书笔记Service接口
 */
public interface ReadingNoteService {

    /**
     * 创建读书笔记
     */
    ReadingNote createReadingNote(ReadingNote readingNote);

    /**
     * 更新读书笔记
     */
    ReadingNote updateReadingNote(ReadingNote readingNote);

    /**
     * 删除读书笔记
     */
    boolean deleteReadingNote(Long id);

    /**
     * 根据ID获取读书笔记
     */
    ReadingNote getReadingNoteById(Long id);

    /**
     * 获取用户的读书笔记列表
     */
    List<ReadingNote> getUserReadingNotes(Long userId);

    /**
     * 根据阅读状态获取读书笔记列表
     */
    List<ReadingNote> getReadingNotesByStatus(Long userId, String readingStatus);

    /**
     * 搜索读书笔记
     */
    List<ReadingNote> searchReadingNotes(Long userId, String keyword);

    /**
     * 获取收藏的读书笔记
     */
    List<ReadingNote> getFavoriteReadingNotes(Long userId);

    /**
     * 更新阅读进度
     */
    ReadingNote updateReadingProgress(Long id, Integer currentPage, Integer readingTime);

    /**
     * 标记为收藏/取消收藏
     */
    boolean toggleFavorite(Long id);

    /**
     * 获取阅读统计
     */
    List<ReadingNote> getReadingStatistics(Long userId, Integer year, Integer month);
} 