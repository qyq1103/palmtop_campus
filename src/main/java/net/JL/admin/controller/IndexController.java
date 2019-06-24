package net.JL.admin.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import net.JL.admin.interceptor.AdminInterceptor;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.List;
@Before(AdminInterceptor.class)
public class IndexController extends Controller {
    public void index() {
        setAttr("url", "/admin/index");
        Record user = getSessionAttr("user");
        setAttr("user", user);
        render("index.html");
    }

    /**
     * 获取数据
     */
    public void getData() {
        long count = 0;
        List<Record> adminusers = null;
        // 获取当前页数、每页条数
        int page = getParaToInt("page");
        int limit = getParaToInt("limit");
        // 总条数
        count = Db.use("palmtop_campus").queryLong("SELECT count(1) FROM tb_administrators");
        // 记录集合
        adminusers = Db.find("SELECT * FROM tb_administrators  LIMIT ?, ?", (page - 1) * limit, limit);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", count);
        map.put("data", adminusers);
        renderJson(map);
    }

    // 增加
    public void addAdministrators() {
        render("addAdministrators.html");
    }

    // 增加提交
    public void saveAdministrators() {
        String name = getPara("name");
        String password = DigestUtils.sha256Hex(getPara("password"));
        int type = getParaToInt("type");
        // 校验是否存在此管理员
        Record adminByidcard = Db.findFirst("select * from tb_administrators where name = ?", name);
        if (adminByidcard != null) {
            fail("新增失败，此信息已存在！");
            return;
        }
        int result = Db.update("INSERT INTO tb_administrators(name,password,type) VALUES(?, ?,?)", name, password,
                type);
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
        Record tRecord = Db.findById("tb_administrators", id);
        setAttr("administrators", tRecord);
        render("editAdministrators.html");
    }

    // 编辑 提交
    public void editAdministrators() {
        String id = getPara("id");
        String name = getPara("name");
        String password = DigestUtils.sha256Hex(getPara("password"));
        int type = getParaToInt("type");
        Record editinfo = Db.findById("tb_administrators", id).set("name", name).set("password", password).set("type",
                type);
        boolean result = Db.update("tb_administrators", editinfo);
        if (!result) {
            fail("修改失败，请重新提交！");
            return;
        }
        ok("修改成功！");
        return;
    }

    // 删除
    public void deleteAdministrators() {
        String[] ids = getPara("ids").split(",");
        for (String id : ids) {
            // 删除账号信息
            Db.delete("delete from tb_administrators where id = ?", id);
        }
        ok("删除成功！");
        return;
    }

    /**
     * 成功提示
     */
    private void ok(Object data) {
        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("status", "0");
        res.put("msg", data);
        renderJson(res);
    }

    /**
     * 失败提示
     */
    private void fail(Object data) {
        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("status", "-1");
        res.put("msg", data);
        renderJson(res);
    }
}
