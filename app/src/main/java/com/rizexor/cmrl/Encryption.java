package com.rizexor.cmrl;

import java.nio.charset.Charset;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class Encryption {
    public static String generateRandomHex(int i) {
        int i2 = i * 2;
        Random random = new Random();
        String str = "";
        for (int i3 = 0; i3 < i2; i3++) {
            int nextInt = random.nextInt(16);
            str = str + "abcdef0123456789".charAt(nextInt);
        }
        return str.replaceAll("00", "11");
    }

    private static String dataToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length; i++) {
            sb.append(String.format("%02x", Byte.valueOf(bArr[i])));
        }
        return sb.toString();
    }

    private static byte[] dataFromHexString(String str) throws Exception {
        String lowerCase = str.trim().replaceAll("[ ]", "").toLowerCase();
        int length = lowerCase.length();
        byte[] bArr = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            char charAt = lowerCase.charAt(i);
            char charAt2 = lowerCase.charAt(i + 1);
            if ("abcdef0123456789".contains("" + charAt)) {
                if ("abcdef0123456789".contains("" + charAt2)) {
                    bArr[i / 2] = (byte) ((Character.digit(charAt, 16) << 4) + Character.digit(charAt2, 16));
                }
            }
        }
        return bArr;
    }

    public static String encryptData(String str, String privateKey) throws Exception {
        checkKey(privateKey);
        String iv16byte = generateRandomHex(16);
        String __encryptData = __encryptData(dataToHexString(str.getBytes(Charset.forName("UTF-8"))), privateKey, iv16byte);
        String generateRandomHex = generateRandomHex(16);
        String __computeHMAC = __computeHMAC(iv16byte, __encryptData, privateKey, generateRandomHex);
        return iv16byte + generateRandomHex + __computeHMAC + __encryptData;
    }

    public static String decryptData(String str, String str2) throws Exception {
        checkKey(str2);
        if (str.length() > 128) {
            String substring = str.substring(0, 32);
            String substring2 = str.substring(32, 64);
            String substring3 = str.substring(64, 128);
            String substring4 = str.substring(128);
            if (__computeHMAC(substring, substring4, str2, substring2).equalsIgnoreCase(substring3)) {
                return new String(dataFromHexString(__decryptData(substring4, str2, substring)), Charset.forName("UTF-8"));
            }
        }
        return null;
    }

    private static void checkKey(String str) throws Exception {
        String lowerCase = str.trim().replaceAll("[ ]", "").toLowerCase();
        if (lowerCase.length() != 64) {
            throw new Exception("key length is not 256 bit (64 hex characters)");
        }
        for (int i = 0; i < lowerCase.length(); i += 2) {
            if (lowerCase.charAt(i) == '0' && lowerCase.charAt(i + 1) == '0') {
                throw new Exception("key cannot contain zero byte block");
            }
        }
    }

    private static String __computeHMAC(String str, String str2, String str3, String str4) throws Exception {
        String lowerCase = str3.trim().replaceAll("[ ]", "").toLowerCase();
        String lowerCase2 = str4.toLowerCase();
        byte[] bytes = (str + str2 + lowerCase).toLowerCase().getBytes(Charset.forName("UTF-8"));
        byte[] bytes2 = lowerCase2.getBytes(Charset.forName("UTF-8"));
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(bytes2, "HmacSHA256"));
        return dataToHexString(mac.doFinal(bytes));
    }

    private static String __encryptData(String str, String privateKey, String iv) throws Exception {
        byte[] dataBytes = dataFromHexString(str);
        byte[] passBytes = dataFromHexString(privateKey);
        byte[] IvBytes = dataFromHexString(iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(passBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, secretKeySpec, new IvParameterSpec(IvBytes));
        return dataToHexString(cipher.doFinal(dataBytes));
    }

    private static String __decryptData(String str, String str2, String str3) throws Exception {
        byte[] dataFromHexString = dataFromHexString(str);
        byte[] dataFromHexString2 = dataFromHexString(str2);
        byte[] dataFromHexString3 = dataFromHexString(str3);
        SecretKeySpec secretKeySpec = new SecretKeySpec(dataFromHexString2, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, secretKeySpec, new IvParameterSpec(dataFromHexString3));
        return dataToHexString(cipher.doFinal(dataFromHexString));
    }
}