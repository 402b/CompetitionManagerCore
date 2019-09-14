package com.github.b402.cmc.core;

import com.github.b402.cmc.core.configuration.Configuration;
import com.github.b402.cmc.core.event.EventBus;
import com.github.b402.cmc.core.event.server.ContextInitEvent;
import com.github.b402.cmc.core.module.Module;
import com.github.b402.cmc.core.module.ModuleClassLoader;
import com.github.b402.cmc.core.servlet.DataServlet;
import com.github.b402.cmc.core.sort.Sort;
import com.github.b402.cmc.core.sql.SQLManager;
import com.github.b402.cmc.core.token.TokenManager;

import org.apache.log4j.Logger;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import kotlin.Unit;

public class ContextManager implements ServletContextListener {
    private static ServletContext servletContext;

    public static ServletContext getServletContext() {
        return servletContext;
    }

    public void contextInitialized(ServletContextEvent evt) {
        servletContext = evt.getServletContext();
        SQLManager.init();
        DataServlet.initDataService();
        TokenManager.init();
        Sort.init();
        Logger.getLogger(ContextManager.class).info("开始初始化模块");
        Module.loadModules();
        ContextInitEvent cie = new ContextInitEvent();
        EventBus.callEvent(cie);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Logger.getLogger(ContextManager.class).info("开始关闭模块");
        for (ModuleClassLoader mcl : Module.getModuleClassLoader()) {
            mcl.getModule().onDisable();
        }
    }

    private void checkAdmin() {
        File f = new File(FileManager.getBaseFolder(), "config.json");
        if (!f.exists()) {
            FileManager.saveResources(f, Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.json")));
        }
        Configuration config = new Configuration(f);
        List<Number> admin = config.getNumberList("Admin");
        SQLManager.operateConnection((conn) -> {
            try {
                Statement stn = conn.createStatement();
                stn.execute("UPDATE User SET Data = JSON_REPLACE(Data,'$.permission','VERIFIED','$.verified',TRUE) WHERE JSON_EXTRACT(Data,'$.permission') = 'ADMIN'");
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE User SET Data = JSON_REPLACE(Data,'$.permission','ADMIN') WHERE UID = ?"
                );
                for(Number id : admin){
                    ps.setInt(1,id.intValue());
                    ps.executeUpdate();
                }
                stn.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return Unit.INSTANCE;
        });
    }
}
