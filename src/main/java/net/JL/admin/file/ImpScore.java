package net.JL.admin.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class ImpScore {

	/** 保存 */
	public HashMap<String, Object> save(File file) {
		InputStream is = null;
		Workbook workbook = null;
		// 判断后缀名
		if (file.getName().endsWith(".xls")) {
			try {
				is = new FileInputStream(file);
				workbook = new HSSFWorkbook(is);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (file.getName().endsWith(".xlsx")) {
			try {
				is = new FileInputStream(file);
				workbook = new XSSFWorkbook(is);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 学生成绩集合
		ArrayList<Record> scores = new ArrayList<Record>();
		// 错误信息集合
		ArrayList<Object> errs = new ArrayList<Object>();
		// 返回信息
		HashMap<String, Object> res = new HashMap<String, Object>();
		// 遍历excel，读取每个sheet
		for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
			// 遍历内容
			String sheetName = workbook.getSheetName(sheetNum);
			Sheet sheet = workbook.getSheetAt(sheetNum);
			int lastRowNum = sheet.getLastRowNum();
			for (int rowNum = 1; rowNum <= lastRowNum; rowNum++) {
				Row row = sheet.getRow(rowNum);
				if (row == null) {
					continue;
				}
				// 单行错误信息映射
				HashMap<String, Object> err = new HashMap<String, Object>();
				// 单行错误信息
				String rowInfo = "";
				// 单行是否有错误
				boolean hasError = false;

				Cell cellStuId = row.getCell(0); // 学号
				Cell cellStuName = row.getCell(1); // 姓名
				Cell cellCourseName = row.getCell(2); // 课程名
				Cell cellScore = row.getCell(3); // 成绩
				// 学号
				String stuId = "";
				if (isCellEmpty(cellStuId)) {
					rowInfo += "学号未填写";
					hasError = true;
				} else {
					cellStuId.setCellType(CellType.STRING);
					stuId = cellStuId.getStringCellValue();
				}
				// 姓名
				String stuName = "";
				if (isCellEmpty(cellStuName)) {
					rowInfo += "姓名未填写；";
					hasError = true;
				} else {
					cellStuName.setCellType(CellType.STRING);
					stuName = cellStuName.getStringCellValue().trim();
				}
				// 课程名
				String courseName = "";
				if (isCellEmpty(cellCourseName)) {
					rowInfo += "课程名未填写；";
					hasError = true;
				} else {
					cellCourseName.setCellType(CellType.STRING);
					courseName = cellCourseName.getStringCellValue().trim();
				}

				// 成绩
				int score = 0;
				if (isCellEmpty(cellScore)) {
					rowInfo += "出生日期未填写；";
					hasError = true;
				} else {
					cellScore.setCellType(CellType.NUMERIC);
					score = (int) cellScore.getNumericCellValue();
				}

				// 校验是否重复录入成绩
				Record studentsById = Db.findFirst("select * from tb_score where student_id = ?and course_name=?",
						stuId, courseName);
				if (studentsById != null) {
					rowInfo += "学号为" + stuId + "的学生" + courseName + "成绩已录入；";
					hasError = true;
				}

				if (hasError) {// 单行有错误
					err.put("sheetName", sheetName);
					err.put("rowNum", rowNum + 1);
					err.put("rowInfo", rowInfo);
					errs.add(err);
				} else {// 单行无错误
					scores.add(new Record().set("student_id", stuId).set("student_name", stuName)
							.set("course_name", courseName).set("score", score));
				}
			}
		}
		// 判断数据是否有错误
		if (errs.isEmpty()) {// 数据无错误
			String sql = "INSERT INTO tb_score(student_id,student_name, course_name, score) VALUES(?, ?, ?, ?)";
			Db.batch(sql, "student_id,student_name, course_name, score", scores, 5000);
			res.put("status", "ok");
			res.put("msg", "导入成功，增加" + scores.size() + "条记录！");
		} else {// 数据有错误
			res.put("status", "fail");
			res.put("msg", errs);
		}
		return res;
	}

	/** 判断单元格是否为空 */
	private boolean isCellEmpty(Cell cell) {
		if (cell == null || cell.toString().trim().isEmpty()) {
			System.out.println("Space or null in this cell!");
			return true;
		} else {
			return false;
		}
	}

}
