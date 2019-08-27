package com.github.b402.cmc.core.module;

import com.github.b402.cmc.core.FileManager;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Module {
    String name;
    String[] dependModules;
    ModuleDescription description;
    private ClassLoader classLoader = this.getClass().getClassLoader();

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String[] getDependModules() {
        return dependModules;
    }

    @NotNull
    public ModuleDescription getDescription() {
        return description;
    }

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

    @NotNull
    public File getDataFolder() {
        File baseFolder = FileManager.INSTANCE.getBaseFolder();
        File folder = new File(baseFolder, "config" + File.pathSeparator + this.name);
        return folder;
    }

    @NotNull
    public InputStream getResource(String filename) {
        try {
            URL url = classLoader.getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    private static Map<String, ModuleClassLoader> modules = new HashMap<>();
    private static Logger logger = Logger.getLogger(Module.class);

    @Nullable
    public static Module getModule(@NotNull String name) {
        ModuleClassLoader mcl = modules.get(name);
        if (mcl != null) {
            return mcl.getModule();
        }
        return null;
    }

    public static Collection<ModuleClassLoader> getModuleClassLoader(){
        return modules.values();
    }

    public static void loadModules() {
        FileManager.INSTANCE.checkFolder();
        File modulefolder = new File(FileManager.INSTANCE.getBaseFolder(), "module");
        ModuleLoader ml = new ModuleLoader(Thread.currentThread().getContextClassLoader());
        for (File file : modulefolder.listFiles()) {
            logger.log(Level.INFO, String.format("加载模块文件: %s", file.getName()));
            ml.load(file);
        }
        Set<String> loaded = new HashSet<>();
        for (ModuleClassLoader moduleClassLoader : ml.getLoaders()) {
            Module m = moduleClassLoader.getModule();
            Collections.addAll(loaded, m.getDependModules());
        }
        for (ModuleClassLoader mcl : ml.getLoaders()) {
            modules.put(mcl.getModule().getName(), mcl);
        }
        //List<ModuleClassLoader> loading = new ArrayList<>(ml.getLoaders());
        for (ModuleClassLoader it : ml.getLoaders()) {
            Module module = it.getModule();
            if (module.getDependModules().length > 0) {
                for (String depend : module.getDependModules()) {
                    if (!loaded.contains(depend)) {
                        logger.error(
                                String.format("模块%s加载失败 缺少前置模块%s", module.getName())
                        );
                        loaded.remove(module.getName());
                        modules.remove(module.getName());
                        continue;
                    }
                }
            }
            try {
                loadModule(it);
            } catch (Throwable t) {
                logger.error(String.format("加载模块$s时发生异常", module.getName()), t);
            }
        }
        for (ModuleClassLoader it : ml.getLoaders()) {
            try {
                enableModule(it);
            } catch (Throwable t) {
                logger.error(String.format("启动模块$s时发生异常", it.getModule().getName()), t);
            }
        }
    }

    private static void enableModule(ModuleClassLoader loader) {
        if (loader.isEnable()) {
            return;
        }
        loader.setEnable(true);
        Module module = loader.getModule();
        for (String depend : module.getDependModules()) {
            ModuleClassLoader dcl = modules.get(depend);
            if (!dcl.isEnable()) {
                enableModule(dcl);
            }
        }
        loader.getModule().onEnable();
    }

    private static void loadModule(ModuleClassLoader loader) {
        if (loader.isLoad()) {
            return;
        }
        loader.setLoad(true);
        Module module = loader.getModule();
        for (String depend : module.dependModules) {
            ModuleClassLoader dcl = modules.get(depend);
            if (!dcl.isLoad()) {
                loadModule(dcl);
            }
        }
        loader.getModule().onLoad();
    }

}
