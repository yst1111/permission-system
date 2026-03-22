package com.permission.service.impl;

import com.permission.entity.ReadingNote;
import com.permission.mapper.ReadingNoteMapper;
import com.permission.service.ReadingNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 读书笔记Service实现类
 */
@Service
public class ReadingNoteServiceImpl implements ReadingNoteService {

    @Autowired
    private ReadingNoteMapper readingNoteMapper;

    @Override
    @Transactional
    public ReadingNote createReadingNote(ReadingNote readingNote) {
        readingNote.setCreatedTime(LocalDateTime.now());
        readingNote.setUpdatedTime(LocalDateTime.now());
        if (readingNote.getCurrentPage() == null) {
            readingNote.setCurrentPage(0);
        }
        if (readingNote.getReadingTime() == null) {
            readingNote.setReadingTime(0);
        }
        if (readingNote.getIsFavorite() == null) {
            readingNote.setIsFavorite(false);
        }
        readingNoteMapper.insert(readingNote);
        return readingNote;
    }

    @Override
    @Transactional
    public ReadingNote updateReadingNote(ReadingNote readingNote) {
        readingNote.setUpdatedTime(LocalDateTime.now());
        readingNoteMapper.updateById(readingNote);
        return readingNote;
    }

    @Override
    @Transactional
    public boolean deleteReadingNote(Long id) {
        return readingNoteMapper.deleteById(id) > 0;
    }

    @Override
    public ReadingNote getReadingNoteById(Long id) {
        return readingNoteMapper.selectById(id);
    }

    @Override
    public List<ReadingNote> getUserReadingNotes(Long userId) {
        return readingNoteMapper.selectByUserId(userId);
    }

    @Override
    public List<ReadingNote> getReadingNotesByStatus(Long userId, String readingStatus) {
        return readingNoteMapper.selectByUserIdAndStatus(userId, readingStatus);
    }

    @Override
    public List<ReadingNote> searchReadingNotes(Long userId, String keyword) {
        return readingNoteMapper.searchByUserIdAndKeyword(userId, keyword);
    }

    @Override
    public List<ReadingNote> getFavoriteReadingNotes(Long userId) {
        return readingNoteMapper.selectFavoritesByUserId(userId);
    }

    @Override
    @Transactional
    public ReadingNote updateReadingProgress(Long id, Integer currentPage, Integer readingTime) {
        ReadingNote readingNote = readingNoteMapper.selectById(id);
        if (readingNote != null) {
            readingNote.setCurrentPage(currentPage);
            readingNote.setReadingTime(readingNote.getReadingTime() + readingTime);
            readingNote.setUpdatedTime(LocalDateTime.now());
            readingNoteMapper.updateById(readingNote);
        }
        return readingNote;
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long id) {
        ReadingNote readingNote = readingNoteMapper.selectById(id);
        if (readingNote != null) {
            readingNote.setIsFavorite(!readingNote.getIsFavorite());
            readingNote.setUpdatedTime(LocalDateTime.now());
            readingNoteMapper.updateById(readingNote);
            return true;
        }
        return false;
    }

    @Override
    public List<ReadingNote> getReadingStatistics(Long userId, Integer year, Integer month) {
        return readingNoteMapper.selectReadingStatistics(userId, year, month);
    }
} 