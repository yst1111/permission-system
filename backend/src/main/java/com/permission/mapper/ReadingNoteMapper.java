package com.permission.mapper;

import com.permission.entity.ReadingNote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 读书笔记Mapper接口
 */
@Mapper
public interface ReadingNoteMapper {

    /**
     * 插入读书笔记
     */
    int insert(ReadingNote readingNote);

    /**
     * 根据ID更新读书笔记
     */
    int updateById(ReadingNote readingNote);

    /**
     * 根据ID删除读书笔记
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询读书笔记
     */
    ReadingNote selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询读书笔记列表
     */
    List<ReadingNote> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和阅读状态查询读书笔记列表
     */
    List<ReadingNote> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("readingStatus") String readingStatus);

    /**
     * 根据用户ID和关键词搜索读书笔记
     */
    List<ReadingNote> searchByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    /**
     * 根据用户ID查询收藏的读书笔记
     */
    List<ReadingNote> selectFavoritesByUserId(@Param("userId") Long userId);

    /**
     * 统计用户阅读数据
     */
    List<ReadingNote> selectReadingStatistics(@Param("userId") Long userId, @Param("year") Integer year, @Param("month") Integer month);
} 