package com.permission.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 读书笔记实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReadingNote {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "用户ID", required = true)
    @NotBlank(message = "用户ID不能为空")
    private Long userId;

    @ApiModelProperty(value = "书名", required = true)
    @NotBlank(message = "书名不能为空")
    private String bookTitle;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "ISBN号")
    private String isbn;

    @ApiModelProperty(value = "阅读状态：未读、在读、已读、已弃")
    private String readingStatus;

    @ApiModelProperty(value = "开始阅读日期")
    private LocalDate startDate;

    @ApiModelProperty(value = "完成阅读日期")
    private LocalDate finishDate;

    @ApiModelProperty(value = "评分(1-5)")
    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    private Integer rating;

    @ApiModelProperty(value = "笔记内容")
    private String notesContent;

    @ApiModelProperty(value = "标签，逗号分隔")
    private String tags;

    @ApiModelProperty(value = "封面图片URL")
    private String coverImage;

    @ApiModelProperty(value = "总页数")
    @Min(value = 1, message = "页数不能小于1")
    private Integer pageCount;

    @ApiModelProperty(value = "当前阅读页数")
    @Min(value = 0, message = "当前页数不能小于0")
    private Integer currentPage;

    @ApiModelProperty(value = "阅读时长(分钟)")
    @Min(value = 0, message = "阅读时长不能小于0")
    private Integer readingTime;

    @ApiModelProperty(value = "是否收藏")
    private Boolean isFavorite;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedTime;

    // 手动添加getter和setter方法，确保兼容性
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getNotesContent() {
        return notesContent;
    }

    public void setNotesContent(String notesContent) {
        this.notesContent = notesContent;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(Integer readingTime) {
        this.readingTime = readingTime;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
} 