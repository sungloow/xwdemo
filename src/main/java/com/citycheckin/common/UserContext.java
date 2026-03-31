package com.citycheckin.common;

/**
 * 利用 ThreadLocal 在请求生命周期内传递当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<TokenInfo> HOLDER = new ThreadLocal<>();

    public static void set(TokenInfo info) {
        HOLDER.set(info);
    }

    public static TokenInfo get() {
        return HOLDER.get();
    }

    public static Integer getUserId() {
        TokenInfo info = HOLDER.get();
        return info == null ? null : info.getUserId();
    }

    public static String getUsername() {
        TokenInfo info = HOLDER.get();
        return info == null ? null : info.getUsername();
    }

    public static boolean isSuperAdmin() {
        TokenInfo info = HOLDER.get();
        return info != null && Integer.valueOf(1).equals(info.getIsSuper());
    }

    public static Integer getDistrictId() {
        TokenInfo info = HOLDER.get();
        return info == null ? null : info.getDistrictId();
    }

    public static void remove() {
        HOLDER.remove();
    }
}
