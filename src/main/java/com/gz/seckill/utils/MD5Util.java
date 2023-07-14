package com.gz.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

//    进行加密的盐：salt（盐）是一个随机生成的额外的数据，用于增加密码的复杂度和安全性。
//    它被添加到原始密码中，然后再进行加密操作。
//    使用salt可以防止相同的密码在加密后产生相同的哈希值，从而增加破解的难度。
//    salt通常会与密码一起存储在数据库中，以便在验证密码时使用相同的salt进行加密操作。
//    这样，可以使用相同的salt和相同的加密算法对用户输入的密码进行加密，
//    并将结果与数据库中存储的加密后的密码进行比对，从而验证密码的正确性。
    private static final String salt = "1a2b3c4d";

    /***
     * 第一次加密（前端实现），前端表单获取到的密码进行加密。
     * 防止前端脚本恶意获取密码
     * 第一次加密：salt获取和md5由前端完成
     * @param password
     * @return
     */
    public static String frontendMD5(String password) {
//        只拿一部分的盐进行加密
        String str = "" + salt.charAt(0) + salt.charAt(2) + password + salt.charAt(5) + salt.charAt(4);
//        调用MD5方法；实现加密
        return md5(str);
    }

    /***
     * 第二次加密，后端存入数据库时需要再次加密
     * 防止数据库泄露带来的密码泄露
     * 第二次加密的salt：随机生成
     * @param password
     * @param salt
     * @return
     */
    public static String storageMD5(String password, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + password + salt.charAt(5) + salt.charAt(4);;
        return md5(str);
    }

//    拿到两次加密后的数据返回
    public static String frontendToStorageMD5(String password, String salt) {
        String frPass = frontendMD5(password);
        String dbPass = storageMD5(frPass, salt);
        return dbPass;
    }
//main方法测试
    public static void main(String[] args) {
        System.out.println(frontendMD5("123456"));//d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(storageMD5("d3b1294a61a07da9b49b6e22b2cbd7f9", "1a2b3c4d"));//b7797cce01b4b131b433b6acf4add449
        System.out.println(frontendToStorageMD5("123456", "1a2b3c4d"));
    }
}
