package com.permission.controller;

import com.permission.entity.ReadingNote;
import com.permission.service.ReadingNoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 读书笔记Controller
 */
@Api(tags = "读书笔记管理")
@RestController
@RequestMapping("/api/reading-notes")

public class ReadingNoteController {

    @Autowired
    private ReadingNoteService readingNoteService;

    @ApiOperation("创建读书笔记")
    @PostMapping
    public ResponseEntity<ReadingNote> createReadingNote(@Valid @RequestBody ReadingNote readingNote) {
        ReadingNote created = readingNoteService.createReadingNote(readingNote);
        return ResponseEntity.ok(created);
    }

    @ApiOperation("更新读书笔记")
    @PutMapping("/{id}")
    public ResponseEntity<ReadingNote> updateReadingNote(
            @ApiParam("读书笔记ID") @PathVariable Long id,
            @Valid @RequestBody ReadingNote readingNote) {
        readingNote.setId(id);
        ReadingNote updated = readingNoteService.updateReadingNote(readingNote);
        return ResponseEntity.ok(updated);
    }

    @ApiOperation("删除读书笔记")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteReadingNote(@ApiParam("读书笔记ID") @PathVariable Long id) {
        boolean deleted = readingNoteService.deleteReadingNote(id);
        return ResponseEntity.ok(deleted);
    }

    @ApiOperation("根据ID获取读书笔记")
    @GetMapping("/{id}")
    public ResponseEntity<ReadingNote> getReadingNoteById(@ApiParam("读书笔记ID") @PathVariable Long id) {
        ReadingNote readingNote = readingNoteService.getReadingNoteById(id);
        return ResponseEntity.ok(readingNote);
    }

    @ApiOperation("获取用户的读书笔记列表")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReadingNote>> getUserReadingNotes(@ApiParam("用户ID") @PathVariable Long userId) {
        List<ReadingNote> readingNotes = readingNoteService.getUserReadingNotes(userId);
        return ResponseEntity.ok(readingNotes);
    }

    @ApiOperation("根据阅读状态获取读书笔记列表")
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<ReadingNote>> getReadingNotesByStatus(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("阅读状态") @PathVariable String status) {
        List<ReadingNote> readingNotes = readingNoteService.getReadingNotesByStatus(userId, status);
        return ResponseEntity.ok(readingNotes);
    }

    @ApiOperation("搜索读书笔记")
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<ReadingNote>> searchReadingNotes(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("搜索关键词") @RequestParam String keyword) {
        List<ReadingNote> readingNotes = readingNoteService.searchReadingNotes(userId, keyword);
        return ResponseEntity.ok(readingNotes);
    }

    @ApiOperation("获取收藏的读书笔记")
    @GetMapping("/user/{userId}/favorites")
    public ResponseEntity<List<ReadingNote>> getFavoriteReadingNotes(@ApiParam("用户ID") @PathVariable Long userId) {
        List<ReadingNote> readingNotes = readingNoteService.getFavoriteReadingNotes(userId);
        return ResponseEntity.ok(readingNotes);
    }

    @ApiOperation("更新阅读进度")
    @PutMapping("/{id}/progress")
    public ResponseEntity<ReadingNote> updateReadingProgress(
            @ApiParam("读书笔记ID") @PathVariable Long id,
            @ApiParam("当前页数") @RequestParam Integer currentPage,
            @ApiParam("阅读时长(分钟)") @RequestParam Integer readingTime) {
        ReadingNote updated = readingNoteService.updateReadingProgress(id, currentPage, readingTime);
        return ResponseEntity.ok(updated);
    }

    @ApiOperation("标记为收藏/取消收藏")
    @PutMapping("/{id}/favorite")
    public ResponseEntity<Boolean> toggleFavorite(@ApiParam("读书笔记ID") @PathVariable Long id) {
        boolean toggled = readingNoteService.toggleFavorite(id);
        return ResponseEntity.ok(toggled);
    }

    @ApiOperation("获取阅读统计")
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<List<ReadingNote>> getReadingStatistics(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("年份") @RequestParam Integer year,
            @ApiParam("月份") @RequestParam Integer month) {
        List<ReadingNote> statistics = readingNoteService.getReadingStatistics(userId, year, month);
        return ResponseEntity.ok(statistics);
    }
} 