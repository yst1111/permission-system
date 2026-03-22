package com.permission.common;

import com.permission.entity.Region;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel工具类
 * 用于生成Excel模板和导入导出功能
 */
@Component
public class ExcelUtils {

    /**
     * 生成地区导入Excel模板
     * @return Excel文件的字节数组
     * @throws IOException IO异常
     */
    public byte[] generateRegionImportTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("地区导入模板");
            
            // 设置列宽
            sheet.setColumnWidth(0, 15 * 256); // 地区编码
            sheet.setColumnWidth(1, 20 * 256); // 地区名称
            sheet.setColumnWidth(2, 15 * 256); // 地区级别
            sheet.setColumnWidth(3, 15 * 256); // 父级编码
            sheet.setColumnWidth(4, 15 * 256); // 排序
            sheet.setColumnWidth(5, 10 * 256); // 状态
            
            // 创建标题行样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"地区编码*", "地区名称*", "地区级别*", "父级编码", "排序", "状态"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 创建说明行
            Row descRow = sheet.createRow(1);
            String[] descriptions = {
                "必填，唯一标识",
                "必填，地区显示名称", 
                "必填，1-省，2-市，3-区",
                "可选，上级地区编码",
                "可选，显示顺序",
                "可选，0-禁用，1-启用"
            };
            
            CellStyle descStyle = createDescriptionStyle(workbook);
            for (int i = 0; i < descriptions.length; i++) {
                Cell cell = descRow.createCell(i);
                cell.setCellValue(descriptions[i]);
                cell.setCellStyle(descStyle);
            }
            
            // 创建示例数据行
            createExampleRows(sheet, workbook);
            
            // 创建数据验证
            createDataValidation(sheet);
            
