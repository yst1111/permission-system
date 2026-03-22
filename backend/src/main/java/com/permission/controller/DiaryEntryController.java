package com.permission.controller;

import com.permission.entity.DiaryEntry;
import com.permission.service.DiaryEntryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * 日记记录Controller
 */
@Api(tags = "日记记录管理")
@RestController
@RequestMapping("/api/diary-entries")

public class DiaryEntryController {

    @Autowired
    private DiaryEntryService diaryEntryService;

    @ApiOperation("创建日记记录")
    @PostMapping
    public ResponseEntity<DiaryEntry> createDiaryEntry(@Valid @RequestBody DiaryEntry diaryEntry) {
        DiaryEntry created = diaryEntryService.createDiaryEntry(diaryEntry);
        return ResponseEntity.ok(created);
    }

    @ApiOperation("更新日记记录")
    @PutMapping("/{id}")
    public ResponseEntity<DiaryEntry> updateDiaryEntry(
            @ApiParam("日记记录ID") @PathVariable Long id,
            @Valid @RequestBody DiaryEntry diaryEntry) {
        diaryEntry.setId(id);
        DiaryEntry updated = diaryEntryService.updateDiaryEntry(diaryEntry);
        return ResponseEntity.ok(updated);
    }

    @ApiOperation("删除日记记录")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteDiaryEntry(@ApiParam("日记记录ID") @PathVariable Long id) {
        boolean deleted = diaryEntryService.deleteDiaryEntry(id);
        return ResponseEntity.ok(deleted);
    }

    @ApiOperation("切换收藏状态")
    @PutMapping("/{id}/favorite")
    public ResponseEntity<DiaryEntry> toggleFavorite(@ApiParam("日记记录ID") @PathVariable Long id) {
        DiaryEntry diaryEntry = diaryEntryService.getDiaryEntryById(id);
        if (diaryEntry != null) {
            diaryEntry.setIsFavorite(!diaryEntry.getIsFavorite());
            diaryEntry.setUpdatedTime(java.time.LocalDateTime.now());
            DiaryEntry updated = diaryEntryService.updateDiaryEntry(diaryEntry);
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation("根据ID获取日记记录")
    @GetMapping("/{id}")
    public ResponseEntity<DiaryEntry> getDiaryEntryById(@ApiParam("日记记录ID") @PathVariable Long id) {
        DiaryEntry diaryEntry = diaryEntryService.getDiaryEntryById(id);
        return ResponseEntity.ok(diaryEntry);
    }

    @ApiOperation("获取用户的日记记录列表")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DiaryEntry>> getUserDiaryEntries(@ApiParam("用户ID") @PathVariable Long userId) {
        List<DiaryEntry> diaryEntries = diaryEntryService.getUserDiaryEntries(userId);
        return ResponseEntity.ok(diaryEntries);
    }

    @ApiOperation("根据日期获取日记记录")
    @GetMapping("/user/{userId}/date/{entryDate}")
    public ResponseEntity<DiaryEntry> getDiaryEntryByDate(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("日记日期") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate entryDate) {
        DiaryEntry diaryEntry = diaryEntryService.getDiaryEntryByDate(userId, entryDate);
        return ResponseEntity.ok(diaryEntry);
    }

    @ApiOperation("根据日期范围获取日记记录列表")
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<DiaryEntry>> getDiaryEntriesByDateRange(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DiaryEntry> diaryEntries = diaryEntryService.getDiaryEntriesByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(diaryEntries);
    }

    @ApiOperation("根据心情状态获取日记记录列表")
    @GetMapping("/user/{userId}/mood/{mood}")
    public ResponseEntity<List<DiaryEntry>> getDiaryEntriesByMood(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("心情状态") @PathVariable String mood) {
        List<DiaryEntry> diaryEntries = diaryEntryService.getDiaryEntriesByMood(userId, mood);
        return ResponseEntity.ok(diaryEntries);
    }

    @ApiOperation("搜索日记记录")
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<DiaryEntry>> searchDiaryEntries(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("搜索关键词") @RequestParam String keyword) {
        List<DiaryEntry> diaryEntries = diaryEntryService.searchDiaryEntries(userId, keyword);
        return ResponseEntity.ok(diaryEntries);
    }

    @ApiOperation("获取收藏的日记记录")
    @GetMapping("/user/{userId}/favorites")
    public ResponseEntity<List<DiaryEntry>> getFavoriteDiaryEntries(@ApiParam("用户ID") @PathVariable Long userId) {
        List<DiaryEntry> diaryEntries = diaryEntryService.getFavoriteDiaryEntries(userId);
        return ResponseEntity.ok(diaryEntries);
    }

    @ApiOperation("获取日记统计")
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<List<DiaryEntry>> getDiaryStatistics(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("年份") @RequestParam Integer year,
            @ApiParam("月份") @RequestParam Integer month) {
        List<DiaryEntry> statistics = diaryEntryService.getDiaryStatistics(userId, year, month);
        return ResponseEntity.ok(statistics);
    }

    @ApiOperation("导出用户日记")
    @GetMapping("/user/{userId}/export")
    public ResponseEntity<byte[]> exportDiaries(@ApiParam("用户ID") @PathVariable Long userId) {
        try {
            List<DiaryEntry> diaries = diaryEntryService.getUserDiaryEntries(userId);
            String jsonData = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(diaries);
            
            byte[] data = jsonData.getBytes("UTF-8");
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=diaries.json")
                    .header("Content-Type", "application/json")
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ApiOperation("导入日记")
    @PostMapping("/import")
    public ResponseEntity<List<DiaryEntry>> importDiaries(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            String content = new String(file.getBytes(), "UTF-8");
            List<DiaryEntry> diaries = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(content, new com.fasterxml.jackson.core.type.TypeReference<List<DiaryEntry>>() {});
            
            // 设置用户ID并保存
            for (DiaryEntry diary : diaries) {
                diary.setUserId(userId);
                diary.setId(null); // 清除ID，让数据库自动生成
                diary.setCreatedTime(java.time.LocalDateTime.now());
                diary.setUpdatedTime(java.time.LocalDateTime.now());
                
                // 确保必要字段有值
                if (diary.getEntryDate() == null) {
                    diary.setEntryDate(java.time.LocalDate.now());
                }
                if (diary.getIsPrivate() == null) {
                    diary.setIsPrivate(true);
                }
                if (diary.getIsFavorite() == null) {
                    diary.setIsFavorite(false);
                }
                if (diary.getWordCount() == null && diary.getContent() != null) {
                    diary.setWordCount(diary.getContent().length());
                }
                if (diary.getReadingTime() == null && diary.getWordCount() != null) {
                    diary.setReadingTime(Math.max(1, diary.getWordCount() / 200)); // 每分钟200字
                }
                
                diaryEntryService.createDiaryEntry(diary);
            }
            
            return ResponseEntity.ok(diaryEntryService.getUserDiaryEntries(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
} 