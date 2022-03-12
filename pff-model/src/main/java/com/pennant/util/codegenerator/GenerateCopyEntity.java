package com.pennant.util.codegenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;

public class GenerateCopyEntity {
	private static final Logger logger = LogManager.getLogger(GenerateCopyEntity.class);

	private static Object object = new AuditDetail();

	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		StringBuilder builder = new StringBuilder();

		String simpleName = object.getClass().getSimpleName();
		builder.append("public ").append(simpleName).append(" copyEntity() {\n");
		builder.append(simpleName).append(" entity = new ").append(simpleName).append("();");

		for (Field field : object.getClass().getDeclaredFields()) {
			String fieldName = field.getName();

			if ("serialVersionUID".equals(fieldName)) {
				continue;
			}

			if (fieldName.contains("audit") && fieldName.contains("Map")) {
				System.out.println("checkaudit");
			}

			if (builder.length() > 0) {
				builder.append("\n");
			}

			if (field.getType() == List.class || field.getType().getSuperclass() == List.class) {
				String FiledVar = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
				String getMethod = "get" + FiledVar;
				Method methodName = object.getClass().getDeclaredMethod(getMethod);

				Object rv = methodName.invoke(object, args);

				field.setAccessible(true);
				ParameterizedType type = (ParameterizedType) field.getGenericType();
				Class<?> key = (Class<?>) type.getActualTypeArguments()[0];

				if (rv == null) {
					builder.append("if(").append(fieldName).append("!=null)").append("{");
					builder.append("\nentity.set").append(FiledVar).append("(").append("new ArrayList<")
							.append(key.getSimpleName()).append(">());\n");
				}
				builder.append("this.").append(fieldName).append(".stream().forEach(e->entity.get");
				builder.append(fieldName.substring(0, 1).toUpperCase())
						.append(fieldName.substring(1, fieldName.length()));
				builder.append("().add(");
				builder.append(checkValueType(key, "e"));
				builder.append("));");
				if (rv == null) {
					builder.append("\n}");
				}
				continue;
			}

			if (field.getType() == Map.class || field.getType().getSuperclass() == AbstractMap.class
					|| field.getType() == HashMap.class) {

				String FiledVar = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
				String getMethod = "get" + FiledVar;

				field.setAccessible(true);
				ParameterizedType type = (ParameterizedType) field.getGenericType();
				try {
					Class<?> key = (Class<?>) type.getActualTypeArguments()[0];
					Class<?> value = (Class<?>) type.getActualTypeArguments()[1];
					Method methodName = object.getClass().getDeclaredMethod(getMethod);
					Object rv = methodName.invoke(object, args);

					if (rv == null) {
						builder.append("if(").append(fieldName).append("!=null)").append("{");
						builder.append("\nentity.set").append(FiledVar).append("(").append("new HashMap<")
								.append(key.getSimpleName()).append(",").append(value.getSimpleName())
								.append(">());\n");
					}

					builder.append("this.").append(fieldName).append(".entrySet().stream().forEach(e->entity.get");
					builder.append(fieldName.substring(0, 1).toUpperCase())
							.append(fieldName.substring(1, fieldName.length()));
					builder.append("().put(").append(checkValueType(key, "e.getKey()")).append(",")
							.append(checkValueType(value, "e.getValue()")).append("));");
					if (rv == null) {
						builder.append("\n}");
					}
				} catch (ClassCastException e) {
					System.out.println(fieldName);
					logger.error(Literal.EXCEPTION, e);
					builder.append("\n");
				}
				continue;
			}

			builder.append("entity.").append("set").append(fieldName.substring(0, 1).toUpperCase())
					.append(fieldName.substring(1, fieldName.length()));

			if (!field.getType().isPrimitive() && field.getType() != Date.class && field.getType() != String.class
					&& field.getType().getSuperclass() != Number.class
					&& field.getType().getSuperclass() != Number.class && field.getType() != LoggedInUser.class
					&& field.getType() != ErrorDetail.class) {
				builder.append("(this.").append(fieldName).append(" == null ? null : this.");
				builder.append(fieldName).append(".copyEntity());");
				continue;
			}

			builder.append("(this.").append(fieldName).append(checkType(field.getType())).append(");");

		}

		builder.append("\nreturn entity;\n}");

		System.out.println(builder.toString());
	}

	private static String checkType(Class<?> key) {
		if (!key.isPrimitive() && key != Date.class && key != String.class && key != Boolean.class
				&& key.getSuperclass() != Number.class && key != LoggedInUser.class && key != ErrorDetail.class
				&& key != EventProperties.class) {
			return ".copyEntity()";
		}
		return "";
	}

	private static String checkValueType(Class<?> key, String value) {
		if (!key.isPrimitive() && key != Date.class && key != String.class && key != Boolean.class
				&& key.getSuperclass() != Number.class && key != LoggedInUser.class && key != ErrorDetail.class
				&& key != EventProperties.class) {
			return value + " == null ? null : " + value + ".copyEntity()";
		}

		return value;
	}

	private static Class<?> ifList(Type listtype) {

		return null;

	}

}