            // 输出到字节数组
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        }
    }
    
    /**
     * 创建标题行样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置背景色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        
        // 设置对齐方式
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * 创建说明行样式
     */
    private CellStyle createDescriptionStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置背景色
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setColor(IndexedColors.DARK_RED.getIndex());
        font.setItalic(true);
        style.setFont(font);
        
        // 设置对齐方式
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * 创建示例数据行
     */
    private void createExampleRows(Sheet sheet, Workbook workbook) {
        CellStyle exampleStyle = createExampleStyle(workbook);
        
        // 示例数据
        String[][] examples = {
            {"110000", "北京市", "1", "", "1", "1"},
            {"110100", "北京市", "2", "110000", "1", "1"},
            {"110101", "东城区", "3", "110100", "1", "1"},
            {"120000", "天津市", "1", "", "2", "1"},
            {"120100", "天津市", "2", "120000", "1", "1"},
            {"120101", "和平区", "3", "120100", "1", "1"}
        };
        
        for (int i = 0; i < examples.length; i++) {
            Row row = sheet.createRow(i + 2);
            for (int j = 0; j < examples[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(examples[i][j]);
                cell.setCellStyle(exampleStyle);
            }
        }
    }
    
    /**
     * 创建示例数据样式
     */
    private CellStyle createExampleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        
        // 设置对齐方式
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * 创建数据验证
     */
    private void createDataValidation(Sheet sheet) {
        // 地区级别验证（1-省，2-市，3-区）
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        
        // 创建地区级别下拉列表
        String[] levels = {"1", "2", "3"};
        DataValidationConstraint levelConstraint = validationHelper.createExplicitListConstraint(levels);
        
        // 应用到地区级别列（C列，索引为2）
        CellRangeAddressList levelRange = new CellRangeAddressList(2, 1000, 2, 2);
        DataValidation levelValidation = validationHelper.createValidation(levelConstraint, levelRange);
        levelValidation.setShowErrorBox(true);
        levelValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        sheet.addValidationData(levelValidation);
        
        // 状态验证（0-禁用，1-启用）
        String[] statuses = {"0", "1"};
        DataValidationConstraint statusConstraint = validationHelper.createExplicitListConstraint(statuses);
        
        // 应用到状态列（F列，索引为5）
        CellRangeAddressList statusRange = new CellRangeAddressList(2, 1000, 5, 5);
        DataValidation statusValidation = validationHelper.createValidation(statusConstraint, statusRange);
        statusValidation.setShowErrorBox(true);
        statusValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        sheet.addValidationData(statusValidation);
    }
    
    /**
     * 解析地区导入Excel文件
     * @param inputStream Excel文件输入流
     * @param fileName 文件名
     * @return 地区数据列表
     * @throws IOException IO异常
     */
    public List<Region> parseRegionImportFile(InputStream inputStream, String fileName) throws IOException {
        List<Region> regions = new ArrayList<>();
        
        try (Workbook workbook = createWorkbook(inputStream, fileName)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 从第3行开始读取数据（跳过标题行和说明行）
            for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                
                Region region = parseRegionRow(row);
                if (region != null) {
                    regions.add(region);
                }
            }
        }
        
        return regions;
    }
    
    /**
     * 创建Workbook对象
     */
    private Workbook createWorkbook(InputStream inputStream, String fileName) throws IOException {
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream);
        } else if (fileName.toLowerCase().endsWith(".xls")) {
            return new HSSFWorkbook(inputStream);
        } else {
            throw new IOException("不支持的文件格式，请使用.xlsx或.xls文件");
        }
    }
    
    /**
     * 解析单行地区数据
     */
    private Region parseRegionRow(Row row) {
        // 检查是否为空行
        if (isEmptyRow(row)) {
            return null;
        }
        
        Region region = new Region();
        
        try {
            // 地区编码（必填）
            String regionCode = getCellValueAsString(row.getCell(0));
            if (regionCode == null || regionCode.trim().isEmpty()) {
                return null; // 跳过空行
            }
            region.setRegionCode(regionCode.trim());
            
            // 地区名称（必填）
            String regionName = getCellValueAsString(row.getCell(1));
            if (regionName == null || regionName.trim().isEmpty()) {
                return null; // 跳过空行
            }
            region.setRegionName(regionName.trim());
            
            // 地区级别（必填）
            String levelStr = getCellValueAsString(row.getCell(2));
            if (levelStr != null && !levelStr.trim().isEmpty()) {
                try {
                    int level = Integer.parseInt(levelStr.trim());
                    if (level >= 1 && level <= 3) {
                        region.setLevel(level);
                    } else {
                        // 级别无效，使用默认值1
                        region.setLevel(1);
                    }
                } catch (NumberFormatException e) {
                    region.setLevel(1); // 默认值
                }
            } else {
                region.setLevel(1); // 默认值
            }
            
            // 父级编码（可选）
            String parentCode = getCellValueAsString(row.getCell(3));
            if (parentCode != null && !parentCode.trim().isEmpty()) {
                // 保存父级编码
                region.setParentCode(parentCode.trim());
            } else {
                region.setParentCode("");
            }
            
            // 排序（可选）
            String sortStr = getCellValueAsString(row.getCell(4));
            if (sortStr != null && !sortStr.trim().isEmpty()) {
                try {
                    int sort = Integer.parseInt(sortStr.trim());
                    region.setSortOrder(sort);
                } catch (NumberFormatException e) {
                    region.setSortOrder(0);
                }
            } else {
                region.setSortOrder(0);
            }
            
            // 状态（可选）
            String statusStr = getCellValueAsString(row.getCell(5));
            if (statusStr != null && !statusStr.trim().isEmpty()) {
                if ("0".equals(statusStr.trim()) || "1".equals(statusStr.trim())) {
                    region.setStatus(statusStr.trim());
                } else {
                    region.setStatus("1"); // 默认启用
                }
            } else {
                region.setStatus("1"); // 默认启用
            }
            
        } catch (Exception e) {
            // 解析失败，返回null
            return null;
        }
        
        return region;
    }
    
    /**
     * 检查是否为空行
     */
    private boolean isEmptyRow(Row row) {
        if (row == null) return true;
        
        for (int i = 0; i < 3; i++) { // 只检查前3个必填字段
            Cell cell = row.getCell(i);
            if (cell != null && getCellValueAsString(cell) != null && 
                !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 获取单元格值作为字符串
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            default:
                return null;
        }
    }
} 