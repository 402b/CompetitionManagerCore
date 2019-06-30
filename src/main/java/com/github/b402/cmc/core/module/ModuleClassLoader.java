package com.github.b402.cmc.core.module;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ModuleClassLoader extends URLClassLoader {
    private ModuleLoader loader;
    private File file;
    private Manifest manifest;
    private JarFile jar;
    private Module module;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private URL url;

    public ModuleClassLoader(ModuleLoader loader,ModuleDescription description, ClassLoader parent, File file) throws IOException {
        super(new URL[]{file.toURI().toURL()}, parent);
        this.loader = loader;
        this.file = file;
        jar = new JarFile(file);
        manifest = jar.getManifest();
        this.url = file.toURI().toURL();

        try {
            Class<?> cls = Class.forName(description.getMainClass(), true, this);
            Class<? extends Module> modulecls = cls.asSubclass(Module.class);
            module = modulecls.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public ModuleLoader getLoader() {
        return loader;
    }

    public File getFile() {
        return file;
    }

    public Manifest getManifest() {
        return manifest;
    }

    public JarFile getJar() {
        return jar;
    }

    public Module getModule() {
        return module;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            jar.close();
        }
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        if (name.startsWith("core")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                String path = name.replace('.', '/').concat(".class");
                JarEntry entry = jar.getJarEntry(path);

                if (entry != null) {
                    byte[] classBytes;

                    try (InputStream is = jar.getInputStream(entry)) {
                        classBytes = ByteStreams.toByteArray(is);
                    } catch (IOException ex) {
                        throw new ClassNotFoundException(name, ex);
                    }


                    int dot = name.lastIndexOf('.');
                    if (dot != -1) {
                        String pkgName = name.substring(0, dot);
                        if (getPackage(pkgName) == null) {
                            try {
                                if (manifest != null) {
                                    definePackage(pkgName, manifest, url);
                                } else {
                                    definePackage(pkgName, null, null, null, null, null, null, null);
                                }
                            } catch (IllegalArgumentException ex) {
                                if (getPackage(pkgName) == null) {
                                    throw new IllegalStateException("Cannot find package " + pkgName);
                                }
                            }
                        }
                    }

                    CodeSigner[] signers = entry.getCodeSigners();
                    CodeSource source = new CodeSource(url, signers);

                    result = defineClass(name, classBytes, 0, classBytes.length, source);
                }

                if (result == null) {
                    result = super.findClass(name);
                }

                if (result != null) {
                    loader.setClass(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }

}
