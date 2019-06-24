package net.JL.admin.file;

import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfinal.plugin.activerecord.Record;

public class ExportExcel {
	public void exportExcel(String sheetName, String[] headerList, List<Record> contentList, String[] attributeList,
			OutputStream os) throws Exception {
		// 创建workbook
		XSSFWorkbook wb = new XSSFWorkbook();
		// 添加sheet
		XSSFSheet sheet = wb.createSheet(sheetName);
		// 设置单元格宽度
		for (int i = 0; i < headerList.length; i++) {
			sheet.setColumnWidth(i, 25 * 256);
		}
		// 居中对齐
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		// 字体
		Font font = wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);
		style.setFont(font);
		// 文本格式
		DataFormat format = wb.createDataFormat();
		style.setDataFormat(format.getFormat("@"));
		// 添加标题
		XSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headerList.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(headerList[i]);
		}
		// 添加内容
		for (int i = 0; i < contentList.size(); i++) {
			// 创建一行表格
			row = sheet.createRow(i + 1);
			Record contentRecord = contentList.get(i);
			// 往表格里插值
			for (int j = 0; j < attributeList.length; j++) {
				Object content = contentRecord.get(attributeList[j]);
				row.createCell(j).setCellValue(content.toString());
			}
		}
		try {
			// 导出
			wb.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
