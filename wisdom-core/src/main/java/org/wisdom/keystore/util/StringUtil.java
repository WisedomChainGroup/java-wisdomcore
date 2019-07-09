package org.wisdom.keystore.util;

public final class StringUtil {


	public static boolean isNullOrWhitespace(final String str) {
		if (isNullOrEmpty(str)) {
			return true;
		}

		for (int i = 0; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	public static boolean isNullOrEmpty(final String str) {
		return null == str || str.isEmpty();
	}


	public static String replaceVariable(final String string, final String name, final String value) {
		return string.replace(String.format("${%s}", name), value);
	}

}
