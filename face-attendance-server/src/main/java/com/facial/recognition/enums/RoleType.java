package com.facial.recognition.enums;

/**
 * 系统内置角色枚举，兼容中文角色名称
 */
public enum RoleType {
    ADMIN("系统管理员"),
    TEACHER("教师"),
    STUDENT("学生");

    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RoleType fromRoleName(String roleName) {
        if (roleName == null) {
            return null;
        }
        for (RoleType type : values()) {
            if (type.displayName.equals(roleName) || type.name().equalsIgnoreCase(roleName)) {
                return type;
            }
        }
        return null;
    }
}

