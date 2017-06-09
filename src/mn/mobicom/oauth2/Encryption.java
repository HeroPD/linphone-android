package mn.mobicom.oauth2;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class Encryption {

	static final byte[] key = {0x50, 0x79, 0x41, 0x01, 0x05, 0x68, 0x26, 0x11, 0x33, 0x75, 0x11, 0x01, 0x36, 0x01,
			0x74, 0x61};
	static final byte[] iv = {0x40, 0x09, 0x54, 0x01, 0x29, 0x14, 0x31, 0x01, 0x52, 0x38, 0x60, 0x35, 0x31, 0x07, 0x48,
			0x23};

	public static String encrypt(String text) throws Exception {
		return encrypt(text, iv, key);
	}

	private static String encrypt(String text, byte[] iv, byte[] key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
		//BASE64Encoder encoder = new BASE64Encoder();
		return Base64.encodeToString(results,Base64.DEFAULT);
	}

	public static String decrypt(String text) throws Exception {
		return decrypt(text, iv, key);
	}

	private static String decrypt(String text, byte[] iv, byte[] key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		//BASE64Decoder decoder = new BASE64Decoder();
		byte[] results = cipher.doFinal(Base64.decode(text,Base64.DEFAULT));
		return new String(results, "UTF-8");
	}

}
