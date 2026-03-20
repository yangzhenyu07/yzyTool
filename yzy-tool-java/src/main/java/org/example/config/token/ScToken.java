package org.example.config.token;

import com.google.common.collect.Maps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.example.exception.ExceptionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成token
 * @author yangzhenyu
 * */
@Component
public class ScToken implements  JxToken{
    /**过期时间*/
    public static String time ;

    /******** 赋值数据 **********/

    @Value("${config.token.time:3600}")
    private void setTime(String time) {
        ScToken.time = time;
    }
    /**
     * 生成刷新令牌和校验令牌
     * @param map
     * @return
     */
    public Map<String,Object> initToken(Map<String,String> map){
        Map<String,Object> result = Maps.newHashMap();
        long exp = System.currentTimeMillis() + (1000 * Integer.parseInt(time));
        Claims claims = new DefaultClaims();
        claims.put("age", map.get("age"));
        claims.put("name", map.get("name"));
        String accessAuthorization = Jwts.builder().setClaims(claims).setExpiration(new Date(exp)).signWith(SignatureAlgorithm.HS256, TOKEN_KEY).compact();
        long refreshExp = System.currentTimeMillis() + (1000 * 60 * 60 * 12);

        String refreshAuthorization = Jwts.builder().setClaims(claims).setExpiration(new Date(refreshExp)).signWith(SignatureAlgorithm.HS256, REFRESH_TOKEN_KEY).compact();
        result.put(ACCESS_AUTHORIZATION,"Bearer " + accessAuthorization);
        result.put(REFRESH_AUTHORIZATION,"Bearer " + refreshAuthorization);

        return result;
    }

    /**
     * 根据 refresh authorization 令牌 刷新 access authorization 令牌
     * @param refreshToken
     * @return
     */
    public static String initAccessToken(String refreshToken){
        final String token = refreshToken.substring(7);
        final Claims refreshClaims = Jwts.parser().setSigningKey(REFRESH_TOKEN_KEY).parseClaimsJws(token).getBody();

        Map<String,String> result = Maps.newHashMap();
        long exp = System.currentTimeMillis() + (1000 * Integer.parseInt(time));
        Claims claims = new DefaultClaims();
        claims.put("age", refreshClaims.get("age"));
        claims.put("name", refreshClaims.get("name"));
        String accessAuthorization = Jwts.builder().setClaims(claims).setExpiration(new Date(exp)).signWith(SignatureAlgorithm.HS256, TOKEN_KEY).compact();
        result.put(ACCESS_AUTHORIZATION,accessAuthorization);
        return accessAuthorization;
    }

}
