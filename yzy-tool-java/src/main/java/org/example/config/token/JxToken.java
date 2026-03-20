package org.example.config.token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * token 解析
 * @author yangzhenyu
 * */
public interface  JxToken {
    // 检验令牌秘钥
    String TOKEN_KEY = "secretkey";
    // 刷新令牌秘钥
    String REFRESH_TOKEN_KEY = "osSDIIyzykey";

    String EXPIRATION = "expiration";
    String ACCESS_AUTHORIZATION = "accessToken";
    String REFRESH_AUTHORIZATION = "refreshToken";

    default  Map<String,Object> getAccessInfo(HttpServletRequest request) {
        final String authHeader = request.getHeader(ACCESS_AUTHORIZATION);
        final String token = authHeader.substring(7);
        final Claims claims = Jwts.parser().setSigningKey(TOKEN_KEY).parseClaimsJws(token).getBody();
        Map<String,Object> map = new HashMap<>();
        map.put("name", claims.get("name"));
        map.put("age", claims.get("age"));
        Date expiration = claims.getExpiration();  // 获取 'exp' claim (过期时间)
        map.put(EXPIRATION, expiration.getTime());
        return map;
    }

    default  Map<String,Object> getRefreshInfo(String authHeader) {
        final String token = authHeader.substring(7);
        final Claims claims = Jwts.parser().setSigningKey(REFRESH_TOKEN_KEY).parseClaimsJws(token).getBody();
        Map<String,Object> map = new HashMap<>();
        map.put("name", claims.get("name"));
        map.put("age", claims.get("age"));
        Date expiration = claims.getExpiration();  // 获取 'exp' claim (过期时间)
        map.put(EXPIRATION, expiration.getTime());
        return map;
    }


}
