package net.JL.admin.controller;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.JL.admin.file.Encode;
import net.JL.admin.file.ExportExcel;
import net.JL.admin.file.ImpStudent;
import net.JL.admin.interceptor.AdminInterceptor;
import org.apache.commons.codec.digest.DigestUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.render.JsonRender;
import com.jfinal.upload.UploadFile;



@Before(AdminInterceptor.class)
public class StudentController extends Controller {
	public void index() {
		setAttr("url", "/admin/student");
		Record user = getSessionAttr("user");
		setAttr("user", user);

		render("student.html");
	}

	/** 获取数据 */
	public void getData() {
		long count = 0;
		List<Record> students = null;
		// 获取当前页数、每页条数
		int page = getParaToInt("page");
		int limit = getParaToInt("limit");
		String queryname = getPara("querystudent");
		if (queryname == null || queryname == "") {
			// 总条数
			count = Db.use("palmtop_campus").queryLong("SELECT count(1) FROM tb_students");
			// 记录集合
			students = Db.find("SELECT * FROM tb_students  LIMIT ?, ?", (page - 1) * limit, limit);
		} else {
			queryname = "%" + queryname.trim() + "%";
			// 总条数
			count = Db.use("palmtop_campus")
					.queryLong("SELECT count(1) FROM tb_students where " + " name like '" + queryname + "'");
			// 记录集合
			students = Db.find("SELECT * FROM tb_students where " + " name like '" + queryname + "' LIMIT ?, ?",
					(page - 1) * limit, limit);
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("code", 0);
		map.put("msg", "");
		map.put("count", count);
		map.put("data", students);
		renderJson(map);
	}

	// 增加
	public void addStudents() {
		render("addStudent.html");
	}

	// 增加提交
	public void saveStudents() {
		String id = getPara("_id");
		String name = getPara("name");
		int sex = getParaToInt("sex");
		Date birthday = getParaToDate("birthday");
		String faculty = getPara("faculty");
		String position = getPara("position");
		String password = DigestUtils.sha256Hex(String.valueOf(id));
		// 校验是否存在此学生
		Record teacherByidcard = Db.findFirst("select * from tb_students where id = ?", id);
		if (teacherByidcard != null) {
			fail("新增失败，此信息已存在！");
			return;
		}
		int result = Db.update(
				"INSERT INTO tb_students(id,name,sex,birthday,faculty,"
						+ "position,password) VALUES(?,?,?,?,?,?,?)",
				id, name, sex, birthday, faculty, position, password);
		if (result != 1) {
			fail("新增失败，请重新提交！");
			return;
		}
		ok("新增成功！");
		return;
	}

	// 编辑
	public void editPage() {
		String id = getPara("id");
		Record tRecord = Db.findById("tb_students", id);
		setAttr("students", tRecord);
		render("editStudents.html");
	}

	// 编辑 提交
	public void editStudents() {
		String id = getPara("_id");
		String name = getPara("name");
		boolean sex = getParaToBoolean("sex");
		Date birthday = getParaToDate("birthday");
		String national = getPara("national");
		String phone = getPara("phone");
		String faculty = getPara("faculty");
		String position = getPara("position");
		String dormitory = getPara("dormitory");
		Record editinfo = Db.findById("tb_students", id).set("name", name).set("sex", sex).set("birthday", birthday)
				.set("national", national).set("phone", phone).set("faculty", faculty).set("position", position)
				.set("dormitory", dormitory);
		boolean result = Db.update("tb_students", editinfo);
		if (!result) {
			fail("修改失败，请重新提交！");
			return;
		}
		ok("修改成功！");
		return;
	}

	// 删除
	public void deleteStudents() {
		String[] ids = getPara("ids").split(",");
		for (String id : ids) {
			// 删除账号信息
			Db.delete("delete from tb_students where id = ?", id);
		}
		ok("删除成功！");
		return;
	}

	// 下载模板
	public void downloadStudents() throws Exception {
		// 查询
		List<Record> downloadList = Db.find(
				"select '' id,'' name, '' sex,'' faculty from tb_students");
		String sheetName = "学生信息表";
		String[] headerList = { "学号", "姓名", "性别", "所在院系" };
		String[] attributeList = { "id", "name", "sex","faculty" };
		Encode encode = new Encode();
		String agent = getRequest().getHeader("USER-AGENT");
		String filename = "学生信息表.xlsx";
		filename = encode.enc(agent, filename);
		getResponse().setHeader("Content-Disposition", "attachment;filename=" + filename);
		getResponse().setContentType("application/vnd.ms-excel");
		OutputStream os = getResponse().getOutputStream();
		ExportExcel exp = new ExportExcel();
		exp.exportExcel(sheetName, headerList, downloadList, attributeList, os);
		renderNull();
	}

	// 导入
	public void impstudent() {
		UploadFile uploadFile = getFile();
		File file = uploadFile.getFile();
		HashMap<String, Object> map = new HashMap<String, Object>();
		ImpStudent impStudent = new ImpStudent();
		map = impStudent.save(file);
		try {
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ("fail".equals(map.get("status"))) {
			setSessionAttr("errs", map.get("msg"));
			map.put("msg", "导入失败！");
		}
		render(new JsonRender(map).forIE());
	}

	/** 错误信息弹框 */
	public void err() {
		render("err.html");
	}

	/** 获取错误信息数据 */
	public void getDataErr() {
		int page = getParaToInt("page");
		int limit = getParaToInt("limit");
		List<Object> errs = getSessionAttr("errs");
		int count = errs.size();
		int start = (page - 1) * limit;
		int end = page * limit < count ? page * limit : count;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("code", 0);
		map.put("msg", "");
		map.put("count", count);
		map.put("data", errs.subList(start, end));
		renderJson(map);
	}

	/** 成功提示 */
	private void ok(Object data) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		res.put("status", "0");
		res.put("msg", data);
		renderJson(res);
	}

	/** 失败提示 */
	private void fail(Object data) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		res.put("status", "-1");
		res.put("msg", data);
		renderJson(res);
	}
}
