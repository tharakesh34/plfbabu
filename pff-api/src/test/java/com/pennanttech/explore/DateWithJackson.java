package com.pennanttech.explore;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.testng.annotations.Test;

public class DateWithJackson {
	@Test
	public void serializedToDefaultTimestamp() throws ParseException, JsonGenerationException, JsonMappingException,
			IOException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		Person person = new Person(1, "Sai", format.parse("1975-07-28 01:20:00"));

		ObjectMapper mapper = new ObjectMapper();
		String value = mapper.writeValueAsString(person.getDob());

		Assert.assertEquals(person.getDob().getTime(), Long.parseLong(value));
	}

	@Test
	public void serializedXmlDateToDefaultTimestamp() throws ParseException, JsonGenerationException,
			JsonMappingException, IOException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

		Person person = new Person(1, "Sai", format.parse("1975-07-28T01:20:00"));

		ObjectMapper mapper = new ObjectMapper();
		String value = mapper.writeValueAsString(person.getDob());

		Assert.assertEquals(person.getDob().getTime(), Long.parseLong(value));
	}
}
