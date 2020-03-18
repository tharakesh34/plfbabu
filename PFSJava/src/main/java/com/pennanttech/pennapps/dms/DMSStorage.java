package com.pennanttech.pennapps.dms;

public enum DMSStorage {
	FS, EXTERNAL;

	public static DMSStorage getStorage(String storage) {
		for (DMSStorage dmsStorage : DMSStorage.values()) {
			if (dmsStorage.name().equals(storage)) {
				return dmsStorage;
			}
		}
		return null;
	}

	public static String getStorage(DMSStorage dmsStorage) {
		return dmsStorage == null ? "" : dmsStorage.name();
	}
}
