package net.JL.admin.controller;

import java.util.HashMap;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import net.JL.admin.interceptor.AdminInterceptor;


@Before(AdminInterceptor.class)
public class AdvisoryController extends Controller {
	public void index() {
		setAttr("url", "/admin/advisory");
		Record user = getSessionAttr("user");
		setAttr("user", user);
		render("advisory.html");
	}

	/** 获取数据 */
	public void getData() {
		long count = 0;
		List<Record> advisorys = null;
		// 获取当前页数、每页条数
		int page = getParaToInt("page");
		int limit = getParaToInt("limit");
		// 总条数
		count = Db.use("palmtop_campus").queryLong("SELECT count(1) FROM tb_advisory");
		// 记录集合
		advisorys = Db.find("SELECT * FROM tb_advisory  LIMIT ?, ?", (page - 1) * limit, limit);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("code", 0);
		map.put("msg", "");
		map.put("count", count);
		map.put("data", advisorys);
		renderJson(map);
	}

	// 增加
	public void addAdvisory() {
		render("addAdvisory.html");
	}

	// 增加提交
	public void saveAdvisory() {
		String apartment = getPara("apartment");
		String call = getPara("call");
		// 校验是否存在此部门信息
		Record advisoryByApartment = Db.findFirst("select * from tb_advisory where apartment = ?", apartment);
		if (advisoryByApartment != null) {
			fail("新增失败，此信息已存在！");
			return;
		}
		int result = Db.update("INSERT INTO tb_advisory(apartment,`call`) VALUES(?,?)", apartment, call);
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
		Record tRecord = Db.findById("tb_advisory", id);
		setAttr("advisory", tRecord);
		render("editAdvisory.html");
	}

	// 编辑 提交
	public void editAdvisory() {
		int id = getParaToInt("id");
		String apartment = getPara("apartment");
		String call = getPara("call");
		Record editinfo = Db.findById("tb_advisory", id).set("aparment", apartment).set("call", call);
		boolean result = Db.update("tb_advisory", editinfo);
		if (!result) {
			fail("修改失败，请重新提交！");
			return;
		}
		ok("修改成功！");
		return;
	}

	// 删除
	public void deleteAdvisory() {
		String[] ids = getPara("ids").split(",");
		for (String id : ids) {
			// 删除账号信息
			Db.delete("delete from tb_advisory where id = ?", id);
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
