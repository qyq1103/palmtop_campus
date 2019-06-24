package net.JL.admin.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class ImpStudent {

    /**
     * 保存
     */
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

        // 学生集合
        ArrayList<Record> students = new ArrayList<Record>();
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

                Cell cellId = row.getCell(0); // 学号
                Cell cellName = row.getCell(1); // 姓名
                Cell cellSex = row.getCell(2); // 性别
                Cell cellFaculty = row.getCell(3); // 所在院系
                // 学号
                String id = "";
                if (isCellEmpty(cellId)) {
                    rowInfo += "学号未填写";
                    hasError = true;
                } else {
                    cellId.setCellType(CellType.STRING);
                    id = cellId.getStringCellValue();
                }
                // 姓名
                String name = "";
                if (isCellEmpty(cellName)) {
                    rowInfo += "姓名未填写；";
                    hasError = true;
                } else {
                    cellName.setCellType(CellType.STRING);
                    name = cellName.getStringCellValue().trim();
                }
                // 性别
                boolean sex = true;
                if (isCellEmpty(cellSex)) {
                    rowInfo += "性别未填写；";
                    hasError = true;
                } else {
                    cellSex.setCellType(CellType.STRING);
                    if (cellSex.getStringCellValue().trim() == "男") {
                        sex = true;
                    } else {
                        sex = false;
                    }
                }

                // 所在院系
                String faculty = "";
                if (isCellEmpty(cellFaculty)) {
                    rowInfo += "所在院系未填写；";
                    hasError = true;
                } else {
                    cellFaculty.setCellType(CellType.STRING);
                    faculty = cellFaculty.getStringCellValue();
                }

                // 校验是否存在此学生
                Record studentsById = Db.findFirst("select * from tb_students where id = ?", id);
                if (studentsById != null) {
                    rowInfo += "学号为" + id + "的学生已录入；";
                    hasError = true;
                }

                if (hasError) {
                    // 单行有错误
                    err.put("sheetName", sheetName);
                    err.put("rowNum", rowNum + 1);
                    err.put("rowInfo", rowInfo);
                    errs.add(err);
                } else {// 单行无错误
                    String password = DigestUtils.sha256Hex(String.valueOf(id));
                    students.add(new Record().set("id", id).set("name", name).set("sex", sex).set("faculty", faculty)
                            .set("password", password));
                }
            }
        }
        // 判断数据是否有错误
        if (errs.isEmpty()) {// 数据无错误
            String sql = "INSERT INTO tb_students(id,name, sex, birthday,national,phone,faculty,position,dormitory,password) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            Db.batch(sql, "id,name, sex, birthday,national,phone,faculty,position,dormitory,password", students, 5000);
            res.put("status", "ok");
            res.put("msg", "导入成功，增加" + students.size() + "条记录！");
        } else {// 数据有错误
            res.put("status", "fail");
            res.put("msg", errs);
        }
        return res;
    }

    /**
     * 判断单元格是否为空
     */
    private boolean isCellEmpty(Cell cell) {
        if (cell == null || cell.toString().trim().isEmpty()) {
            System.out.println("Space or null in this cell!");
            return true;
        } else {
            return false;
        }
    }
}
