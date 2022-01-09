package org.cuckoo.universal.utils.db;

import java.util.UUID;

public class IDUtils {

	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
