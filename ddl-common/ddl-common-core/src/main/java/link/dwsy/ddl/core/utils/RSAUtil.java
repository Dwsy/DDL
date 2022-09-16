package link.dwsy.ddl.core.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加密
 * 非对称加密，有公钥和私钥之分，公钥用于数据加密，私钥用于数据解密。加密结果可逆
 * 公钥一般提供给外部进行使用，私钥需要放置在服务器端保证安全性。
 * 特点：加密安全性很高，但是加密速度较慢
 *
 */
public class RSAUtil {

	private static final String PUBLIC_KEY =
			"MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA5hJHATNnoqYHI6M2OTA1\n" +
			"qX30KJKrKQcpBCvMXipV9oTQcMwDLYwSbI/uL1PQawC7sxPb9PKYtZf/mNT6W79I\n" +
			"HB+s6XJZrW2z++KYKiX8KIIXN1Qimf6jmEPGpfcOA1midPlVN8vRDG3FXVoKB/Ks\n" +
			"7X0iDiHf2FEc/s/0mQYlNH6jVCFyrRvAiGqGMegb1hqvSrl56fvjUkCr04jZhqJ6\n" +
			"+FyEnRWGqex94OcYHqEjZCy2TI2WpcnIfNUeOHGYTZvfiek1VafzwTzd9G1Wr4xW\n" +
			"6aNJ/GmlQbUyWwliqqQp6NXnTzRDORycIJD3JyMOpNmlkJMmNw5C3DN/vA753LbU\n" +
			"GhBLiu8yBVNLLecXhBV4bIayCIvRZj0CyxrjHwUx7axopDK6BnzVNeEe+HkPi0S7\n" +
			"kfPWbllcRa3jZ5RJOeX4z7Q67Gu5aUeJmrYw/IfiXUEy6qkzYy6ZQ5dY8fH1bbXK\n" +
			"R/vkiUJ8l3AF2dLfVq42ayaNgt8zh8GjkrsU03vc8W+m0kw7NmBGUpxYfBkJcP1Q\n" +
			"/Z4YjPlUzGAPj8xbfD17hFUQX2+GqG8INK5dCUkey+yx+au8DjSFMEnKNlDg6uka\n" +
			"2t16LSt2AT54ImnlqGt04a/fXruZvfKtz3CN5RmaSrTv5JcvJtjqtlz8E0WDXVAS\n" +
			"HcE948KL4g0WaGqwpoNOuL8CAwEAAQ==";

	private static final String PRIVATE_KEY =
			"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\n" +
			"5EPg8Cor5bJbmttGz4sU+4+NTzqCbA==";

	public static void main(String[] args) throws Exception{
		String str = RSAUtil.encrypt("123456");
		System.out.println(str);
		System.out.println(RSAUtil.decrypt(str));
	}

	public static String getPublicKeyStr(){
		return PUBLIC_KEY;
	}
	public static RSAPublicKey getPublicKey() throws Exception {
		byte[] decoded = Base64.decodeBase64(PUBLIC_KEY);
		return (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(decoded));
	}

	public static RSAPrivateKey getPrivateKey() throws Exception {
		byte[] decoded = Base64.decodeBase64(PRIVATE_KEY);
		return (RSAPrivateKey) KeyFactory.getInstance("RSA")
				.generatePrivate(new PKCS8EncodedKeySpec(decoded));
	}
	
	public static RSAKey generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024, new SecureRandom());
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
		String privateKeyString = new String(Base64.encodeBase64(privateKey.getEncoded()));
		return new RSAKey(privateKey, privateKeyString, publicKey, publicKeyString);
	}

	public static String encrypt(String source) throws Exception {
		byte[] decoded = Base64.decodeBase64(PUBLIC_KEY);
		RSAPublicKey rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(decoded));
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(1, rsaPublicKey);
		return Base64.encodeBase64String(cipher.doFinal(source.getBytes(StandardCharsets.UTF_8)));
	}

	public static Cipher getCipher() throws Exception {
		byte[] decoded = Base64.decodeBase64(PRIVATE_KEY);
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
				.generatePrivate(new PKCS8EncodedKeySpec(decoded));
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(2, rsaPrivateKey);
		return cipher;
	}

	public static String decrypt(String text) throws Exception {
		Cipher cipher = getCipher();
		byte[] inputByte = Base64.decodeBase64(text.getBytes(StandardCharsets.UTF_8));
		return new String(cipher.doFinal(inputByte));
	}
	
	public static class RSAKey {
		  private RSAPrivateKey privateKey;
		  private String privateKeyString;
		  private RSAPublicKey publicKey;
		  public String publicKeyString;

		  public RSAKey(RSAPrivateKey privateKey, String privateKeyString, RSAPublicKey publicKey, String publicKeyString) {
		    this.privateKey = privateKey;
		    this.privateKeyString = privateKeyString;
		    this.publicKey = publicKey;
		    this.publicKeyString = publicKeyString;
		  }

		  public RSAPrivateKey getPrivateKey() {
		    return this.privateKey;
		  }

		  public void setPrivateKey(RSAPrivateKey privateKey) {
		    this.privateKey = privateKey;
		  }

		  public String getPrivateKeyString() {
		    return this.privateKeyString;
		  }

		  public void setPrivateKeyString(String privateKeyString) {
		    this.privateKeyString = privateKeyString;
		  }

		  public RSAPublicKey getPublicKey() {
		    return this.publicKey;
		  }

		  public void setPublicKey(RSAPublicKey publicKey) {
		    this.publicKey = publicKey;
		  }

		  public String getPublicKeyString() {
		    return this.publicKeyString;
		  }

		  public void setPublicKeyString(String publicKeyString) {
		    this.publicKeyString = publicKeyString;
		  }
		}
}