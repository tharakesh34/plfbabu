package com.pennanttech.pff.core.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennant.backend.model.customermasters.Customer;

public class CustomerUtilTest {

	@Test
	public void testCustomerFullName1() {
		Customer customer = new Customer();

		customer.setCustSalutationCode("Mr.");
		customer.setCustFName("Venkat");
		customer.setCustMName("Prasad");
		customer.setCustLName("Rao");

		String fullName = CustomerUtil.getCustomerFullName(customer);

		Assert.assertEquals(fullName, "Mr.Venkat Prasad Rao");
	}

	@Test
	public void testCustomerFullName2() {
		Customer customer = new Customer();

		customer.setCustSalutationCode("");
		customer.setCustFName("Venkat");
		customer.setCustMName("Prasad");
		customer.setCustLName("Rao");

		String fullName = CustomerUtil.getCustomerFullName(customer);

		Assert.assertEquals(fullName, "Venkat Prasad Rao");
	}

	@Test
	public void testCustomerFullName3() {
		Customer customer = new Customer();

		customer.setCustSalutationCode("Mr.");
		customer.setCustFName("");
		customer.setCustMName("Prasad");
		customer.setCustLName("Rao");

		String fullName = CustomerUtil.getCustomerFullName(customer);

		Assert.assertEquals(fullName, "Mr.Prasad Rao");
	}

	@Test
	public void testCustomerFullName4() {
		Customer customer = new Customer();

		customer.setCustSalutationCode("Mr.");
		customer.setCustFName("Venkat");
		customer.setCustMName("");
		customer.setCustLName("Rao");

		String fullName = CustomerUtil.getCustomerFullName(customer);

		Assert.assertEquals(fullName, "Mr.Venkat Rao");
	}

	@Test
	public void testCustomerFullName5() {
		Customer customer = new Customer();

		customer.setCustSalutationCode("Mr.");
		customer.setCustFName("Venkat");
		customer.setCustMName("Prasad");
		customer.setCustLName("");

		String fullName = CustomerUtil.getCustomerFullName(customer);

		Assert.assertEquals(fullName, "Mr.Venkat Prasad");
	}
}
