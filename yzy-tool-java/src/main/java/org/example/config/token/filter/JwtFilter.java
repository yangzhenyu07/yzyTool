package org.example.config.token.filter;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.example.config.token.JxToken;
import org.example.config.token.ScToken;
import org.example.config.web3.tag.Web3NodeUtils;
import org.example.exception.ExceptionEnum;
import org.example.exception.model.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * @author yangzhneyu
 * */

public class JwtFilter  extends GenericFilterBean {


    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    public JwtFilter() {
        log.info("===================token拦截配置===================");
    }

    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
            throws IOException, ServletException {

//        if (true) {
//            chain.doFilter(req, res);
//            return;
//        }
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        String requestURI = request.getRequestURI();
        // 获取排除的路径
        String excludes = getFilterConfig().getInitParameter("excludes");
        if (excludes != null) {
            List<String> excludePaths = Arrays.asList(excludes.split(","));
            for (String excludePath : excludePaths) {
                if (requestURI.matches(excludePath.replace("*", ".*"))) {
                    // 忽略处理，直接放行
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        // 刷新令牌
        final String refreshAuthHeader = request.getHeader(JxToken.REFRESH_AUTHORIZATION);
        // 校验令牌
        final String accessAuthHeader = request.getHeader(JxToken.ACCESS_AUTHORIZATION);
        response.setHeader(JxToken.ACCESS_AUTHORIZATION, refreshAuthHeader);
        // web3 tag
        final String tag = request.getHeader(Web3NodeUtils.TENANT_TAG);
        // web3 交易类还是查询类
        final String tagType = request.getHeader(Web3NodeUtils.TAG_TYPE);

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(req, res);
        }else {
            if (refreshAuthHeader == null || !refreshAuthHeader.startsWith("Bearer ")) {
                PrintWriter out = response.getWriter();
                log.error("refresh token错误：token={}",request.getRequestURL());
                out.print(JSON.toJSON(new ResponseResult(ExceptionEnum.TOKEN_ERROR)));
                out.flush();
                return;
            }
            if (accessAuthHeader == null || !accessAuthHeader.startsWith("Bearer ")) {
                PrintWriter out = response.getWriter();
                log.error("access token错误：token={}",request.getRequestURL());
                out.print(JSON.toJSON(new ResponseResult(ExceptionEnum.TOKEN_ERROR)));
                out.flush();
                return;
            }

            final String token = accessAuthHeader.substring(7);
            try {
                final Claims claims = Jwts.parser().setSigningKey(JxToken.TOKEN_KEY).parseClaimsJws(token).getBody();

                long exp=claims.getExpiration().getTime();
                if(System.currentTimeMillis()>=exp){
                    //极端情况
                    // 刷新校验令牌
                    String refreshAccessAuthHeader = ScToken.initAccessToken(refreshAuthHeader);
                    // 如果刷新令牌成功，返回新的access token到客户端
                    response.setHeader(JxToken.ACCESS_AUTHORIZATION, "Bearer " + refreshAccessAuthHeader);
                    log.info("刷新token成功：token={}", request.getRequestURL());
                }else {
                    request.setAttribute("claims", claims);
                }
            }catch (ExpiredJwtException ex){

                long exp = ex.getClaims().getExpiration().getTime();
                /**
                 *  当检验令牌过期时 判断校验令牌的过期时间与当前时间是否相差10分钟以内，在10分钟以内的话
                 *  根据刷新令牌进行刷新校验令牌
                 */
                long remainingTime = System.currentTimeMillis() - exp  ;
                // 判断令牌是否即将过期（10分钟以内）
                if (remainingTime <= 10 * 60 * 1000) {
                    // 刷新校验令牌
                    String refreshAccessAuthHeader = ScToken.initAccessToken(refreshAuthHeader);
                    // 如果刷新令牌成功，返回新的access token到客户端
                    response.setHeader(JxToken.ACCESS_AUTHORIZATION, "Bearer " + refreshAccessAuthHeader);
                    log.info("刷新token成功：token={}", request.getRequestURL());
                }else {
                    log.error("token过期：token={}",request.getRequestURL());
                    PrintWriter out = response.getWriter();
                    out.print(JSON.toJSON(new ResponseResult(ExceptionEnum.TOKEN_TIMEOUT)));
                    out.flush();
                    return;
                }
            }catch (final Exception e) {
                PrintWriter out = response.getWriter();
                out.print(JSON.toJSON(new ResponseResult(ExceptionEnum.TOKEN_TIMEOUT)));
                out.flush();
                return;
            }
            response.setHeader(JxToken.REFRESH_AUTHORIZATION, accessAuthHeader);
            try {
                if (StringUtils.isNotBlank(tag)) {
                    // 增加web3逻辑，查询类:tag传链路key，交易类:tag传rpc name key
                    Web3NodeUtils.updateChannelTag(tag, tagType);
                }
                chain.doFilter(req, res);
            } finally {
                if (StringUtils.isNotBlank(tag)) {
                    Web3NodeUtils.clear(); //  清理ThreadLocal
                }
            }
        }
    }
}