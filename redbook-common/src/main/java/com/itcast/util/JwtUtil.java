package com.itcast.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

/**
 * jwt工具类
 */
public class JwtUtil {
    private static final String SECRET_KEY = "itcast";
    private static final long EXPIRE_TIME = 60L * 60 * 1000 * 24 * 365;

    /**
     * 生成token
     * @param userId
     * @return
     */
    public static String createToken(Integer userId) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .sign(algorithm);
    }

    /**
     * 解析token
     * @param token
     * @return
     */
    public static String parseToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }
}
