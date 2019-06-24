package net.JL.admin.controller;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.render.JsonRender;
import com.jfinal.upload.UploadFile;
import net.JL.admin.file.Encode;
import net.JL.admin.file.ExportExcel;
import net.JL.admin.file.ImpScore;
import net.JL.admin.interceptor.AdminInterceptor;


@Before(AdminInterceptor.class)
public class ScoreController extends Controller {
	public void index() {
		setAttr("url", "/admin/score");
		Record user = getSessionAttr("user");
		setAttr("user", user);

		render("score.html");
	}

	/** 获取数据 */
	public void getData() {
		long count = 0;
		List<Record> scores = null;
		// 获取当前页数、每页条数
		int page = getParaToInt("page");
		int limit = getParaToInt("limit");
		String queryId = getPara("queryscore");
		if (queryId == null || queryId == "") {
			// 总条数
			count = Db.use("palmtop_campus").queryLong("SELECT count(1) FROM tb_score");
			// 记录集合
			scores = Db.find("SELECT * FROM tb_score  LIMIT ?, ?", (page - 1) * limit, limit);
		} else {
			queryId = "%" + queryId.trim() + "%";
			// 总条数
			count = Db.use("palmtop_campus")
					.queryLong("SELECT count(1) FROM tb_score where " + " student_id like '" + queryId + "'");
			// 记录集合
			scores = Db.find("SELECT * FROM tb_score where " + " student_id like '" + queryId + "' LIMIT ?, ?",
					(page - 1) * limit, limit);
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("code", 0);
		map.put("msg", "");
		map.put("count", count);
		map.put("data", scores);
		renderJson(map);
	}

	// 编辑
	public void editPage() {
		int id = getParaToInt("id");
		Record tRecord = Db.findById("tb_score", id);
		setAttr("score", tRecord);
		render("editScore.html");
	}

	// 编辑 提交
	public void editScore() {
		int id = getParaToInt("id");
		int score = getParaToInt("score");

		Record editinfo = Db.findById("tb_score", id).set("score", score);
		boolean result = Db.update("tb_score", editinfo);
		if (!result) {
			fail("修改失败，请重新提交！");
			return;
		}
		ok("修改成功！");
		return;
	}

	// 删除
	public void deleteScore() {
		String[] ids = getPara("ids").split(",");
		for (String id : ids) {
			// 删除账号信息
			Db.delete("delete from tb_score where id = ?", id);
		}
		ok("删除成功！");
		return;
	}

	// 下载模板
	public void downloadScore() throws Exception {
		// 查询
		List<Record> downloadList = Db
				.find("select '' student_id,'' student_name, '' course_name,'' score from tb_score");
		String sheetName = "学生成绩表";
		String[] headerList = { "学号", "姓名", "课程", "成绩" };
		String[] attributeList = { "student_id", "student_name", "course_name", "score" };
		Encode encode = new Encode();
		String agent = getRequest().getHeader("USER-AGENT");
		String filename = "学生成绩表.xlsx";
		filename = encode.enc(agent, filename);
		getResponse().setHeader("Content-Disposition", "attachment;filename=" + filename);
		getResponse().setContentType("application/vnd.ms-excel");
		OutputStream os = getResponse().getOutputStream();
		ExportExcel exp = new ExportExcel();
		exp.exportExcel(sheetName, headerList, downloadList, attributeList, os);
		renderNull();
	}

	// 导入
	public void impscore() {
		UploadFile uploadFile = getFile();
		File file = uploadFile.getFile();
		HashMap<String, Object> map = new HashMap<String, Object>();
		ImpScore impScore = new ImpScore();
		map = impScore.save(file);
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
