package org.example;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author trx
 */
public final class CopySheetUtil {

    public CopySheetUtil() {
    }

    public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet) {
    copySheets(newSheet, sheet, true);
}

    public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet,
                                  boolean copyStyle) {
        int maxColumnNum = 0;
        Map<Integer, HSSFCellStyle> styleMap = (copyStyle) ? new HashMap<Integer, HSSFCellStyle>()
                : null;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            HSSFRow srcRow = sheet.getRow(i);
            HSSFRow destRow = newSheet.createRow(i);
            if (srcRow != null) {
                CopySheetUtil.copyRow(sheet, newSheet, srcRow, destRow,
                        styleMap);
                if (srcRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = srcRow.getLastCellNum();
                }
            }
        }
        for (int i = 0; i <= maxColumnNum; i++) {    //设置列宽
            newSheet.setColumnWidth(i, sheet.getColumnWidth(i)+250);
        }
    }

    /**
     * 复制合并单元格样式
     * @param srcSheet
     * @param destSheet
     * @param srcRow
     * @param destRow
     * @param styleMap
     */
    public static void copyRow(HSSFSheet srcSheet, HSSFSheet destSheet,
                               HSSFRow srcRow, HSSFRow destRow,
                               Map<Integer, HSSFCellStyle> styleMap) {
        Set<CellRangeAddressWrapper> mergedRegions = new TreeSet<CellRangeAddressWrapper>();
        destRow.setHeight(srcRow.getHeight());
        int deltaRows = destRow.getRowNum() - srcRow.getRowNum(); //如果copy到另一个sheet的起始行数不同
        for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
            HSSFCell oldCell = srcRow.getCell(j); // old cell
            HSSFCell newCell = destRow.getCell(j); // new cell
            if (oldCell != null) {
                if (newCell == null) {
                    newCell = destRow.createCell(j);
                }
                copyCell(oldCell, newCell, styleMap);
                CellRangeAddress mergedRegion = getMergedRegion(srcSheet,
                        srcRow.getRowNum(), (short) oldCell.getColumnIndex());
                if (mergedRegion != null) {
                    CellRangeAddress newMergedRegion = new CellRangeAddress(
                            mergedRegion.getFirstRow() + deltaRows,
                            mergedRegion.getLastRow() + deltaRows, mergedRegion
                            .getFirstColumn(), mergedRegion
                            .getLastColumn());
                    CellRangeAddressWrapper wrapper = new CellRangeAddressWrapper(
                            newMergedRegion);
                    if (isNewMergedRegion(wrapper, mergedRegions)) {
                        mergedRegions.add(wrapper);
                        destSheet.addMergedRegion(wrapper.range);
                    }
                }
            }
        }
    }

    /**
     * 把原来的Sheet中cell（列）的样式和数据类型复制到新的sheet的cell（列）中
     *
     * @param oldCell
     * @param newCell
     * @param styleMap
     */
    public static void copyCell(HSSFCell oldCell, HSSFCell newCell,
                                Map<Integer, HSSFCellStyle> styleMap) {
        if (styleMap != null) {
            if (oldCell.getSheet().getWorkbook() == newCell.getSheet()
                    .getWorkbook()) {
                newCell.setCellStyle(oldCell.getCellStyle());
            } else {
                int stHashCode = oldCell.getCellStyle().hashCode();
                HSSFCellStyle newCellStyle = styleMap.get(stHashCode);
                if (newCellStyle == null) {
                    newCellStyle = newCell.getSheet().getWorkbook()
                            .createCellStyle();
                    newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
                    styleMap.put(stHashCode, newCellStyle);
                }
                newCell.setCellStyle(newCellStyle);
            }
        }

        CellType cellTypeEnum = oldCell.getCellTypeEnum();
        if(cellTypeEnum == CellType.STRING){
            newCell.setCellValue(oldCell.getStringCellValue());
        }else if (cellTypeEnum == CellType.NUMERIC){
            newCell.setCellValue(oldCell.getNumericCellValue());
        }else if (cellTypeEnum == CellType.BLANK){
            newCell.setCellType(cellTypeEnum);
        }else if (cellTypeEnum == CellType.BOOLEAN){
            newCell.setCellValue(oldCell.getBooleanCellValue());
        }else if (cellTypeEnum == CellType.ERROR){
            newCell.setCellErrorValue(FormulaError.forInt(oldCell.getErrorCellValue()));
        }else if (cellTypeEnum == CellType.FORMULA){
            newCell.setCellFormula(oldCell.getCellFormula());
        }

    }

    // 获取merge对象
    public static CellRangeAddress getMergedRegion(HSSFSheet sheet, int rowNum,
                                                   short cellNum) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress merged = sheet.getMergedRegion(i);
            if (merged.isInRange(rowNum, cellNum)) {
                return merged;
            }
        }
        return null;
    }

    private static boolean isNewMergedRegion(
            CellRangeAddressWrapper newMergedRegion,
            Set<CellRangeAddressWrapper> mergedRegions) {
        boolean bool = mergedRegions.contains(newMergedRegion);
        return !bool;
    }

}
