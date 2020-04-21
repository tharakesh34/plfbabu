package com.pennanttech.pennapps.dms;

import java.io.IOException;
import java.util.Locale;

public class OSInfo {
	public enum OS {
		WINDOWS, UNIX, MAC, OTHER;

	}

	private static OS os = OS.OTHER;

	static {
		try {
			String osName = System.getProperty("os.name");
			if (osName == null) {
				throw new IOException("os.name not found");
			}
			osName = osName.toLowerCase(Locale.ENGLISH);
			if (osName.contains("windows")) {
				os = OS.WINDOWS;
			} else if (osName.contains("linux") || osName.contains("mpe/ix") || osName.contains("freebsd")
					|| osName.contains("irix") || osName.contains("digital unix") || osName.contains("unix")) {
				os = OS.UNIX;
			} else if (osName.contains("mac os")) {
				os = OS.MAC;
			} else {
				os = OS.OTHER;
			}

		} catch (Exception ex) {
			os = OS.OTHER;
		}
	}

	public static OS getOs() {
		return os;
	}
}