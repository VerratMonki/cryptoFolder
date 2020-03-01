package com.nikondsl.cryptoFolder.cypher;

import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.cipher.CryptoCipherFactory;
import org.apache.commons.crypto.stream.CryptoOutputStream;
import org.apache.commons.crypto.utils.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

  import java.nio.charset.StandardCharsets;
  import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

  import javax.crypto.Cipher;
  import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

  import org.apache.commons.crypto.cipher.CryptoCipher;
  import org.apache.commons.crypto.cipher.CryptoCipherFactory;
  import org.apache.commons.crypto.cipher.CryptoCipherFactory.CipherProvider;
  import org.apache.commons.crypto.utils.Utils;

public class CipherHelper {
	private static String secretKey = "boooooooooom!!!!";
	private static String salt = "ssshhhhhhhhhhh!!!!";
	
	
	public String encript(final String sampleInput) throws IOException, BadPaddingException, ShortBufferException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException {
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
		
		Cipher encipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		encipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
		
		System.out.println("input:  " + sampleInput);
		
		return Base64.getEncoder().encodeToString(encipher.doFinal(sampleInput.getBytes("UTF-8")));
		
	}
	
	public static void main(String[] args) throws BadPaddingException, InvalidKeyException, IOException, ShortBufferException, IllegalBlockSizeException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException, NoSuchAlgorithmException {
		CipherHelper cipher = new CipherHelper();
		System.err.println("out="+ cipher.encript("hello"));
	}
	
	private static byte[] getUTF8Bytes(String input) {
		return input.getBytes(StandardCharsets.UTF_8);
	}
}
