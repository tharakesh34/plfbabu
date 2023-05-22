package com.pennanttech.pff.dao.test;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.cibil.CIBILDAO;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.model.cibil.CibilFileInfo;
import com.pennanttech.pff.model.cibil.CibilMemberDetail;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCIBILDAO {

	@Autowired
	private CIBILDAO cIBILDAO;

	Date dt1 = DateUtil.parse("29/03/2021", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCustomerDetails() {
		cIBILDAO.getCustomerDetails(1463);
		cIBILDAO.getCustomerDetails(109);

		cIBILDAO.getCustomer(1463, "RETAIL");
		cIBILDAO.getCustomer(1567, "CORP");

		cIBILDAO.getCustomerDocuments(1463, "RETAIL");
		cIBILDAO.getCustomerDocuments(1567, "CORP");

		cIBILDAO.getCustomerPhoneNumbers(1463, "RETAIL");
		cIBILDAO.getCustomerPhoneNumbers(1567, "CORP");

		cIBILDAO.getCustomerEmails(1301);

		cIBILDAO.getCustomerAddres(1463, "RETAIL");

		cIBILDAO.getCustomerAddres(1463, "RETAIL");

		// cIBILDAO.getExceptions(43);

		cIBILDAO.getLatestExecution();

		cIBILDAO.deleteDetails();

		cIBILDAO.getEventProperties("MANDATES_EXPORT", "SFTP");
		cIBILDAO.getEventProperties("MANDATES_EXPORT", "");

		cIBILDAO.getMemberDetails("RETAIL");
		cIBILDAO.getMemberDetails("");

		cIBILDAO.getMemberDetailsByType("RETAIL", "R");
		cIBILDAO.getMemberDetailsByType("", "");

		cIBILDAO.getFinODDetails(4335, "");

		cIBILDAO.getCollateralDetails(3021, "CORP"); // no data found

		cIBILDAO.getChequeBounceStatus(""); // method changed while moving delta from 5.16

		cIBILDAO.getGuarantorsDetails(3548, true);
		cIBILDAO.getGuarantorsDetails(3710, false);

		cIBILDAO.getotalRecords("RETAIL");

		cIBILDAO.getExternalCustomer(Long.valueOf(1));

		cIBILDAO.getExternalCustomerAddres(Long.valueOf(1));

		cIBILDAO.getExternalCustomerPhoneNumbers(Long.valueOf(1));

		cIBILDAO.getExternalCustomerDocuments(Long.valueOf(1));

		cIBILDAO.getEventProperties("MANDATES_EXPORT");
		cIBILDAO.getEventProperties("");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCustomerAddres() {
		// CustAddrPriority should be int
		cIBILDAO.getCustomerAddres(1567, "CORP");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinanceSummary() {
		// FinId doesnot exist in Cibil_Customer_Loans_View
		cIBILDAO.getFinanceSummary(1463, 5354, "RETAIL");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinanceSummary1() {
		// FinId doesnot exist in Cibil_Customer_Loans_View
		cIBILDAO.getFinanceSummary(1463, "RETAIL");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testLogFileInfoException() {
		cIBILDAO.logFileInfoException(447, 5354, "1500BUS0003280", "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testLogFileInfo() {
		// Issue with SysParamUtil.getAppDate()
		CibilFileInfo cfi = new CibilFileInfo();
		CibilMemberDetail cmd = new CibilMemberDetail();
		cfi.setId(448);
		cfi.setFileName("NB68030221_30072021.txt");
		cmd.setMemberId("NB68030221");
		cmd.setMemberShortName("BAJAJ FIN LTD");
		cmd.setMemberPassword("A6K48");
		cmd.setSegmentType("CORP");
		cfi.setCibilMemberDetail(cmd);
		cIBILDAO.logFileInfo(cfi);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testExtractCustomers() throws Exception {
		// Issue with SysParamUtil.getAppDate()
		cIBILDAO.extractCustomers("RETAIL", "ESFB"); // method changed while moving delta from 5.16
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testExtractCustomers1() throws Exception {
		// Issue with SysParamUtil.getAppDate()s
		cIBILDAO.extractCustomers("CORP", "ESFB"); // method changed while moving delta from 5.16
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateFileStatus() {
		CibilFileInfo cfi = new CibilFileInfo();
		cfi.setId(447);
		cfi.setStatus("S");
		cIBILDAO.updateFileStatus(cfi);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateFileStatus1() {
		CibilFileInfo cfi = new CibilFileInfo();
		cfi.setId(447);
		cfi.setStatus("F");
		cIBILDAO.updateFileStatus(cfi);
	}
}
