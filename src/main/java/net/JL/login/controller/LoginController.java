package net.JL.login.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;

public class LoginController extends Controller {
    public void index() {
        render("login.html");
    }

    //    立即登录
    public void login() {
        String name = getPara("account");
        Record user = Db.findFirst("select password from tb_administrators where name=?", name);
        if (user == null) {
            fail("Login failed,user don't exist!");
            return;
        }
        String password = getPara("password");
        password = DigestUtils.sha256Hex(password);
        if (!password.equals(user.getStr("password"))) {
            fail("Login failed,password mistake!");
            return;
        }
        user = Db.findFirst("select id,name from tb_administrators where name=?", name);

        setSessionAttr("user", user);

        ok("/admin/index");

        return;
    }

    /**
     * 登录超时
     */
    public void over() {
        render("over.html");
    }

    public void exit() {
        Record user = getSessionAttr("user");
        if (user != null) {
            getSession().invalidate();
        }
        redirect("/admin");
    }

    private void ok(Object msg) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", "ok");
        res.put("msg", msg);
        renderJson(res);
    }

    private void fail(Object msg) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", "fail");
        res.put("msg", msg);
        renderJson(res);
    }
}
