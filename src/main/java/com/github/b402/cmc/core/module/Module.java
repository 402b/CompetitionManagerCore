package com.github.b402.cmc.core.module;

import com.github.b402.cmc.core.FileManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public abstract class Module {
    String name;
    String[] dependModules;
    ModuleDescription description;
    private ClassLoader classLoader = this.getClass().getClassLoader();

    public String getName() {
        return name;
    }

    public String[] getDependModules() {
        return dependModules;
    }

    public ModuleDescription getDescription() {
        return description;
    }

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

    public File getDataFolder() {
        File baseFolder = FileManager.INSTANCE.getBaseFolder();
        File folder = new File(baseFolder, "config" + File.pathSeparator + this.name);
        return folder;
    }

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


}
