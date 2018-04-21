package com.pennant.backend.util;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennant.backend.model.Property;

public class PennantStaticListUtilTest {
	@Test
	private void testGetPropertyValue() {
		List<Property> properties = new ArrayList<>();

		properties.add(new Property(1, "Sai"));
		properties.add(new Property(2, "Krishna"));
		properties.add(new Property(3, "P"));

		Assert.assertEquals(PennantStaticListUtil.getPropertyValue(properties, 2), "Krishna");
	}

	@Test
	private void testGetPropertyValueOfString() {
		List<Property> properties = new ArrayList<>();

		properties.add(new Property("040", "Sai"));
		properties.add(new Property("041", "Krishna"));
		properties.add(new Property("042", "P"));

		Assert.assertEquals(PennantStaticListUtil.getPropertyValue(properties, "042"), "P");
	}
}
