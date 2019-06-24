package net.JL.commom.config;

import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import net.JL.admin.controller.*;
import net.JL.login.controller.LoginController;
import net.JL.test.controller.HelloController;

public class MainConfig extends JFinalConfig {

    public static void main(String[] args) {
        JFinal.start("webapp", 8080, "/", 5);
    }

    @Override
    public void configConstant(Constants me) {
        PropKit.use("config.properties");
        me.setDevMode(PropKit.getBoolean("devMode"));
    }

    @Override
    public void configRoute(Routes me) {
//        me.add("/admin", HelloController.class,"/");
        me.add("/admin", LoginController.class,"/login");
        me.add("/admin/index", IndexController.class);
        me.add("/admin/teacher", TeacherController.class);
        me.add("/admin/student", StudentController.class);
        me.add("/admin/score", ScoreController.class);
        me.add("/admin/notices", NoticesController.class);
        me.add("/admin/news", NewsController.class);
        me.add("/admin/advisory", AdvisoryController.class);
    }

    @Override
    public void configEngine(Engine me) {
        me.addSharedFunction("/admin/common/_admin_layout.html");
    }

    @Override
    public void configPlugin(Plugins me) {
        DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("Url"), PropKit.get("User"), PropKit.get("Pwd").trim());
        me.add(druidPlugin);
        ActiveRecordPlugin arp = new ActiveRecordPlugin("palmtop_campus", druidPlugin);
        me.add(arp);
    }

    @Override
    public void configInterceptor(Interceptors me) {

    }

    @Override
    public void configHandler(Handlers me) {

    }
}
