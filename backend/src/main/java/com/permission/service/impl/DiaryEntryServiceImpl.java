package com.permission.service.impl;

import com.permission.entity.DiaryEntry;
import com.permission.mapper.DiaryEntryMapper;
import com.permission.service.DiaryEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DiaryEntryServiceImpl implements DiaryEntryService {

    @Autowired
    private DiaryEntryMapper diaryEntryMapper;

    @Override
    public DiaryEntry createDiaryEntry(DiaryEntry diaryEntry) {
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        diaryEntry.setCreatedTime(now);
        diaryEntry.setUpdatedTime(now);
        
        // 计算字数
        if (diaryEntry.getContent() != null) {
            diaryEntry.setWordCount(calculateWordCount(diaryEntry.getContent()));
        }
        
        // 设置默认值
        if (diaryEntry.getIsPrivate() == null) {
            diaryEntry.setIsPrivate(true);
        }
        if (diaryEntry.getIsFavorite() == null) {
            diaryEntry.setIsFavorite(false);
        }
        if (diaryEntry.getReadingTime() == null) {
            diaryEntry.setReadingTime(0);
        }
        
        diaryEntryMapper.insert(diaryEntry);
        return diaryEntry;
    }

    @Override
    public DiaryEntry getDiaryEntryById(Long id) {
        return diaryEntryMapper.selectById(id);
    }

    @Override
    public DiaryEntry updateDiaryEntry(DiaryEntry diaryEntry) {
        DiaryEntry existingEntry = diaryEntryMapper.selectById(diaryEntry.getId());
        if (existingEntry == null) {
            throw new RuntimeException("日记不存在");
        }
        
        // 更新字段
        if (diaryEntry.getTitle() != null) {
            existingEntry.setTitle(diaryEntry.getTitle());
        }
        if (diaryEntry.getContent() != null) {
            existingEntry.setContent(diaryEntry.getContent());
            existingEntry.setWordCount(calculateWordCount(diaryEntry.getContent()));
        }
        if (diaryEntry.getMood() != null) {
            existingEntry.setMood(diaryEntry.getMood());
        }
        if (diaryEntry.getWeather() != null) {
            existingEntry.setWeather(diaryEntry.getWeather());
        }
        if (diaryEntry.getLocation() != null) {
            existingEntry.setLocation(diaryEntry.getLocation());
        }
        if (diaryEntry.getTags() != null) {
            existingEntry.setTags(diaryEntry.getTags());
        }
        if (diaryEntry.getIsPrivate() != null) {
            existingEntry.setIsPrivate(diaryEntry.getIsPrivate());
        }
        if (diaryEntry.getIsFavorite() != null) {
            existingEntry.setIsFavorite(diaryEntry.getIsFavorite());
        }
        if (diaryEntry.getReadingTime() != null) {
            existingEntry.setReadingTime(diaryEntry.getReadingTime());
        }
        
        // 设置更新时间
        existingEntry.setUpdatedTime(LocalDateTime.now());
        
        diaryEntryMapper.updateById(existingEntry);
        return existingEntry;
    }

    @Override
    public boolean deleteDiaryEntry(Long id) {
        DiaryEntry existingEntry = diaryEntryMapper.selectById(id);
        if (existingEntry == null) {
            return false;
        }
        diaryEntryMapper.deleteById(id);
        return true;
    }

    @Override
    public List<DiaryEntry> getUserDiaryEntries(Long userId) {
        return diaryEntryMapper.selectByUserId(userId);
    }

    @Override
    public DiaryEntry getDiaryEntryByDate(Long userId, LocalDate entryDate) {
        return diaryEntryMapper.selectByUserIdAndDate(userId, entryDate);
    }

    @Override
    public List<DiaryEntry> getDiaryEntriesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return diaryEntryMapper.selectByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    public List<DiaryEntry> getDiaryEntriesByMood(Long userId, String mood) {
        return diaryEntryMapper.selectByUserIdAndMood(userId, mood);
    }

    @Override
    public List<DiaryEntry> searchDiaryEntries(Long userId, String keyword) {
        return diaryEntryMapper.searchByUserIdAndKeyword(userId, keyword);
    }

    @Override
    public List<DiaryEntry> getFavoriteDiaryEntries(Long userId) {
        return diaryEntryMapper.selectFavoritesByUserId(userId);
    }

    @Override
    public boolean toggleFavorite(Long id) {
        DiaryEntry existingEntry = diaryEntryMapper.selectById(id);
        if (existingEntry == null) {
            return false;
        }
        
        existingEntry.setIsFavorite(!existingEntry.getIsFavorite());
        existingEntry.setUpdatedTime(LocalDateTime.now());
        diaryEntryMapper.updateById(existingEntry);
        return true;
    }

    @Override
    public List<DiaryEntry> getDiaryStatistics(Long userId, Integer year, Integer month) {
        if (year != null && month != null) {
            // 按年月统计
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            return diaryEntryMapper.selectByUserIdAndDateRange(userId, startDate, endDate);
        } else {
            // 返回所有日记
            return diaryEntryMapper.selectByUserId(userId);
        }
    }

    @Override
    public int calculateWordCount(String content) {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }
        // 简单的字数统计，按字符计算
        return content.trim().length();
    }
} 