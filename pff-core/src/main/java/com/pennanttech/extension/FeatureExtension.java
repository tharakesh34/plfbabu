package com.pennanttech.extension;

import java.util.Map;

import com.pennanttech.extension.implementation.IFeatureExtension;
import com.pennanttech.pennapps.core.FactoryException;

public abstract class FeatureExtension {

	private static IFeatureExtension featureExtension;

	private static Map<String, Object> getExtensions() {
		if (featureExtension == null) {
			initilizeExtension();
		}

		return featureExtension.getCustomConstants();
	}

	private static void initilizeExtension() {
		try {
			Object object = Class.forName("com.pennanttech.extension.implementation.FeatureExtension").newInstance();
			if (object != null) {
				featureExtension = (IFeatureExtension) object;
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
	public static boolean getValueAsBoolean(String key, boolean defaultValue) {
		try {
			return (boolean) getExtensions().computeIfAbsent(key, ft -> defaultValue);
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
	public static String getValueAsString(String key, String defaultValue) {
		try {
			return (String) getExtensions().computeIfAbsent(key, ft -> defaultValue);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static int getValueAsInt(String key, int defaultValue) {
		try {
			return (int) getExtensions().computeIfAbsent(key, ft -> defaultValue);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static Object getValueAsObject(String key, Object defaultValue) {
		try {
			return (Object) getExtensions().computeIfAbsent(key, ft -> defaultValue);
		} catch (Exception ex) {
			return defaultValue;
		}
	}
}
