package com.github.b402.cmc.core.module;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleLoader {
    private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private ClassLoader loader;
    private List<ModuleClassLoader> loaders = new CopyOnWriteArrayList<>();

    public ModuleLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public void load(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry des = jar.getJarEntry("module.json");
            ModuleDescription description = new ModuleDescription(jar.getInputStream(des));
            ModuleClassLoader mcl = new ModuleClassLoader(this,description,loader,jarFile);
            loaders.add(mcl);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ClassLoader getLoader() {
        return loader;
    }

    void setClass(final String name, final Class<?> clazz) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz);
        }
    }

    Class<?> getClassByName(final String name) {
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (ModuleClassLoader loader : loaders) {
                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException cnfe) {
                }
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        return null;
    }

    public List<ModuleClassLoader> getLoaders() {
        return loaders;
    }
}
