package com.citycheckin.common;

import com.citycheckin.entity.SysUser;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单内存 Token 管理，Demo 用途。
 * 生产环境建议替换为 Redis + JWT 方案。
 */
@Component
public class TokenManager {

    private static final long EXPIRE_MILLIS = 24 * 60 * 60 * 1000L;
    private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

    public String createToken(SysUser user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        TokenInfo info = new TokenInfo();
        info.setUserId(user.getId());
        info.setUsername(user.getUsername());
        info.setIsSuper(user.getIsSuper());
        info.setDistrictId(user.getDistrictId());
        info.setExpireTime(System.currentTimeMillis() + EXPIRE_MILLIS);
        tokenStore.put(token, info);
        return token;
    }

    public TokenInfo getTokenInfo(String token) {
        if (token == null) return null;
        TokenInfo info = tokenStore.get(token);
        if (info == null || info.getExpireTime() < System.currentTimeMillis()) {
            tokenStore.remove(token);
            return null;
        }
        return info;
    }

    public void removeToken(String token) {
        tokenStore.remove(token);
    }
}
