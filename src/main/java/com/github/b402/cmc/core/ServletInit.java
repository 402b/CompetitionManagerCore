package com.github.b402.cmc.core;

import com.github.b402.cmc.core.module.Module;
import com.github.b402.cmc.core.module.ModuleClassLoader;
import com.github.b402.cmc.core.module.ModuleLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletInit implements ServletContextListener {
    public void contextInitialized(ServletContextEvent evt) {
        FileManager.INSTANCE.checkFolder();
        File modules = new File(FileManager.INSTANCE.getBaseFolder(),"module");
        ModuleLoader ml = new ModuleLoader(this.getClass().getClassLoader());
        for (File file : modules.listFiles()) {
            ml.load(file);
        }
        Set<String> loaded = ml.getLoaders()
                .stream()
                .map(ModuleClassLoader::getModule)
                .flatMap(m -> Arrays.stream(m.getDependModules()))
                .collect(Collectors.toSet());
        List<ModuleClassLoader> loading = new ArrayList<>(ml.getLoaders());
        for (ModuleClassLoader mc : loading) {
            Module module = mc.getModule();
            if(module.getDependModules().length > 0){
                for (String depend : module.getDependModules()) {
                    if(!loaded.contains(depend)){

                    }
                }
            }
        }

    }
}
