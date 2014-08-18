package cn.zthz.tool.common;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cn.zthz.tool.account.AccountService;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RsaUtils {

	public static String encrypt(String rawData, String publicKey) {
		return (new BASE64Encoder()).encodeBuffer(encrypt(rawData.getBytes(), getPublicKey(publicKey)));
	}
	public static String decrypt(String encryptedData, String privateKey) {
		
		try {
			return new String(decrypt((new BASE64Decoder()).decodeBuffer(encryptedData), getPrivateKey(privateKey)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] encrypt(byte[] rawData, PublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher.doFinal(rawData);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static byte[] encrypt(byte[] rawData, PrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			return cipher.doFinal(rawData);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static String encryptInPrivateKey(String rawData, String privateKey) {
		return (new BASE64Encoder()).encodeBuffer(encrypt(rawData.getBytes(), getPrivateKey(privateKey)));
	}
	
	public static void verifyInPublicKey(String encryptedData, String publicKey){
//		Cipher c4 = Cipher.getInstance("RSA");  
        // 根据密钥，对Cipher对象进行初始化,DECRYPT_MODE表示解密模式  
//        c4.init(Cipher.DECRYPT_MODE, getPublicKey(publicKey));  
        // 解密  
//        byte[] desDecTextBytes = c4.doFinal(Base64.base64ToByteArray(cipherText));  
	}

	public static byte[] decrypt(byte[] encryptedData, PrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(encryptedData);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static PublicKey getPublicKey(String key) {
		try {

			byte[] keyBytes;
			keyBytes = (new BASE64Decoder()).decodeBuffer(key);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(keySpec);
			return publicKey;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static PrivateKey getPrivateKey(String key) {
		try {
			byte[] keyBytes = (new BASE64Decoder()).decodeBuffer(key);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
			return privateKey;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * 得到密钥字符串（经过base64编码）
	 * 
	 * @return
	 */
	public static String getKeyString(Key key) {
		try {
			byte[] keyBytes = key.getEncoded();
			String s = (new BASE64Encoder()).encode(keyBytes);
			return s;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public static void main1(String[] args) throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		// 密钥位数
		keyPairGen.initialize(1024);
		// 密钥对
		KeyPair keyPair = keyPairGen.generateKeyPair();
		// 公钥
		PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		// 私钥
		PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		String publicKeyString = getKeyString(publicKey);
		System.out.println("public key:\n" + publicKeyString);
		String privateKeyString = getKeyString(privateKey);
		System.out.println("private key:\n" + privateKeyString);
		// 加解密类
		Cipher cipher = Cipher.getInstance("RSA");// Cipher.getInstance("RSA/ECB/PKCS1Padding");
		// 明文
		byte[] plainText = "我们都很好！邮件：@sina.com".getBytes();
		// 加密
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] enBytes = cipher.doFinal(plainText);
		// 通过密钥字符串得到密钥
		publicKey = getPublicKey(publicKeyString);
		privateKey = getPrivateKey(privateKeyString);
		// 解密
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] deBytes = cipher.doFinal(enBytes);
		publicKeyString = getKeyString(publicKey);
		System.out.println("public:\n" + publicKeyString);
		privateKeyString = getKeyString(privateKey);
		System.out.println("private:\n" + privateKeyString);
		String s = new String(deBytes);
		System.out.println(s);
	}

}
