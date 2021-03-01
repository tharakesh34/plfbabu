package com.pennant.backend.dao.finance.impl;

import java.lang.reflect.Field;

import com.pennant.backend.model.rulefactory.AEAmountCodes;

public class GenerateFieldsMap {
	private static Object object = new AEAmountCodes();
	private static String preficx = "ae_";

	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		StringBuilder builder = new StringBuilder();
		for (Field field : object.getClass().getDeclaredFields()) {
			String fieldName = field.getName();

			if ("serialVersionUID".equals(fieldName)) {
				continue;
			}

			if (builder.length() > 0) {
				builder.append("\n");
			}

			builder.append("map.put(\"" + preficx + fieldName + "\", this." + fieldName + ");");
		}

		System.out.println(builder.toString());
	}

}
