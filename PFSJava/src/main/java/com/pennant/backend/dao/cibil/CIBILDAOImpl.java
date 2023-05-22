package com.pennant.backend.dao.cibil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.model.cibil.CibilFileInfo;
import com.pennanttech.pff.model.cibil.CibilMemberDetail;

public class CIBILDAOImpl extends BasicDao<Object> implements CIBILDAO {
	private static Logger logger = LogManager.getLogger(CIBILDAOImpl.class);

	@Override
	public CustomerDetails getCustomerDetails(long customerId) {
		logger.trace(Literal.ENTERING);

		try {
			CustomerDetails customer = new CustomerDetails();
			customer.setCustomer(getCustomer(customerId, PennantConstants.PFF_CUSTCTG_INDIV));
			customer.setCustomerDocumentsList(getCustomerDocuments(customerId, PennantConstants.PFF_CUSTCTG_INDIV));
			customer.setCustomerPhoneNumList(getCustomerPhoneNumbers(customerId, PennantConstants.PFF_CUSTCTG_INDIV));
			customer.setCustomerEMailList(getCustomerEmails(customerId));
			customer.setAddressList(getCustomerAddres(customerId, PennantConstants.PFF_CUSTCTG_INDIV));

			return customer;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Customer getCustomer(long customerId, String bureauType) {
		StringBuilder sql = new StringBuilder();
		Object[] obj = new Object[] {};

		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
			sql.append("Select");
			sql.append(" CustShrtName, CustFName, CustMName, CustLName, CustCIF");
			sql.append(", CustSalutationCode, CustDOB, CustGenderCode, CustCRCPR");
			sql.append(" From Customers Where CustID = ?");

			obj = new Object[] { customerId };
		} else {
			sql.append("Select distinct c.CustID");
			sql.append(", c.CustDftBranch, c.CustCtgCode, CustCIF");
			sql.append(", c.CustFName, c.CustMName, c.CustLName, c.CustShrtName");
			sql.append(", c.CustTradeLicenceNum, c.CustDOB, CustCOB, CustGenderCode");
			sql.append(", c.CustCRCPR, c.CustSalutationCode");
			sql.append(", lcm.Code LegalConstitution, bcm.Code BusinessCategory, cc.CustCtgType LovDescCustCtgType");
			sql.append(" From Customers c");
			sql.append(" Inner Join BmtCustCategories cc on cc.CustCtgCode = c.CustCtgCode");
			sql.append(" Left Join Cibil_Legal_Const_Mapping lcm on lcm.Cust_Type_Code = c.CustTypeCode");
			sql.append(" and lcm.Segment_Type = ?");
			sql.append(" Left Join Cibil_Legal_Constitution lc on lc.Code = lcm.Code");
			sql.append(" and lc.Segment_Type = lcm.Segment_Type");
			sql.append(" Left Join Cibil_Business_Catgry_Mapping bcm on bcm.Category = c.CustCtgCode");
			sql.append(" and bcm.Segment_Type = ?");
			sql.append(" Left Join Cibil_Business_Category bc on bc.Code = bcm.Code");
			sql.append(" and bc.Segment_Type = bcm.Segment_Type");
			sql.append(" Left Join Cibil_Industry_Type_Mapping bit on bit.Industry = c.CustIndustry");
			sql.append(" where c.Custid = ?");

			obj = new Object[] { "CORP", "CORP", customerId };
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			Customer customer = new Customer();

			if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
				customer.setCustShrtName(rs.getString("CustShrtName"));
				customer.setCustFName(rs.getString("CustFName"));
				customer.setCustMName(rs.getString("CustMName"));
				customer.setCustLName(rs.getString("CustLName"));
				customer.setCustSalutationCode(rs.getString("CustSalutationCode"));
				customer.setCustDOB(rs.getDate("CustDOB"));
				customer.setCustGenderCode(rs.getString("CustGenderCode"));
				customer.setCustCRCPR(rs.getString("CustCRCPR"));
				customer.setCustCIF(rs.getString("CustCIF"));
			} else {
				customer.setCustID(rs.getLong("CustID"));
				customer.setCustDftBranch(rs.getString("CustDftBranch"));
				customer.setCustCtgCode(rs.getString("CustCtgCode"));
				customer.setCustCIF(rs.getString("CustCIF"));
				customer.setCustFName(rs.getString("CustFName"));
				customer.setCustMName(rs.getString("CustMName"));
				customer.setCustLName(rs.getString("CustLName"));
				customer.setCustShrtName(rs.getString("CustShrtName"));
				customer.setCustTradeLicenceNum(rs.getString("CustTradeLicenceNum"));
				customer.setCustDOB(rs.getDate("CustDOB"));
				customer.setCustCOB(rs.getString("CustCOB"));
				customer.setCustGenderCode(rs.getString("CustGenderCode"));
				customer.setCustCRCPR(rs.getString("CustCRCPR"));
				customer.setCustSalutationCode(rs.getString("CustSalutationCode"));
				customer.setLegalconstitution(rs.getString("LegalConstitution"));
				customer.setBusinesscategory(rs.getString("BusinessCategory"));
				customer.setLovDescCustCtgType(rs.getString("LovDescCustCtgType"));
			}

			return customer;
		}, obj);

	}

	@Override
	public List<CustomerDocument> getCustomerDocuments(long customerId, String bureauType) {
		StringBuilder sql = new StringBuilder();

		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
			sql.append("Select");
			sql.append(" dt.Code CustDocCategory, CustDocTitle, CustDocIssuedOn, CustDocExpDate");
			sql.append(" From CustomerDocuments doc");
			sql.append(" Inner Join Cibil_Document_Types_Mapping dm on dm.DocTypeCode = doc.CustDocCategory");
			sql.append(" Inner Join Cibil_Document_Types dt on dt.Code = dm.Code");
		} else {
			sql.append("Select CustDocCategory, CustDocTitle, CustDocIssuedOn, CustDocExpDate");
			sql.append(" From CustomerDocuments doc");
		}
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CustomerDocument cd = new CustomerDocument();

			cd.setCustDocCategory(rs.getString("CustDocCategory"));
			cd.setCustDocTitle(rs.getString("CustDocTitle"));
			cd.setCustDocIssuedOn(rs.getDate("CustDocIssuedOn"));
			cd.setCustDocExpDate(rs.getDate("CustDocExpDate"));

			return cd;
		}, customerId);

	}

	@Override
	public List<CustomerPhoneNumber> getCustomerPhoneNumbers(long customerId, String bureauType) {
		StringBuilder sql = new StringBuilder();

		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
			sql.append("Select");
			sql.append(" cpt.Code PhoneTypeCode, cp.PhoneNumber, PhoneAreaCode, PhoneTypePriority");
			sql.append(" From CustomerPhoneNumbers cp");
			sql.append(" Left Join Cibil_Phone_Types_Mapping pm on pm.PhoneTypeCode = cp.PhoneTypeCode");
			sql.append(" Left Join Cibil_Phone_Types cpt on cpt.Code = pm.Code");
		} else {
			sql.append("Select");
			sql.append(" PhoneTypeCode, cp.PhoneNumber, PhoneAreaCode, PhoneTypePriority");
			sql.append(" From CustomerPhoneNumbers cp");
		}
		sql.append(" where PhoneCustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CustomerPhoneNumber cpn = new CustomerPhoneNumber();

			cpn.setPhoneTypeCode(rs.getString("PhoneTypeCode"));
			cpn.setPhoneNumber(rs.getString("PhoneNumber"));
			cpn.setPhoneAreaCode(rs.getString("PhoneAreaCode"));
			cpn.setPhoneTypePriority(rs.getInt("PhoneTypePriority"));

			return cpn;
		}, customerId);
	}

	@Override
	public List<CustomerEMail> getCustomerEmails(long customerId) {
		String sql = "Select CustEMail from CustomerEmails where CustID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, (rs, rowNum) -> {
			CustomerEMail ce = new CustomerEMail();
			ce.setCustEMail(rs.getString("CustEMail"));

			return ce;
		}, customerId);
	}

	@Override
	public List<CustomerAddres> getCustomerAddres(long customerId, String segmentType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" cat.code CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet");
		sql.append(", CustDistrict, pvc.PcCityName, CustAddrLine1, CustAddrLine2, CustAddrPriority");
		sql.append(", sm.code CustAddrProvince, sm.Description, CustAddrZIP, CustAddrCountry");
		sql.append(" From CustomerAddresses ca");
		sql.append(" Left Join Cibil_Address_Types_Mapping am on am.Address_Type = ca.CustAddrType");
		sql.append(" and am.Segment_Type = ?");
		sql.append(" Left Join Cibil_Address_Types cat on cat.Code = am.Code and cat.Segment_type = am.Segment_type");
		sql.append(" Left Join Cibil_States_Mapping sm on sm.CpProvince = ca.CustAddrProvince");
		sql.append(" and sm.Segment_Type  = am.Segment_Type");
		sql.append(" Left Join RMTProvinceVsCity pvc on pvc.PcCity = ca.CustAddrCity");
		sql.append(" Where CustID = ?");

		Object[] obj = new Object[] { segmentType, customerId };

		if (!PennantConstants.PFF_CUSTCTG_INDIV.equals(segmentType)) {
			sql.append(" and CustAddrPriority = ?");

			obj = new Object[] { segmentType, customerId, Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) };
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CustomerAddres ca = new CustomerAddres();
			ca.setCustAddrType(rs.getString("CustAddrType"));
			ca.setCustAddrHNbr(rs.getString("CustAddrHNbr"));
			ca.setCustFlatNbr(rs.getString("CustFlatNbr"));
			ca.setCustAddrStreet(rs.getString("CustAddrStreet"));
			ca.setCustDistrict(rs.getString("CustDistrict"));
			ca.setCustAddrCity(rs.getString("PcCityName"));
			ca.setCustAddrLine1(rs.getString("CustAddrLine1"));
			ca.setCustAddrLine2(rs.getString("CustAddrLine2"));
			ca.setCustAddrPriority(rs.getInt("CustAddrPriority"));
			ca.setCustAddrProvince(rs.getString("CustAddrProvince"));
			// customerAddres.setLovDescCustAddrProvinceName(rs.getString("Description"));
			ca.setCustAddrZIP(rs.getString("CustAddrZIP"));
			ca.setCustAddrCountry(rs.getString("CustAddrCountry"));

			return ca;
		}, obj);
	}

	@Override
	public FinanceEnquiry getFinanceSummary(long customerId, long finID, String segmentType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, CustId, Fintype, FinReference, FinStartDate, FinApprovedDate,");
		sql.append(" LatestRpyDate, RepayFrq, FinAssetValue, Instalment_Paid,");
		sql.append(" CurOdDays, MaturityDate, ClosingStatus, Ownership, NumberofTerms");
		sql.append(" From Cibil_Customer_Loans_View");
		sql.append(" Where FinID = ? and CustID = ? and Segment_Type = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceEnquiry finEnqy = new FinanceEnquiry();

				finEnqy.setFinID(rs.getLong("FinID"));
				finEnqy.setCustID(rs.getLong("CustId"));
				finEnqy.setFinType(rs.getString("FinType"));
				finEnqy.setFinReference(rs.getString("FinReference"));
				finEnqy.setFinStartDate(rs.getDate("FinStartDate"));
				finEnqy.setFinApprovedDate(rs.getDate("FinApprovedDate"));
				finEnqy.setLatestRpyDate(rs.getDate("LatestRpyDate"));
				finEnqy.setRepayFrq(rs.getString("RepayFrq"));
				finEnqy.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
				finEnqy.setInstalmentPaid(rs.getBigDecimal("Instalment_Paid"));
				finEnqy.setCurODDays(rs.getInt("CurOdDays"));
				finEnqy.setMaturityDate(rs.getDate("MaturityDate"));
				finEnqy.setClosingStatus(rs.getString("ClosingStatus"));
				finEnqy.setOwnership(rs.getString("OwnerShip"));
				finEnqy.setNumberOfTerms(rs.getInt("NumberOfTerms"));

				return finEnqy;
			}, finID, customerId, segmentType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinanceEnquiry> getFinanceSummary(long customerId, String segmentType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" cs.FinID, cs.CustId, Fintype, cs.FinReference, FinStartDate, FinApprovedDate");
		sql.append(", cs.LatestRpyDate,RepayFrq, FinAssetValue, Instalment_Paid");
		sql.append(", CurOdDays, MaturityDate, ClosingStatus, cs.Ownership, NumberofTerms");
		sql.append(" from Cibil_Customer_Loans_View cs");
		sql.append(" Inner Join Cibil_Customer_Extract cce on cce.FinID = cs.FinID");
		sql.append(" and cs.CustId = cce.CustId");
		sql.append(" where cs.Custid = ? and cs.Segment_Type = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinanceEnquiry finEnqy = new FinanceEnquiry();

			finEnqy.setFinID(rs.getLong("FinID"));
			finEnqy.setCustID(rs.getLong("CustId"));
			finEnqy.setFinType(rs.getString("FinType"));
			finEnqy.setFinReference(rs.getString("FinReference"));
			finEnqy.setFinStartDate(rs.getDate("FinStartDate"));
			finEnqy.setFinApprovedDate(rs.getDate("FinApprovedDate"));
			finEnqy.setLatestRpyDate(rs.getDate("LatestRpyDate"));
			finEnqy.setRepayFrq(rs.getString("RepayFrq"));
			finEnqy.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			finEnqy.setInstalmentPaid(rs.getBigDecimal("Instalment_Paid"));
			finEnqy.setCurODDays(rs.getInt("CurOdDays"));
			finEnqy.setMaturityDate(rs.getDate("MaturityDate"));
			finEnqy.setClosingStatus(rs.getString("ClosingStatus"));
			finEnqy.setOwnership(rs.getString("OwnerShip"));
			finEnqy.setNumberOfTerms(rs.getInt("NumberOfTerms"));

			return finEnqy;
		}, customerId, segmentType);
	}

	@Override
	public void logFileInfoException(long id, long finID, String finReference, String reason) {
		StringBuilder sql = new StringBuilder("Insert Into CIBIL_FILE_INFO_LOG");
		sql.append(" (ID, FinID, FinReference, Reason, Status)");
		sql.append(" Values(?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, id);
				ps.setLong(index++, finID);
				ps.setString(index++, finReference);
				ps.setString(index++, reason);
				ps.setString(index, "F");
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public DataEngineStatus getLatestExecution() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, Total_Records, Processed_Records, Success_Records, Failed_Records");
		sql.append(", Remarks, Start_Time, End_Time");
		sql.append(" From Cibil_File_Info");
		sql.append(" Where ID = (Select MAX(Id) from Cibil_File_Info)");

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				DataEngineStatus ds = new DataEngineStatus();

				ds.setId(rs.getInt("ID"));
				ds.setTotalRecords(rs.getInt("Total_Records"));
				ds.setProcessedRecords(rs.getInt("Processed_Records"));
				ds.setSuccessRecords(rs.getInt("Success_Records"));
				ds.setFailedRecords(rs.getInt("Failed_Records"));
				ds.setRemarks(rs.getString("Remarks"));
				ds.setStartTime(rs.getDate("Start_Time"));
				ds.setEndTime(rs.getDate("End_Time"));
				ds.setDataEngineLogList(getExceptions(ds.getId()));

				return ds;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return new DataEngineStatus();
		}
	}

	public List<DataEngineLog> getExceptions(long id) {
		String sql = "Select FinID, FinReference, Reason, Status From Cibil_File_Info_Log where ID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, (rs, rowNum) -> {
			DataEngineLog dataEngLog = new DataEngineLog();
			// dataEngLog.setId(rs.getLong("FinID"));
			dataEngLog.setKeyId(rs.getString("FinReference"));
			dataEngLog.setReason(rs.getString("Reason"));
			dataEngLog.setStatus(rs.getString("Status"));
			return dataEngLog;
		}, id);
	}

	@Override
	public void deleteDetails() {
		jdbcOperations.update("Truncate Table Cibil_Customer_Extract");
	}

	@Override
	public EventProperties getEventProperties(String configName, String eventType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" dep.Storage_Type, dep.Region_Name, dep.Bucket_Name, dep.Access_Key, dep.Secret_Key");
		sql.append(", dep.Prefix, dep.Sse_Algorithm, dep.Host_Name, dep.Port, dep.Private_Key");
		sql.append(" From Data_Engine_Event_Properties dep");
		sql.append(" Inner Join Data_Engine_Config dc on dc.ID = dep.Config_ID");
		sql.append(" Where dc.Name = ? and dep.Storage_Type = ?");

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				EventProperties ep = new EventProperties();

				ep.setStorageType(rs.getString("Storage_Type"));
				ep.setRegionName(rs.getString("Region_Name"));
				ep.setBucketName(rs.getString("Bucket_Name"));
				ep.setAccessKey(rs.getString("Access_Key"));
				ep.setSecretKey(rs.getString("Secret_Key"));
				ep.setPrefix(rs.getString("Prefix"));
				ep.setSseAlgorithm(rs.getString("Sse_Algorithm"));
				ep.setHostName(rs.getString("Host_Name"));
				ep.setPort(rs.getString("Port"));
				ep.setPrivateKey(rs.getString("Private_Key"));

				return ep;
			}, configName, eventType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Configuration details not available for " + configName);
			return null;
		}
	}

	@Override
	public CibilMemberDetail getMemberDetails(String bureauType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Segment_Type, Member_Code, Member_ID, Previous_Member_ID");
		sql.append(", Member_Short_Name, Member_Password, File_Path, File_Formate");
		sql.append(" From Cibil_Member_Details");
		sql.append(" Where Segment_Type = ?");

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CibilMemberDetail cmd = new CibilMemberDetail();

				cmd.setSegmentType(rs.getString("Segment_Type"));
				cmd.setMemberCode(rs.getString("Member_Code"));
				cmd.setMemberId(rs.getString("Member_ID"));
				cmd.setPreviousMemberId(rs.getString("Previous_Member_ID"));
				cmd.setMemberShortName(rs.getString("Member_Short_Name"));
				cmd.setMemberPassword(rs.getString("Member_Password"));
				cmd.setFilePath(rs.getString("File_Path"));
				cmd.setFileFormate(rs.getString("File_Formate"));

				return cmd;
			}, bureauType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public CibilMemberDetail getMemberDetailsByType(String bureauType, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, Type, Segment_Type, Member_Code, Member_ID, Previous_Member_ID");
		sql.append(", Member_Short_Name, Member_Password, File_Path, File_Formate");
		sql.append(" From CIBIL_MEMBER_DETAILS");
		sql.append(" Where Segment_Type = ? and Type = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CibilMemberDetail cmd = new CibilMemberDetail();

				cmd.setId(rs.getString("ID"));
				cmd.setType(rs.getString("TYPE"));
				cmd.setSegmentType(rs.getString("SEGMENT_TYPE"));
				cmd.setMemberCode(rs.getString("MEMBER_CODE"));
				cmd.setMemberId(rs.getString("MEMBER_ID"));
				cmd.setPreviousMemberId(rs.getString("PREVIOUS_MEMBER_ID"));
				cmd.setMemberShortName(rs.getString("MEMBER_SHORT_NAME"));
				cmd.setMemberPassword(rs.getString("MEMBER_PASSWORD"));
				cmd.setFilePath(rs.getString("FILE_PATH"));
				cmd.setFileFormate(rs.getString("FILE_FORMATE"));

				return cmd;
			}, bureauType, type);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void logFileInfo(CibilFileInfo fileInfo) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		StringBuilder sql = new StringBuilder("insert into cibil_file_info");
		sql.append(" (File_Name, Member_Id, Member_Short_Name, Member_Password, CreatedOn");
		sql.append(", Status, File_Location, Start_Time, Segment_Type)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 1;

					CibilMemberDetail memberDetail = fileInfo.getCibilMemberDetail();
					ps.setString(index++, fileInfo.getFileName());
					ps.setString(index++, memberDetail.getMemberId());
					ps.setString(index++, memberDetail.getMemberShortName());
					ps.setString(index++, memberDetail.getMemberPassword());
					ps.setDate(index++, JdbcUtil.getDate(SysParamUtil.getAppDate()));
					ps.setString(index++, "I");
					ps.setString(index++, memberDetail.getFilePath());
					ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
					ps.setString(index, memberDetail.getSegmentType());

					return ps;
				}
			}, keyHolder);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		fileInfo.setId(keyHolder.getKey().longValue());
	}

	@Override
	public long extractCustomers(String segmentType, String entity) throws Exception {
		StringBuilder sql = new StringBuilder("INSERT INTO CIBIL_CUSTOMER_EXTRACT");
		sql.append("(CustID, FinID, FinReference, OwnerShip, LatestRpyDate, Segment_type");
		if (ImplementationConstants.CIBIL_BASED_ON_ENTITY) {
			sql.append(", Entity");
		}
		sql.append(")");
		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(segmentType)) {
			sql.append(" Select CustID, FinID, FinReference, OwnerShip, LatestRpyDate, ?");
			if (ImplementationConstants.CIBIL_BASED_ON_ENTITY) {
				sql.append(", ?");
			}
			sql.append(" From Cibil_Customer_Extarct_View");
		} else {
			sql.append(" Select c.CustID, fm.FinID, fm.FinReference, 0, LatestRpyDate, ?");
			if (ImplementationConstants.CIBIL_BASED_ON_ENTITY) {
				sql.append(", ?");
			}
			sql.append(" From FinanceMain fm");
			sql.append(" Inner Join FinPftDetails fp on fp.FinID = fm.FinID");
			sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
			sql.append(" Inner Join RmtCustTypes ct on ct.CustTypeCode = c.CustTypeCode and ct.CustTypeCtg <> ?");
			sql.append(" Inner Join RMTFinanceTypes rmt on rmt.FinType = fm.FinType");
			sql.append(" Inner Join SMTDivisionDetail smt ON smt.DivisionCode = rmt.FinDivision");
		}

		sql.append(" Where LatestRpyDate >= ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, segmentType);
				if (ImplementationConstants.CIBIL_BASED_ON_ENTITY) {
					ps.setString(index++, entity);
				}

				if (!PennantConstants.PFF_CUSTCTG_INDIV.equals(segmentType)) {
					ps.setString(index++, PennantConstants.PFF_CUSTCTG_INDIV);
				}

				ps.setDate(index, JdbcUtil.getDate(DateUtil.addMonths(SysParamUtil.getAppDate(), -36)));
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception(String.format("Unable Extarct %s CIBIL Data", segmentType));
		}
	}

	@Override
	public void updateFileStatus(CibilFileInfo fileInfo) {
		StringBuilder sql = new StringBuilder("Update Cibil_File_Info");
		sql.append(" Set Status = ?, Total_Records = ?, Processed_Records = ?");
		sql.append(", Success_Records = ?, Failed_Records = ?, Remarks = ?, End_Time = ?");
		sql.append(" Where ID = ?");

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, "S".equals(fileInfo.getStatus()) ? "C" : "F");
			ps.setLong(index++, fileInfo.getTotalRecords());
			ps.setLong(index++, fileInfo.getProcessedRecords());
			ps.setLong(index++, fileInfo.getSuccessCount());
			ps.setLong(index++, fileInfo.getFailedCount());
			ps.setString(index++, fileInfo.getRemarks());
			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));

			ps.setLong(index, fileInfo.getId());
		});
	}

	@Override
	public List<FinODDetails> getFinODDetails(long finID, String finCCY) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinODSchdDate, UDF_CONVERTCURRENCY(FinCurODAmt, ?, ?) FinCurODAmt, FinCurODDays");
		sql.append(" From FinODDetails od");
		sql.append(" Where FinID = ? and FinCurODAmt > ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinODDetails finODDtl = new FinODDetails();
			finODDtl.setFinODSchdDate(rs.getDate("FinODSchdDate"));
			finODDtl.setFinCurODAmt(rs.getBigDecimal("FinCurODAmt"));
			finODDtl.setFinCurODDays(rs.getInt("FinCurODDays"));
			return finODDtl;
		}, "INR", "INR", finID, 0);
	}

	@Override
	public List<CollateralSetup> getCollateralDetails(long finID, String segmentType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" cs.BankValuation, ccy.CcyCode, cct.Code");
		sql.append(" From collateralassignment ca");
		/* FIXME : change to FinID */
		sql.append(" Inner Join Cibil_Customer_Extract cce on cce.FinReference = ca.Reference");
		sql.append(" Inner Join CollateralSetup cs on cs.CollateralRef = ca.CollateralRef");
		sql.append(" Inner Join RmtCurrencies ccy on ccy.CcyCode = cs.CollateralCcy");
		sql.append(" Inner Join CollateralStructure ce on ce.CollateralType = cs.CollateralType");
		sql.append(" Left Join Cibil_Collateral_Types_Mapping cctm on cctm.Collateral_Type = cs.CollateralType");
		sql.append(" and cctm.Segment_Type = ?");
		sql.append(" Left Join Cibil_Collateral_Types cct on cct.Code = cctm.Code and cct.Segment_Type = ?");
		sql.append(" where cce.FinID = ? and cce.segment_type = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CollateralSetup colltflStp = new CollateralSetup();

			colltflStp.setBankValuation(rs.getBigDecimal("BankValuation"));
			colltflStp.setCollateralCcy(rs.getString("CcyCode"));
			colltflStp.setCollateralType(rs.getString("Code"));

			return colltflStp;
		}, segmentType, segmentType, finID, segmentType);

	}

	@Override
	public List<ChequeDetail> getChequeBounceStatus(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BounceDate, ReceiptAmount, rd.FavourNumber, br.Reason");
		sql.append(" From finreceiptheader rh");
		sql.append(" Inner Join FinReceiptDetail rd on rd.ReceiptId = rh.ReceiptId");
		sql.append(" Inner Join ManualAdvise ma on ma.ReceiptId = rh.ReceiptId");
		sql.append(" Inner Join bouncereasons br on br.BounceID = ma.BounceID");
		sql.append(" where Receiptmode = ? and receiptmodestatus = ? and rh.Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ChequeDetail chqueDtl = new ChequeDetail();

			chqueDtl.setChequeBounceDate(rs.getDate("BounceDate"));
			chqueDtl.setAmount(rs.getBigDecimal("ReceiptAmount"));
			chqueDtl.setChequeNumber(rs.getString("FavourNumber"));
			chqueDtl.setChequeBounceReason(rs.getString("Reason"));

			return chqueDtl;
		}, "CHEQUE", "B", finReference);
	}

	@Override
	public List<Long> getGuarantorsDetails(long finID, boolean isBankCustomers) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" c.CustID, GuarantorID");
		sql.append(" From FinGuarantorsDetails gd");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = gd.FinID");
		sql.append(" Inner Join Customers c on c.CustCif = gd.GuarantorCif");
		sql.append(" Where fm.FinID = ? and BankCustomer = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			if (isBankCustomers) {
				return rs.getLong("CustID");
			} else {
				return rs.getLong("GuarantorID");
			}
		}, finID, isBankCustomers ? 1 : 0);
	}

	@Override
	public long getotalRecords(String segmentType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select count(CustID) from (");
		sql.append(" Select distinct CustID From Cibil_Customer_Extract");
		sql.append(" Where Segment_Type = ?) t");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Long.class, segmentType);
	}

	@Override
	public Customer getExternalCustomer(Long customerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CustomerAddres> getExternalCustomerAddres(Long custId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CustomerPhoneNumber> getExternalCustomerPhoneNumbers(Long custId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CustomerDocument> getExternalCustomerDocuments(Long custId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventProperties getEventProperties(String configName) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" dep.Storage_Type, dep.Region_Name, dep.Bucket_Name, dep.Access_Key");
		sql.append(", dep.Secret_Key, dep.Prefix, dep.Sse_Algorithm, dep.Host_Name, dep.Port, dep.Private_Key");
		sql.append(" From Data_Engine_Event_Properties dep");
		sql.append(" Inner Join Data_Engine_Config dc on dc.ID = dep.Config_ID");
		sql.append(" Where dc.Name = ?");

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				EventProperties evntProrts = new EventProperties();

				evntProrts.setStorageType(rs.getString("Storage_Type"));
				evntProrts.setRegionName(rs.getString("Region_Name"));
				evntProrts.setBucketName(rs.getString("Bucket_Name"));
				evntProrts.setAccessKey(rs.getString("Access_Key"));
				evntProrts.setSecretKey(rs.getString("Secret_Key"));
				evntProrts.setPrefix(rs.getString("Prefix"));
				evntProrts.setSseAlgorithm(rs.getString("Sse_Algorithm"));
				evntProrts.setHostName(rs.getString("Host_Name"));
				evntProrts.setPort(rs.getString("Port"));
				evntProrts.setPrivateKey(rs.getString("Private_Key"));

				return evntProrts;
			}, configName);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Configuration details not available for " + configName);
			return null;
		}
	}

	@Override
	public List<Long> getJointAccountDetails(long finID) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Distinct c.CustID");
		sql.append(" From FinJointAccountDetails jd");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = jd.FinID");
		sql.append(" Inner Join Customers c on c.CustCIF = jd.CustCIF");
		sql.append(" Where fm.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			return rs.getLong("CustID");
		}, finID);
	}

	@Override
	public BigDecimal getLastRepaidAmount(String finReference) {
		String sql = "Select Amount From FinReceiptDetail Where ReceiptId = (Select max(ReceiptId) from FinReceiptHeader where Reference = ?)";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public BigDecimal getGuarantorPercentage(long finID) {
		String sql = "Select coalesce(sum(GuranteePercentage), 0) From FinGuarantorsDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}

	@Override
	public List<String> getEntityCodes() {
		String sql = "Select EntityCode From Entity where Active = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.query(sql, (rs, rowNum) -> {
			return rs.getString(1);
		}, 1);

	}

	@Override
	public String getCoAppRelation(String custCIF, long finID) {
		String sql = "Select CatOfCoApplicant from  FinJointAccountDetails Where FinID = ? and CustCif = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, String.class, finID, custCIF);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceSummary getFinanceProfitDetails(String finRef) {
		String sql = "Select TotalOverDue, OutStandPrincipal from FinanceProfitEnquiry_View where FinReference = ?";

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FinanceSummary summary = new FinanceSummary();

				summary.setTotalOverDue(rs.getBigDecimal("TotalOverDue"));
				summary.setOutStandPrincipal(rs.getBigDecimal("OutStandPrincipal"));

				return summary;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}