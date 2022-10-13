package com.pennanttech.extension;

import java.util.Map;

import com.pennanttech.extension.implementation.IFeatureExtension;
import com.pennanttech.pennapps.core.FactoryException;

public final class FeatureExtension {

	private FeatureExtension() {
		super();
	}

	private static IFeatureExtension extension;

	private static Map<String, Object> getExtensions(String module) {
		if (extension == null) {
			initilizeExtension();
		}

		return getFeatureExtension(module);
	}

	private static Map<String, Object> getFeatureExtension(String module) {
		switch (module) {
		case "MANDATE":
			return extension.getMandateExtensions();
		case "PRESENTMENT":
			return extension.getPresentmentExtensions();
		case "ACCOUNTING":
			return extension.getAccountingExtensions();

		default:
			return extension.getCustomConstants();
		}
	}

	private static void initilizeExtension() {
		try {
			Object object = Class.forName("com.pennanttech.extension.implementation.FeatureExtension")
					.getDeclaredConstructor().newInstance();

			if (object != null) {
				extension = (IFeatureExtension) object;
			} else {
				throw new FactoryException(
						"The IFeature implimentation should be available in the client exetension layer to override the implimentation constants.");
			}
		} catch (Exception e) {
			throw new FactoryException(
					"The IFeature implimentation should be available in the client exetension layer to override the implimentation constants.");

		}
	}

	/**
	 * Returns the value as boolean from extended constants to which the specified key is mapped, or defaultValue if the
	 * extended constants contain no mapping for the key.
	 * 
	 * @param extendedConstants The constants specified in the extension layer.
	 * @param key               The key whose associated value is to be returned.
	 * @param defaultValue      The default value that has to be used if the extended constants contain no mapping for
	 *                          the key.
	 * @return the value as boolean from extended constants to which the specified key is mapped, or defaultValue if
	 *         this map contain no mapping for the key.
	 */
	public static boolean getValueAsBoolean(String module, String key, boolean defaultValue) {
		try {
			return (boolean) getExtensions(module).computeIfAbsent(key, ft -> defaultValue);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Returns the value as String from extended constants to which the specified key is mapped, or defaultValue if the
	 * extended constants contain no mapping for the key.
	 * 
	 * @param extendedConstants The constants specified in the extension layer.
	 * @param key               The key whose associated value is to be returned.
	 * @param defaultValue      The default value that has to be used if the extended constants contain no mapping for
	 *                          the key.
	 * @return the value as String from extended constants to which the specified key is mapped, or defaultValue if this
	 *         map contain no mapping for the key.
	 */
	public static String getValueAsString(String module, String key, String defaultValue) {
		try {
			return (String) getExtensions(module).computeIfAbsent(key, ft -> defaultValue);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static int getValueAsInt(String module, String key, int defaultValue) {
		try {
			return (int) getExtensions(module).computeIfAbsent(key, ft -> defaultValue);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static Object getValueAsObject(String module, String key, Object defaultValue) {
		try {
			return getExtensions(module).computeIfAbsent(key, ft -> defaultValue);
		} catch (Exception ex) {
			return defaultValue;
		}
	}
}
