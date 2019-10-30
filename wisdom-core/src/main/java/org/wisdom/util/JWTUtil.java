package org.wisdom.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class JWTUtil {
    private final static String APP_ID = "YHWS845682HYESE12yhsd187451289";
    private final static String APP_SECRET = "JHGSYW87453624JHHS";
    private final static String id = "1";
    private final static String issuer = "admin";
    private final static String subject = "JWTToken";

    //Sample method to construct a JWT
    public static String createJWT(long ttlMillis) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(APP_ID + APP_SECRET);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signingKey, signatureAlgorithm);

        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();

    }

    //Sample method to validate and read the JWT
    public static boolean parseJWT(String jwt) {
        try {
            //This line will throw an exception if it is not a signed JWS (as expected)
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(APP_ID + APP_SECRET))
                    .parseClaimsJws(jwt).getBody();
            System.out.println("ID: " + claims.getId());
            System.out.println("Subject: " + claims.getSubject());
            System.out.println("Issuer: " + claims.getIssuer());
            System.out.println("Expiration: " + claims.getExpiration());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    public static void main(String[] args) {
//        long exp = 3600000;//过期时间为1h
//        System.out.println("create:"+createJWT(exp));
//
//        boolean claims = JWTUtil.parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTcyNDE2MTYwLCJzdWIiOiJKV1RUb2tlbiIsImlzcyI6ImFkbWluIiwiZXhwIjoxNTcyNDE5NzYwfQ.oBfuzZVRxiDXiMOGBYdHHKHDJzu9P4Kdb-zdtaD-Jvo");
//        System.out.println(claims);
//    }
}
