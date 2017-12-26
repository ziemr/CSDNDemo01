package com.android.gastove.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encode {

	//codeType
	//"MD5"
	//"SHA"
	public static String getEncode(String codeType, String content) {
		try {
			MessageDigest digest = MessageDigest.getInstance(codeType);
			digest.reset();
			digest.update(content.getBytes());
			StringBuilder builder = new StringBuilder();
			for (byte b : digest.digest()) {
				builder.append(Integer.toHexString((b >> 4) & 0xf));
				builder.append(Integer.toHexString(b & 0xf));
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
