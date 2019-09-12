package com.github.b402.cmc.core;

import com.github.b402.cmc.core.event.EventBus;
import com.github.b402.cmc.core.event.server.ContextInitEvent;
import com.github.b402.cmc.core.module.Module;
import com.github.b402.cmc.core.module.ModuleClassLoader;
import com.github.b402.cmc.core.servlet.DataServlet;
import com.github.b402.cmc.core.sort.Sort;
import com.github.b402.cmc.core.sql.SQLManager;
import com.github.b402.cmc.core.token.TokenManager;

import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
        EventBus.INSTANCE.callEvent(cie);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Logger.getLogger(ContextManager.class).info("开始关闭模块");
        for (ModuleClassLoader mcl : Module.getModuleClassLoader()) {
            mcl.getModule().onDisable();
        }
    }
}
