package com.github.b402;

import java.io.File;

public abstract class Module {
    private String name;
    private String[] dependModules;

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

    public File getDataFolder(){
        throw new UnsupportedOperationException();
    }


}
