package com.pennanttech.extrenal.ucic.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.app.util.MasterDefUtil;
import com.pennant.app.util.MasterDefUtil.DocType;
import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.fileutil.TextFileUtil;
import com.pennanttech.extrenal.ucic.dao.ExtUcicDao;
import com.pennanttech.extrenal.ucic.model.ExtCustAddress;
import com.pennanttech.extrenal.ucic.model.ExtCustDoc;
import com.pennanttech.extrenal.ucic.model.ExtCustEmail;
import com.pennanttech.extrenal.ucic.model.ExtCustPhones;
import com.pennanttech.extrenal.ucic.model.ExtUcicCust;
import com.pennanttech.extrenal.ucic.model.ExtUcicFinDetails;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicRequestData extends TextFileUtil implements InterfaceConstants, ExtUciccomparator {
	private static final Logger logger = LogManager.getLogger(ExtUcicRequestData.class);
	private ExtUcicDao extUcicDao;

	private ApplicationContext applicationContext;
	private DataSource dataSource;

	private static final String MODIFIED_CUST = "M";
	private static final String NEW_CUST = "A";
	private static final String SOURCE_SYSTEM = "PLF";
	private static final String SI_ACCOUNT = "SI";

	public void fetchAndProcessCustomers(Date appDate) {
		logger.debug(Literal.ENTERING);

		applicationContext = ApplicationContextProvider.getApplicationContext();
		dataSource = applicationContext.getBean("dataSource", DataSource.class);

		JdbcCursorItemReader<ExtUcicCust> cursorItemReader = new JdbcCursorItemReader<ExtUcicCust>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setFetchSize(100);
		cursorItemReader.setSql(getFetchCustomersQuery());
		cursorItemReader.setRowMapper(new RowMapper<ExtUcicCust>() {
			@Override
			public ExtUcicCust mapRow(ResultSet rs, int rowNum) throws SQLException {

				ExtUcicCust customer = new ExtUcicCust();
				customer.setCustId(rs.getLong("CUSTID"));
				customer.setCustCif(rs.getString("CUSTCIF"));
				customer.setCustCoreBank(rs.getString("CUSTCOREBANK"));
				customer.setSourceSystem(SOURCE_SYSTEM);
				customer.setSubCategory(StringUtils.stripToEmpty(rs.getString("SUBCATEGORY")));
				customer.setCustShrtName(StringUtils.stripToEmpty(rs.getString("CUSTSHRTNAME")));
				customer.setCustDob(rs.getDate("CUSTDOB"));
				customer.setCustMotherMaiden(StringUtils.stripToEmpty(rs.getString("CUSTMOTHERMAIDEN")));
				customer.setCustCtgCode(StringUtils.stripToEmpty(rs.getString("CUSTCTGCODE")));
				if (customer.getCustCtgCode() != null) {
					if ("RETAIL".equalsIgnoreCase(customer.getCustCtgCode())) {
						customer.setCustomertype("I");
					} else {
						customer.setCustomertype("C");
					}
				}
				if ("I".equals(customer.getCustomertype())) {
					customer.setCustCtgCode("INDIVIDUAL");
				} else {
					customer.setCustCtgCode("CORPORATE");
				}
				return customer;
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		ExtUcicCust customer;

		try {
			while ((customer = cursorItemReader.read()) != null) {

				extUcicDao.setLoanAccNumber(customer, SI_ACCOUNT);
				extUcicDao.setCustEmployerName(customer, 1);

				List<ExtCustEmail> custEmails = extUcicDao.getCustEmails(customer);
				List<ExtCustPhones> custPhones = extUcicDao.getCustPhones(customer);
				List<ExtCustAddress> custAddresses = extUcicDao.getCustAddress(customer);
				List<ExtCustDoc> custDocList = extUcicDao.getCustDocs(customer);

				setEmails(custEmails, customer);
				setPhones(custPhones, customer);
				setAddress(custAddresses, customer);
				setCustDocs(custDocList, customer);

				List<ExtUcicFinDetails> extUcicFinDetails = extUcicDao.getCustFinDetailsByCustId(customer.getCustId());

				List<ExtUcicFinDetails> ucicJointCustomers = extUcicDao
						.getCustomerFinDetailsByCustCif(customer.getCustCif());

				// Check if the loan is having guaranteer account. Get guaranteer account customer by custcif
				List<ExtUcicFinDetails> extUcicGuarantors = extUcicDao
						.getCustomerFinDetailsWithGuarantorCif(customer.getCustCif());

				List<ExtUcicFinDetails> finalList = new ArrayList<ExtUcicFinDetails>();
				finalList.addAll(extUcicFinDetails);
				finalList.addAll(ucicJointCustomers);
				finalList.addAll(extUcicGuarantors);

				// If no loans or joint account or guaranteer details
				if (finalList.isEmpty()) {
					continue;
				}

				// Iterate each loan and set details to customer and process
				for (ExtUcicFinDetails finDetails : finalList) {
					customer.setFinreference(finDetails.getFinreference());
					customer.setFinId(finDetails.getFinId());
					customer.setCloseDate(finDetails.getClosedDate());
					customer.setClosingStatus(finDetails.getClosingStatus());
					// Customer with loan is processed.
					processCustomer(customer, appDate);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cursorItemReader.close();
		logger.debug(Literal.LEAVING);
	}

	private void processCustomer(ExtUcicCust customer, Date appDate) {
		// Check if record already exist with finid
		boolean isExist = extUcicDao.isRecordExist(customer.getFinId());
		if (isExist) {
			// Fetch existing record as object. Compare changes with new object for changes
			ExtUcicCust existingCustomer = extUcicDao.fetchRecord(customer.getFinId());
			boolean isSame = compareCustomerData(customer, existingCustomer);
			if (!isSame) {
				extUcicDao.saveHistory(existingCustomer, customer.getLastMntOn(), appDate);
				extUcicDao.deleteRecord(customer.getFinId());
				customer.setInsertUpdateFlag(MODIFIED_CUST);
				customer.setFileStatus(FILE_NOT_WRITTEN);
				customer.setProgressFlag(UNPROCESSED);
				extUcicDao.insertRecord(customer);
			}
		} else {
			customer.setInsertUpdateFlag(NEW_CUST);
			customer.setFileStatus(FILE_NOT_WRITTEN);
			customer.setProgressFlag(UNPROCESSED);
			extUcicDao.insertRecord(customer);
		}
	}

	private void setCustDocs(List<ExtCustDoc> custDocList, ExtUcicCust customer) {
		if (!custDocList.isEmpty()) {

			for (ExtCustDoc doc : custDocList) {
				if (doc != null && doc.getDocCategory() != null) {
					String docCategory = doc.getDocCategory();
					if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.PAN))) {
						customer.setPan(doc.getDocTitle());
					} else {
						customer.setPan("");
					}
					if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.AADHAAR))) {
						customer.setAadhaar(doc.getDocTitle());
					} else {
						customer.setAadhaar("");
					}
					if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.DRIVING_LICENCE))) {
						customer.setDrivingLicence(doc.getDocTitle());
					} else {
						customer.setDrivingLicence("");
					}
					if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.PASSPORT))) {
						customer.setPassport(doc.getDocTitle());
					} else {
						customer.setPassport("");
					}
					if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.VOTER_ID))) {
						customer.setVoterId(doc.getDocTitle());
					} else {
						customer.setVoterId("");
					}
				}
			}
		} else {
			customer.setPan("");
			customer.setAadhaar("");
			customer.setDrivingLicence("");
			customer.setPassport("");
			customer.setVoterId("");
		}

	}

	private void setAddress(List<ExtCustAddress> custAddresses, ExtUcicCust customer) {
		if (!custAddresses.isEmpty()) {

			if (custAddresses.size() == 1) {
				addEmptyAddress(custAddresses, 4);
				addEmptyAddress(custAddresses, 3);
			}
			if (custAddresses.size() == 2) {
				addEmptyAddress(custAddresses, 3);
			}
		} else {
			addEmptyAddress(custAddresses, 5);
			addEmptyAddress(custAddresses, 4);
			addEmptyAddress(custAddresses, 3);
		}

		Collections.sort(custAddresses, Comparator.comparing(ExtCustAddress::getPriority));

		customer.setAddr1Type(custAddresses.get(0).getAddrType());
		customer.setAddr1Line1(custAddresses.get(0).getAddrLine1());
		customer.setAddr1Line2(custAddresses.get(0).getAddrLine2());
		customer.setAddr1Line3(custAddresses.get(0).getAddrLine3());
		customer.setAddr1Line4(custAddresses.get(0).getAddrLine4());
		customer.setAddr1City(custAddresses.get(0).getAddrCity());
		customer.setAddr1State(custAddresses.get(0).getAddrState());
		customer.setAddr1Pin(custAddresses.get(0).getAddrPin());

		customer.setAddr2Type(custAddresses.get(1).getAddrType());
		customer.setAddr2Line1(custAddresses.get(1).getAddrLine1());
		customer.setAddr2Line2(custAddresses.get(1).getAddrLine2());
		customer.setAddr2Line3(custAddresses.get(1).getAddrLine3());
		customer.setAddr2Line4(custAddresses.get(1).getAddrLine4());
		customer.setAddr2City(custAddresses.get(1).getAddrCity());
		customer.setAddr2State(custAddresses.get(1).getAddrState());
		customer.setAddr2Pin(custAddresses.get(1).getAddrPin());

		customer.setAddr3Type(custAddresses.get(2).getAddrType());
		customer.setAddr3Line1(custAddresses.get(2).getAddrLine1());
		customer.setAddr3Line2(custAddresses.get(2).getAddrLine2());
		customer.setAddr3Line3(custAddresses.get(2).getAddrLine3());
		customer.setAddr3Line4(custAddresses.get(2).getAddrLine4());
		customer.setAddr3City(custAddresses.get(2).getAddrCity());
		customer.setAddr3State(custAddresses.get(2).getAddrState());
		customer.setAddr3Pin(custAddresses.get(2).getAddrPin());
	}

	private void setPhones(List<ExtCustPhones> custPhones, ExtUcicCust customer) {

		HashMap<Integer, List<String>> phoneMap = new HashMap<Integer, List<String>>();
		if (!custPhones.isEmpty()) {

			Collections.sort(custPhones, Comparator.comparing(ExtCustPhones::getPriority));

			for (ExtCustPhones phone : custPhones) {
				if (phoneMap.containsKey(phone.getPriority())) {
					phoneMap.get(phone.getPriority()).add(phone.getMobile());
				} else {
					List<String> phoneNos = new ArrayList<String>();
					phoneNos.add(phone.getMobile());
					phoneMap.put(phone.getPriority(), phoneNos);
				}
			}

			ExtCustPhones mob1 = new ExtCustPhones();
			ExtCustPhones mob2 = new ExtCustPhones();
			ExtCustPhones mob3 = new ExtCustPhones();

			ExtCustPhones land1 = new ExtCustPhones();
			ExtCustPhones land2 = new ExtCustPhones();
			ExtCustPhones land3 = new ExtCustPhones();

			if (phoneMap.containsKey(5)) {
				int size = phoneMap.get(5).size();
				if (size > 0) {
					mob1.setMobile(phoneMap.get(5).get(0));
				} else {
					mob1.setMobile("");
				}
			} else {
				mob1.setMobile("");
			}

			if (phoneMap.containsKey(4)) {
				List<String> dataList = phoneMap.get(4);
				int size = dataList.size();
				if (size > 0) {
					String m2 = "";
					for (int i = 0; i < size; i++) {
						if (dataList.get(i) != null && !"".equals(dataList.get(i))) {
							m2 = dataList.get(i);
							phoneMap.get(4).remove(i);
							return;
						}
					}
					mob2.setMobile(m2);
				} else {
					mob2.setMobile("");
				}
			} else {
				mob2.setMobile("");
			}

			if (phoneMap.containsKey(3)) {
				setPhoneData(phoneMap, phoneMap.get(3), mob3, true);
				setPhoneData(phoneMap, phoneMap.get(3), land1, false);
				setPhoneData(phoneMap, phoneMap.get(3), land2, false);
				setPhoneData(phoneMap, phoneMap.get(3), land3, false);
			} else {
				mob3.setMobile("");
				land1.setLand("");
				land2.setLand("");
				land3.setLand("");
			}

			customer.setMobile1(mob1.getMobile());
			customer.setMobile2(mob2.getMobile());
			customer.setMobile3(mob3.getMobile());
			customer.setLandLine1(land1.getLand());
			customer.setLandLine2(land2.getLand());
			customer.setLandLine3(land3.getLand());
		} else {
			customer.setMobile1("");
			customer.setMobile2("");
			customer.setMobile3("");
			customer.setLandLine1("");
			customer.setLandLine2("");
			customer.setLandLine3("");
		}
	}

	private void setPhoneData(HashMap<Integer, List<String>> phoneMap, List<String> dataList, ExtCustPhones custPhone,
			boolean isMobile) {
		int size = dataList.size();
		if (size > 0) {
			String l1 = "";
			for (int i = 0; i < size; i++) {
				if (dataList.get(i) != null && !"".equals(dataList.get(i))) {
					l1 = dataList.get(i);
					phoneMap.get(3).remove(i);
					return;
				}
			}
			if (isMobile) {
				custPhone.setLand(l1);
			} else {
				custPhone.setLand(l1);
			}

		} else {
			if (isMobile) {
				custPhone.setLand("");
			} else {
				custPhone.setLand("");
			}
		}
	}

	private void setEmails(List<ExtCustEmail> custEmails, ExtUcicCust customer) {

		if (!custEmails.isEmpty()) {

			Collections.sort(custEmails, Comparator.comparing(ExtCustEmail::getPriority));

			if (custEmails.size() == 1) {
				addEmptyEmail(custEmails, 4);
				addEmptyEmail(custEmails, 3);
			}
			if (custEmails.size() == 2) {
				addEmptyEmail(custEmails, 3);
			}

			if (custEmails.size() > 3) {
				if (custEmails.get(2).getEmail() == null || "".equals(custEmails.get(2).getEmail())) {
					for (int j = 3; j < custEmails.size(); j++) {
						ExtCustEmail emails = custEmails.get(j);
						if (emails.getEmail() != null && !"".equals(emails.getEmail())) {
							custEmails.get(2).setEmail(emails.getEmail());
							return;
						}
					}
				}
			}

		} else {
			addEmptyEmail(custEmails, 5);
			addEmptyEmail(custEmails, 4);
			addEmptyEmail(custEmails, 3);
		}
		custEmails = custEmails.subList(0, 3);

		Collections.sort(custEmails, Comparator.comparing(ExtCustEmail::getPriority));

		customer.setEmail1(custEmails.get(0).getEmail());
		customer.setEmail2(custEmails.get(1).getEmail());
		customer.setEmail3(custEmails.get(3).getEmail());

	}

	private void addEmptyEmail(List<ExtCustEmail> custEmails, int priority) {
		ExtCustEmail custEmail = new ExtCustEmail();
		custEmail.setEmail("");
		custEmail.setPriority(priority);
		custEmails.add(custEmail);
	}

	private void addEmptyAddress(List<ExtCustAddress> custAddresses, int priority) {
		ExtCustAddress address = new ExtCustAddress();
		address.setAddrType("");
		address.setAddrLine1("");
		address.setAddrLine2("");
		address.setAddrLine3("");
		address.setAddrLine4("");
		address.setAddrCity("");
		address.setAddrState("");
		address.setAddrPin("");
		address.setPriority(priority);
		custAddresses.add(address);
	}

	private String getFetchCustomersQuery() {
		String customersQuery = "SELECT CUSTID,CUSTCOREBANK,CUSTDOB,CUSTMOTHERMAIDEN,"
				+ " CUSTSHRTNAME,SUBCATEGORY,CUSTCTGCODE,CUSTCIF FROM CUSTOMERS";
		return customersQuery;
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

}
