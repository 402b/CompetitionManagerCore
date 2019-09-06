package com.github.b402.cmc.core;

import org.jetbrains.annotations.NotNull;

public enum JudgeType {
    NORMAL("裁判", Permission.JUDGE),
    PROJECT("项目裁判", Permission.PROJECT_JUDGE);
    private final String display;
    private final Permission permission;

    JudgeType(String display, Permission permission) {
        this.display = display;
        this.permission = permission;
    }

    @NotNull
    public String getDisplay() {
        return display;
    }

    @NotNull
    public Permission getPermission() {
        return permission;
    }
}
