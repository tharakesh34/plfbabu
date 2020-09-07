package com.pennanttech.pennapps.pff.external.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.pff.external.DisbursementRequest;

public class TestDisbursement {

	private DisbursementRequest disbursementRequest;

	@BeforeTest
	public void start() {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			disbursementRequest = context.getBean(DisbursementRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(enabled = false)
	public void process() {
		try {

			List<FinAdvancePayments> list = new ArrayList<>();

			FinAdvancePayments fa = null;

			fa = new FinAdvancePayments();
			fa.setFinReference("6R8PBD00003166");
			fa.setPaymentId(1363);
			fa.setPaymentType("IMPS");
			fa.setPartnerbankCode("SBI127");
			list.add(fa);

			fa = new FinAdvancePayments();
			fa.setFinReference("6R8PBD00003166");
			fa.setPaymentId(1364);
			fa.setPaymentType("IMPS");
			fa.setPartnerbankCode("HDFC");
			list.add(fa);

			fa = new FinAdvancePayments();
			fa.setFinReference("6R8PBD00003166");
			fa.setPaymentId(1365);
			fa.setPaymentType("NEFT");
			fa.setPartnerbankCode("SBI127");
			list.add(fa);

			fa = new FinAdvancePayments();
			fa.setFinReference("6R8PBD00003166");
			fa.setPaymentId(1366);
			fa.setPaymentType("RTGS");
			fa.setPartnerbankCode("HDFC");
			list.add(fa);

			fa = new FinAdvancePayments();
			fa.setFinReference("6R8PBD00003166");
			fa.setPaymentId(1367);
			fa.setPaymentType("CHEQUE");
			fa.setPartnerbankCode("SBI127");
			list.add(fa);

			fa = new FinAdvancePayments();
			fa.setFinReference("6R8PBD00003166");
			fa.setPaymentId(1368);
			fa.setPaymentType("NEFT");
			fa.setPartnerbankCode("HDFC");
			list.add(fa);

			fa = new FinAdvancePayments();
			fa.setFinReference("6R8PBD00003166");
			fa.setPaymentId(1369);
			fa.setPaymentType("RTGS");
			fa.setPartnerbankCode("SBI127");
			list.add(fa);

			fa = new FinAdvancePayments();
			fa.setFinReference("6R8PBD00003166");
			fa.setPaymentId(1370);
			fa.setPaymentType("DD");
			fa.setPartnerbankCode("SBI127");
			list.add(fa);

			fa = new FinAdvancePayments();
			fa.setFinReference("6R8PBD00003166");
			fa.setPaymentId(1371);
			fa.setPaymentType("DD");
			fa.setPartnerbankCode("HDFC");
			list.add(fa);

			fa = new FinAdvancePayments();
			fa.setFinReference("6R8PBD00003166");
			fa.setPaymentId(1372);
			fa.setPaymentType("CHEQUE");
			fa.setPartnerbankCode("HDFC");
			list.add(fa);

			disbursementRequest.sendReqest("PBD", list, Long.valueOf(1000));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
