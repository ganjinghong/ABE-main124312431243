package com.abe.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {
    public static String encrypt(String password) {
        try {
            // 创建一个 MessageDigest 对象，指定使用 MD5 哈希算法
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将密码转换成字节数组，并更新 MessageDigest 对象
            md.update(password.getBytes());
            // 计算哈希值，并将结果转换成十六进制字符串
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to encrypt password", e);
        }
    }
}
