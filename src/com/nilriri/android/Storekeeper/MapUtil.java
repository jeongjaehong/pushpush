package com.nilriri.android.Storekeeper;

import java.util.StringTokenizer;

public class MapUtil {

	public static String[] Str2Map(String str, String token) {
		StringTokenizer st = null;
		String toStr[] = (String[]) null;
		int tokenCount = 0;
		int index = 0;
		int len = 0;
		try {
			len = str.length();
			for (int i = 0; i < len; i++)
				if ((index = str.indexOf((new StringBuilder(String
						.valueOf(token))).append(token).toString())) != -1)
					str = (new StringBuilder(String.valueOf(str.substring(0,
							index)))).append(token).append(" ").append(token)
							.append(str.substring(index + 2, str.length()))
							.toString();

			st = new StringTokenizer(str, token);
			tokenCount = st.countTokens();
			toStr = new String[tokenCount];
			for (int i = 0; i < tokenCount; i++)
				toStr[i] = st.nextToken();

		} catch (Exception e) {
			toStr = (String[]) null;
		}
		return (toStr);
	}

	public MapUtil() {
	}

}