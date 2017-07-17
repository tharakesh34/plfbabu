package com.pennanttech.pff.core.util;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.util.ClassUtil;

public class ClassUtilTest {
	@Test
	public void getFieldsOfSuperclass() {
		String[] result = { "p1", "p2" };
		Field[] fields = ClassUtil.getFields(new ClassP());

		Assert.assertEquals(fields.length, result.length, "Field Count: ");

		boolean matched;

		for (Field field : fields) {
			matched = false;

			for (String r : Arrays.asList(result)) {
				if (r.equals(field.getName())) {
					matched = true;
				}
			}

			Assert.assertTrue(matched, field.getName() + " not expected!");
		}
	}

	@Test
	public void getFieldsOfExtendedClass() {
		String[] result = { "a1", "a2" };
		Field[] fields = ClassUtil.getFields(new ClassA());

		Assert.assertEquals(fields.length, result.length, "Field Count: ");

		boolean matched;

		for (Field field : fields) {
			matched = false;

			for (String r : Arrays.asList(result)) {
				if (r.equals(field.getName())) {
					matched = true;
				}
			}

			Assert.assertTrue(matched, field.getName() + " not expected!");
		}
	}

	@Test
	public void getAllFieldsOfSuperclass() {
		String[] result = { "p1", "p2" };
		Field[] fields = ClassUtil.getAllFields(new ClassP());

		Assert.assertEquals(fields.length, result.length, "Field Count: ");

		boolean matched;

		for (Field field : fields) {
			matched = false;

			for (String r : Arrays.asList(result)) {
				if (r.equals(field.getName())) {
					matched = true;
				}
			}

			Assert.assertTrue(matched, field.getName() + " not expected!");
		}
	}

	@Test
	public void getAllFieldsOfExtendedClass() {
		String[] result = { "p1", "p2", "a1", "a2" };
		Field[] fields = ClassUtil.getAllFields(new ClassA());

		Assert.assertEquals(fields.length, result.length, "Field Count: ");

		boolean matched;

		for (Field field : fields) {
			matched = false;

			for (String r : Arrays.asList(result)) {
				if (r.equals(field.getName())) {
					matched = true;
				}
			}

			Assert.assertTrue(matched, field.getName() + " not expected!");
		}
	}

	@Test
	public void pFields() {
		Field[] fields = new Field[] {};

		for (int i = 0; i < 100000; i++) {
			fields = ClassUtil.getFields(new ClassA());
		}

		Assert.assertEquals(fields.length, 2, "Field Count: ");
	}

	@Test
	public void pAllFields() {
		// 5 to 10 times slower, but improves readability and maintainability.
		Field[] fields = new Field[] {};

		for (int i = 0; i < 100000; i++) {
			fields = ClassUtil.getAllFields(new ClassA());
		}

		Assert.assertEquals(fields.length, 4, "Field Count: ");
	}

	@Test
	public void isMethodExists() {
		Assert.assertEquals(true, ClassUtil.isMethodExists(new ClassA(), "getA1"));
		Assert.assertEquals(true, ClassUtil.isMethodExists(new ClassA(), "getA2"));
		Assert.assertEquals(false, ClassUtil.isMethodExists(new ClassA(), "isSai"));
	}
}
