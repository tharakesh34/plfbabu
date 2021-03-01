package com.pennant.util.codegenerator;

import java.lang.reflect.Field;

import com.pennant.backend.model.finance.FinReceiptData;

public class GenerateCopyEntity {
	private static Object object = new FinReceiptData();

	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		StringBuilder builder = new StringBuilder();

		String simpleName = object.getClass().getSimpleName();
		builder.append("public ").append(simpleName).append(" copyEntity() {\n");
		builder.append(simpleName).append(" entity = new ").append(simpleName).append("();");

		for (Field field : object.getClass().getDeclaredFields()) {
			String fieldName = field.getName();

			if ("serialVersionUID".equals(fieldName)) {
				continue;
			}

			if (builder.length() > 0) {
				builder.append("\n");
			}

			builder.append("entity.").append("set").append(fieldName.substring(0, 1).toUpperCase())
					.append(fieldName.substring(1, fieldName.length()));
			builder.append("(this.").append(fieldName).append(");");

		}

		builder.append("\nreturn entity;\n}");

		System.out.println(builder.toString());
	}

}
