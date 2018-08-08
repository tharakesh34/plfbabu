package com.pennanttech.explore;

import java.io.IOException;
import java.text.ParseException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.util.DateUtil;

public class DateWithJackson {
	@Test
	public void serializedToDefaultTimestamp() {
		Person person = new Person(1, "Sai");
		try {
			person.setDob(DateUtil.parse("1975-07-28 01:20:00", "yyyy-MM-dd hh:mm:ss"));
		} catch (ParseException e) {
			throw new AppException("Given string cannot be parsed.", e);
		}

		String value = null;
		try {
			value = (new ObjectMapper()).writeValueAsString(person.getDob());
		} catch (IOException e) {
			throw new AppException("Unable to serialize Java value as a String.", e);
		}

		Assert.assertEquals(person.getDob().getTime(), Long.parseLong(value));
	}

	@Test
	public void serializedXmlDateToDefaultTimestamp() {
		Person person = new Person(1, "Sai");
		try {
			person.setDob(DateUtil.parse("1975-07-28T01:20:00", "yyyy-MM-dd'T'hh:mm:ss"));
		} catch (ParseException e) {
			throw new AppException("Given string cannot be parsed.", e);
		}

		String value = null;
		try {
			value = (new ObjectMapper()).writeValueAsString(person.getDob());
		} catch (IOException e) {
			throw new AppException("Unable to serialize Java value as a String.", e);
		}

		Assert.assertEquals(person.getDob().getTime(), Long.parseLong(value));
	}
}
