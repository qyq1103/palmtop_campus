package net.JL.admin.controller;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import net.JL.admin.interceptor.AdminInterceptor;

@Before(AdminInterceptor.class)
public class NewsController extends Controller {
	public void index() {
		setAttr("url", "/admin/news");
		Record user = getSessionAttr("user");
		setAttr("user", user);
		render("news.html");
	}

	/** 获取数据 */
	public void getData() {
		long count = 0;
		List<Record> newsLists = null;
		// 获取当前页数、每页条数
		int page = getParaToInt("page");
		int limit = getParaToInt("limit");
		// 总条数
		count = Db.use("palmtop_campus").queryLong("SELECT count(1) FROM tb_news");
		// 记录集合
		newsLists = Db.find("SELECT * FROM tb_news  LIMIT ?, ?", (page - 1) * limit, limit);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("code", 0);
		map.put("msg", "");
		map.put("count", count);
		map.put("data", newsLists);
		renderJson(map);
	}

	// 增加
	public void addNews() {
		render("addNews.html");
	}

	// 增加提交
	public void saveNews() {
		String title = getPara("title");
		String desc = getPara("desc");
		String img_url = getPara("img_url");
		String img="";
		if (!img_url.isEmpty()) {
			img = getPara("img_base64");
		}
		System.out.println(img);
		// 校验是否存在此通知
		Record newsByApartment = Db.findFirst("select * from tb_news where title = ?", title);
		if (newsByApartment != null) {
			fail("新增失败，此信息已存在！");
			return;
		}
		// 获取当前时间
		Date update_at = new Date(new java.util.Date().getTime());
		int result = Db.update("INSERT INTO tb_news(title,rep,img,update_at) VALUES(?,?,?,?)", title, desc, img,
				update_at);
		if (result != 1) {
			fail("新增失败，请重新提交！");
			return;
		}
		ok("新增成功！");
		return;
	}

	// 图片上传请求
	public void uploadImage() {
		UploadFile file = getFile();
		System.out.println("文件名字：" + file.getFileName() + "\n" + getAttr("src"));
		String path = getSession().getServletContext().getRealPath("upload");
		String url = path + "\\" + file.getFileName();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("code", 0);
		map.put("msg", "图片添加成功");
		map.put("url", url);
		renderJson(map);
	}

	// 删除
	public void deleteNews() {
		String[] ids = getPara("ids").split(",");
		for (String id : ids) {
			// 删除账号信息
			Db.delete("delete from tb_news where id = ?", id);
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
