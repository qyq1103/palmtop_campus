package net.JL.admin.controller;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import net.JL.admin.interceptor.AdminInterceptor;


@Before(AdminInterceptor.class)
public class NoticesController extends Controller {
	public void index() {
		setAttr("url", "/admin/notices");
		Record user = getSessionAttr("user");
		setAttr("user", user);
		render("notices.html");
	}

	/** 获取数据 */
	public void getData() {
		long count = 0;
		List<Record> notices = null;
		// 获取当前页数、每页条数
		int page = getParaToInt("page");
		int limit = getParaToInt("limit");
		// 总条数
		count = Db.use("palmtop_campus").queryLong("SELECT count(1) FROM tb_notices");
		// 记录集合
		notices = Db.find("SELECT * FROM tb_notices  LIMIT ?, ?", (page - 1) * limit, limit);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("code", 0);
		map.put("msg", "");
		map.put("count", count);
		map.put("data", notices);
		renderJson(map);
	}

	// 增加
	public void addNotices() {
		render("addNotices.html");
	}

	// 增加提交
	public void saveNotices() {
		String title = getPara("title");
		String notice_body = getPara("notice_body");
		String department = getPara("department");
		// 校验是否存在此通知
		Record noticesByApartment = Db.findFirst("select * from tb_notices where title = ?", title);
		if (noticesByApartment != null) {
			fail("新增失败，此信息已存在！");
			return;
		}
		// 获取当前时间
		Date release_time = new Date(new java.util.Date().getTime());
		int result = Db.update("INSERT INTO tb_notices(title,notice_body,department,release_time) VALUES(?,?,?,?)",
				title, notice_body, department, release_time);
		if (result != 1) {
			fail("新增失败，请重新提交！");
			return;
		}
		ok("新增成功！");
		return;
	}

	// 编辑
	public void editPage() {
		int id = getParaToInt("id");
		Record tRecord = Db.findById("tb_notices", id);
		setAttr("notices", tRecord);
		render("editNotices.html");
	}

	// 编辑 提交
	public void editNotices() {
		int id = getParaToInt("id");
		String title = getPara("title");
		String notice_body = getPara("notice_body");
		String department = getPara("department");

		Record editinfo = Db.findById("tb_notices", id).set("title", title).set("notice_body", notice_body).set("department", department);
		boolean result = Db.update("tb_notices", editinfo);
		if (!result) {
			fail("修改失败，请重新提交！");
			return;
		}
		ok("修改成功！");
		return;
	}

	// 删除
	public void deleteNotices() {
		String[] ids = getPara("ids").split(",");
		for (String id : ids) {
			// 删除账号信息
			Db.delete("delete from tb_notices where id = ?", id);
		}
		ok("删除成功！");
		return;
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
