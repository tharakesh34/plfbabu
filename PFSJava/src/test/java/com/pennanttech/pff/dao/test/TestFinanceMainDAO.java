package com.pennanttech.pff.dao.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceMainExtension;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pff.core.TableType;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFinanceMainDAO {

	@Autowired
	private FinanceMainDAO financeMainDAO;

	@Autowired
	private FinanceDisbursementDAO financeDisbursementDAO;

	@Autowired
	private FinODDetailsDAO finODDetailsDAO;

	@Autowired
	private FinODPenaltyRateDAO finODPenaltyRateDAO;

	@Autowired
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	private FinanceMain fm;

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinanceMainByRef() {
		// financeMainDAO.getFinanceMain(false); //Module Registration not available
		// financeMainDAO.getFinanceMain(true); //Module Registration not available

		// financeMainDAO.getFinanceMain(490, "", "_View"); //FinID not existed in FinanceMain_View

		// financeMainDAO.getFinanceDetailsByCustId(1759); //FinID not existed in FinanceEnquiry_View

		// financeMainDAO.getFinanceProfitDetails(5222); //FinID not existed in FinanceProfitEnquiry_View

		// financeMainDAO.getFinExposureByCustId(1759); //FinID not existed in CustFinanceExposure_View

		// financeMainDAO.getFinanceForAssignments(5354); //AlwFlexi column not existed in FinanceMain

		// financeMainDAO.updateAssignmentId(5354, 1); //AssignmentId column not existed in FinanceMain

		// financeMainDAO.getGLSubHeadCodes(5354); //FinID not existed in GL_SubHeadCodes_View

		List<String> firstTask = financeMainDAO.getFinanceWorlflowFirstTaskOwners("AddDisbursement", "");
		List<String> firstTaskList = financeMainDAO.getFinanceWorlflowFirstTaskOwners("", "");

		financeMainDAO.getFinanceMain(490, "", "");
		financeMainDAO.getFinanceMain(491, "", "");

		financeMainDAO.getFinanceMainByRef("1000AGR0001535", "", false);
		financeMainDAO.getFinanceMainByRef("1500AGR0001258", "_Temp", true);
		financeMainDAO.getFinanceMainByRef("10000200001000", "", false);

		financeMainDAO.getFinanceMainById(5222, "", false);
		financeMainDAO.getFinanceMainById(500, "", false);

		financeMainDAO.getDisbursmentFinMainById(5222, TableType.MAIN_TAB);
		financeMainDAO.getDisbursmentFinMainById(5222, TableType.TEMP_TAB);

		financeMainDAO.getFinanceMainForPftCalc(3962);
		financeMainDAO.getFinanceMainForPftCalc(300);

		financeMainDAO.getFinanceMainForRpyCancel(4763);
		financeMainDAO.getFinanceMainForRpyCancel(400);

		financeMainDAO.getFinanceMainForBatch(4557);
		financeMainDAO.getFinanceMainForBatch(1001);

		Date dt1 = DateUtil.parse("01/02/2019", DateFormat.SHORT_DATE);
		Date dt2 = DateUtil.parse("01/07/2021", DateFormat.SHORT_DATE);
		financeMainDAO.getFinanceMainListByBatch(dt1, dt2, "");
		financeMainDAO.getFinanceMainListByBatch(dt1, dt2, "_Temp");

		financeMainDAO.isFinReferenceExists(5222, "", false);
		boolean isExists = financeMainDAO.isFinReferenceExists(5323, "", true);
		boolean isExist = financeMainDAO.isFinReferenceExists(500, "", false);

		financeMainDAO.getActualPftBal(5222, "");
		financeMainDAO.getActualPftBal(500, "");

		financeMainDAO.updateCustCIF(1, 5345);

		financeMainDAO.updateFinBlackListStatus(5222);

		financeMainDAO.getNextRoleCodeByRef(5345);
		financeMainDAO.getNextRoleCodeByRef(5);

		financeMainDAO.getFinanceRefByPriority();

		financeMainDAO.getApprovedRepayMethod(5354, "");

		financeMainDAO.updateMaturity(5354, "M", false, dt2);
		financeMainDAO.updateMaturity(5354, "M", true, dt2);

		financeMainDAO.getFinanceMainbyCustId(1463);

		int count = financeMainDAO.getFinanceCountById(5354, "", false);
		int count1 = financeMainDAO.getFinanceCountById(500, "", false);
		financeMainDAO.getFinanceCountById(5323, "", true);

		int cnt = financeMainDAO.getFinCountByCustId(19);
		int cnt1 = financeMainDAO.getFinCountByCustId(100);

		int mndcnt = financeMainDAO.getFinanceCountByMandateId(347);

		financeMainDAO.getFinanceCountById(5354, 0);

		financeMainDAO.loanMandateSwapping(5354, 1, "MANUAL", "");
		financeMainDAO.loanMandateSwapping(5354, 1, "", "");

		financeMainDAO.getFinanceDetailsForService(5222, "", false);
		financeMainDAO.getFinanceDetailsForService(5323, "", true);
		financeMainDAO.getFinanceDetailsForService(500, "", false);

		financeMainDAO.getFinanceByCustId(1463, "");

		List<FinanceMain> fm = financeMainDAO.getFinanceByCollateralRef("CT2930100001");

		financeMainDAO.getFinReferencesByMandateId(347);

		financeMainDAO.getFinReferencesByCustID(19, null);
		financeMainDAO.getFinReferencesByCustID(1463, "M");

		financeMainDAO.getFinAssetValue(5354);

		financeMainDAO.getFinBranch(5222);

		financeMainDAO.getFinMainsForEODByFinRef(5354, true);
		financeMainDAO.getFinMainsForEODByFinRef(5354, false);

		financeMainDAO.getFinanceBasicDetailByRef(5354, false);
		financeMainDAO.getFinanceBasicDetailByRef(5323, true);
		financeMainDAO.getFinanceBasicDetailByRef(5323, false);

		financeMainDAO.updateFinMandateId(347, 5222, "");

		financeMainDAO.getMandateIdByRef(5222, "");
		financeMainDAO.getMandateIdByRef(500, "");

		financeMainDAO.getFinanceCountById(5354);
		financeMainDAO.getFinanceCountById(500);

		financeMainDAO.getApplicationNoById(5354, "");
		financeMainDAO.getApplicationNoById(500, "");

		financeMainDAO.isFinTypeExistsInFinanceMain("IBASL", "");
		financeMainDAO.isFinTypeExistsInFinanceMain("IBA", "");

		financeMainDAO.getEarlyPayMethodsByFinRefernce(5354);
		financeMainDAO.getEarlyPayMethodsByFinRefernce(500);

		financeMainDAO.getActiveCount("IBASL", 1463);

		financeMainDAO.getODLoanCount("BL", 1463);

		financeMainDAO.getUnApprovedFinances();

		financeMainDAO.updateNextUserId(5354, "");

		financeMainDAO.getNextUserId(5222);
		financeMainDAO.getNextUserId(5345);

		financeMainDAO.getEntityNEntityDesc(5354, "", false);
		financeMainDAO.getEntityNEntityDesc(5353, "", true);

		financeMainDAO.getClosingStatus(5354, TableType.MAIN_TAB, false);
		financeMainDAO.getClosingStatus(5353, TableType.MAIN_TAB, true);

		financeMainDAO.getClosedDateByFinRef(5354);
		financeMainDAO.getClosedDateByFinRef(500);

		financeMainDAO.isFinReferenceExitsWithEntity(5222, "", "ESFB");
		financeMainDAO.isFinReferenceExitsWithEntity(5222, "", "FG");

		financeMainDAO.isDeveloperFinance(5354, "", false);
		financeMainDAO.isDeveloperFinance(5353, "", true);
		financeMainDAO.isDeveloperFinance(500, "", false);

		financeMainDAO.getFinanceMainbyCustId(1463, "");
		financeMainDAO.getFinanceMainbyCustId(1, "_Temp");

		financeMainDAO.getFinanceTypeFinReference(5354, "");
		financeMainDAO.getFinanceTypeFinReference(500, "");

		financeMainDAO.getFinListForIncomeAMZ(dt2);

		// financeMainDAO.getCountByBlockedFinances(5354); // BlockedFinance not existed

		// financeMainDAO.isFlexiLoan(5354); //AlwFlexi column not existed in FinanceMain

		financeMainDAO.isFinReferenceExitsinLQ(5354, TableType.MAIN_TAB, false);
		financeMainDAO.isFinReferenceExitsinLQ(500, TableType.MAIN_TAB, false);

		financeMainDAO.getFinanceMainForLinkedLoans(1463);

		financeMainDAO.getFinanceMainForLinkedLoans("1500AGR0001450");

		financeMainDAO.getGSTDataMap(5354, TableType.MAIN_TAB);
		financeMainDAO.getGSTDataMap(5345, TableType.TEMP_TAB);

		financeMainDAO.getCustGSTDataMap(1463, TableType.MAIN_TAB);
		financeMainDAO.getCustGSTDataMap(1, TableType.TEMP_TAB);

		financeMainDAO.isFinActive(5354);

		financeMainDAO.getFinanceMainByRcdMaintenance(5354, "");
		financeMainDAO.getFinanceMainByRcdMaintenance(500, "");

		financeMainDAO.getRcdMaintenanceByRef(5354, "");
		financeMainDAO.getRcdMaintenanceByRef(500, "");

		financeMainDAO.getFinanceMainByOldFinReference("", true);

		Date dt3 = DateUtil.parse("05/04/2019", DateFormat.SHORT_DATE);
		Date dt4 = DateUtil.parse("04/12/2033", DateFormat.SHORT_DATE);

		// financeMainDAO.getFinancesByFinApprovedDate(dt3, dt4);// EntityCode column does not exist in FinanceMain

		financeMainDAO.getFinanceForIncomeAMZ(5354);
		financeMainDAO.getFinanceForIncomeAMZ(500);

		// financeMainDAO.getFinListForAMZ(dt3); // EntityCode column does not exist in FinanceMain

		financeMainDAO.getCountByFinReference(5354, true);

		financeMainDAO.getCountByOldFinReference("12869399899");

		financeMainDAO.getLoanWorkFlowIdByFinRef(5354, "");

		// financeMainDAO.getLovDescEntityCode(5354, ""); // LovDescEntityCode column does not exist in FinanceMain

		financeMainDAO.getFinanceMainByHostReference("1858799199", true);
		financeMainDAO.getFinanceMainByHostReference("12", true);

		financeMainDAO.getCountByExternalReference("1858799199");

		financeMainDAO.getCountByOldHostReference("1858799199");

		financeMainDAO.getFinanceMainStutusById(5354, "");
		financeMainDAO.getFinanceMainStutusById(500, "");

		financeMainDAO.getFinanceDetailsForInsurance(5354, "");
		financeMainDAO.getFinanceDetailsForInsurance(500, "");

		financeMainDAO.getFinMainListBySQLQueryRule("Where FinID = 5354", "");

		financeMainDAO.getFinanceMainDetails(5354);
		financeMainDAO.getFinanceMainDetails(500);

		// financeMainDAO.isFinExistsByPromotionSeqID(0); //FinID column does not exist in FinanceMain_View
		// financeMainDAO.isRepayFrqExists("MCLR"); //FinID column does not exist in FinanceMain_View
		// financeMainDAO.isGrcRepayFrqExists("M0004"); //FinID column does not exist in FinanceMain_View

		financeMainDAO.getFinStartDate(5354);

		// financeMainDAO.getAllFinanceDetailsByCustId(1463); // FinID column does not exist in FinanceEnquiry_View

		financeMainDAO.updateCustChange(1, 1, 5354, "");

		financeMainDAO.getUserPendingCasesDetails(0, "");

		financeMainDAO.getCustomerIdByFin(5354);
		financeMainDAO.getCustomerIdByFin(110);

		financeMainDAO.getFinDetailsForHunter("123", "");

		financeMainDAO.getFinanceByInvReference(5354, "");

		financeMainDAO.getInvestmentFinRef("", "");

		financeMainDAO.getParentRefifAny("", "", false);
		financeMainDAO.getParentRefifAny("", "", true);

		financeMainDAO.updatePmay(5354, true, "");

		financeMainDAO.getEHFinanceMain(5354);
		financeMainDAO.getEHFinanceMain(500);

		FinanceMain fma = financeMainDAO.getFinBasicDetails(5354, "");
		FinanceMain fmn = financeMainDAO.getFinBasicDetails(5345, "_Temp");
		financeMainDAO.getFinBasicDetails(500, "");

		financeMainDAO.getClosedDate(5354);

		financeMainDAO.isPmayApplicable(5354, "");

		financeMainDAO.updateRestructure(5354, true);

		financeMainDAO.getGSTDataMapForDealer(72);

		financeMainDAO.updateWriteOffStatus(5354, true);

		financeMainDAO.updateMaintainceStatus(5354, "");

		financeMainDAO.getFinCategoryByFinRef(5354);
		financeMainDAO.getFinCategoryByFinRef(500);

		financeMainDAO.getChildFinRefByParentRef("");

		financeMainDAO.getSchdVersion(5354);

		// LovDescCustCIF column does not exist in FinanceMain
		// financeMainDAO.getFinMainLinkedFinancesByFinRef(5354, "");

		financeMainDAO.isLoanPurposeExits("", "");
		financeMainDAO.isLoanPurposeExits("abc", "");

		financeMainDAO.getCustomerODLoanDetails(0);

		// financeMainDAO.getFinanceDetailsByFinRefence(5354, "");

		financeMainDAO.isAppNoExists("", TableType.MAIN_TAB);
		financeMainDAO.isAppNoExists("", TableType.TEMP_TAB);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5345, "_Temp", false);
		fm.setFinID(fm.getFinID() + 1);
		fm.setFinReference(fm.getFinReference() + 1);
		financeMainDAO.save(fm, TableType.TEMP_TAB, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveMain() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setFinID(fm.getFinID() + 1);
		fm.setFinReference(fm.getFinReference() + 1);
		financeMainDAO.save(fm, TableType.MAIN_TAB, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveWIF() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5323, "", true);
		fm.setFinID(fm.getFinID() + 1);
		fm.setFinReference(fm.getFinReference() + 1);
		financeMainDAO.save(fm, TableType.MAIN_TAB, true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave1() {
		// Duplicate Key Exception
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5345, "_Temp", false);
		fm.setFinID(fm.getFinID() + 1);
		financeMainDAO.save(fm, TableType.TEMP_TAB, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setVersion(17);
		financeMainDAO.update(fm, TableType.MAIN_TAB, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate2() {
		// record count = 0
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5345, "_Temp", false);
		fm.setFinIsActive(false);
		financeMainDAO.update(fm, TableType.TEMP_TAB, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateWIF() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5323, "", true);
		fm.setVersion(3);
		financeMainDAO.update(fm, TableType.MAIN_TAB, true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteTemp() {
		Date dt1 = DateUtil.parse("01/02/2019", DateFormat.SHORT_DATE);
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5345, "_Temp", false);
		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteMain() {
		// DependencyFoundException
		Date dt1 = DateUtil.parse("01/02/2019", DateFormat.SHORT_DATE);
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeDisbursementDAO.deleteByFinReference(5354, "", false, 0);
		finODDetailsDAO.deleteAfterODDate("1500BUS0003280", JdbcUtil.getDate(dt1));
		finODPenaltyRateDAO.delete("1500BUS0003280", "");
		financeScheduleDetailDAO.deleteByFinReference(5354, "", false, 0);
		financeMainDAO.delete(fm, TableType.MAIN_TAB, false, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteWIF() {
		// record count = 0
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5323, "", true);
		financeMainDAO.delete(fm, TableType.TEMP_TAB, true, true);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateCustCIF() {
		// recordcount = 0
		financeMainDAO.updateCustCIF(1, 5);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateFinBlackListStatus() {
		// recordcount = 0
		financeMainDAO.updateFinBlackListStatus(500);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveRejectFinanceDetails() {
		// difference b/w no. of columns in tables
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.saveRejectFinanceDetails(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveFinanceSnapshot() {
		// difference b/w no. of columns in tables
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.saveFinanceSnapshot(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateNextUserId() {
		List<Long> finIdList = new ArrayList<>();
		fm = financeMainDAO.getFinanceMainById(5345, "_Temp", false);
		finIdList.add(fm.getFinID());
		financeMainDAO.updateNextUserId(finIdList, "", "", false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateNextUserId1() {
		List<Long> finIdList = new ArrayList<>();
		App.DATABASE = Database.ORACLE;
		fm = financeMainDAO.getFinanceMainById(5345, "_Temp", false);
		finIdList.add(fm.getFinID());
		financeMainDAO.updateNextUserId(finIdList, "", "", true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateNextUserId2() {
		List<Long> finIdList = new ArrayList<>();
		App.DATABASE = Database.POSTGRES;
		fm = financeMainDAO.getFinanceMainById(5345, "_Temp", false);
		finIdList.add(fm.getFinID());
		financeMainDAO.updateNextUserId(finIdList, "", "", true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateNextUserId3() {
		List<Long> finIdList = new ArrayList<>();
		App.DATABASE = Database.SQL_SERVER;
		fm = financeMainDAO.getFinanceMainById(5345, "_Temp", false);
		finIdList.add(fm.getFinID());
		financeMainDAO.updateNextUserId(finIdList, "", "", true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateDeviationApproval() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updateDeviationApproval(fm, false, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateDeviationApprovalTemp() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5345, "_Temp", false);
		financeMainDAO.updateDeviationApproval(fm, true, "_Temp");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdatePaymentInEOD() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updatePaymentInEOD(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdatePaymentInEOD1() {
		// Issue with Date
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setFinIsActive(false);
		financeMainDAO.updatePaymentInEOD(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdatePaymentInEOD2() {
		// record count = 0
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setFinID(500);
		financeMainDAO.updatePaymentInEOD(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetScheduleEffectModuleList() {
		financeMainDAO.getScheduleEffectModuleList(true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateFinanceBasicDetails() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updateFinanceBasicDetails(fm, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetUsersLoginList() {
		List<String> usrids = new ArrayList<>();
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		usrids.add(fm.getNextUserId());
		financeMainDAO.getUsersLoginList(usrids);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinMainsForEODByCustId() {
		App.DATABASE = Database.POSTGRES;
		financeMainDAO.getFinMainsForEODByCustId(1463, true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinMainsForEODByCustId1() {
		App.DATABASE = Database.SQL_SERVER;
		financeMainDAO.getFinMainsForEODByCustId(19, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetBYCustIdForLimitRebuild() {
		App.DATABASE = Database.SQL_SERVER;
		financeMainDAO.getBYCustIdForLimitRebuild(1463, true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetBYCustIdForLimitRebuild1() {
		App.DATABASE = Database.ORACLE;
		financeMainDAO.getBYCustIdForLimitRebuild(19, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetBYCustIdForLimitRebuild2() {
		App.DATABASE = Database.ORACLE;
		financeMainDAO.getBYCustIdForLimitRebuild(1, true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateFinAssetValue() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updateFinAssetValue(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateFromReceipt() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updateFromReceipt(fm, TableType.MAIN_TAB);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateFromReceipt1() {
		// Issue with Date
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setFinIsActive(false);
		financeMainDAO.updateFromReceipt(fm, TableType.MAIN_TAB);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateFromReceipt2() {
		// record count = 0
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setFinID(500);
		financeMainDAO.updateFromReceipt(fm, TableType.MAIN_TAB);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testIsFinReferenceExitsinLQ() {
		// RcdMaintainSts column does not exist in WifFinanceMain
		financeMainDAO.isFinReferenceExitsinLQ(5323, TableType.MAIN_TAB, true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteFinreference() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5345, "_Temp", false);
		financeMainDAO.deleteFinreference(fm, TableType.TEMP_TAB, false, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteFinreference1() {
		// DependencyFoundException
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5323, "", true);
		financeMainDAO.deleteFinreference(fm, TableType.MAIN_TAB, true, true);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteFinreference2() {
		// recordcount = 0
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5323, "", true);
		financeMainDAO.deleteFinreference(fm, TableType.TEMP_TAB, true, true);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveHostRef() {
		FinanceMainExtension fm = new FinanceMainExtension();
		fm.setFinId(5354);
		fm.setFinreference("1500BUS0003280");
		fm.setHostreference("1858799200");
		fm.setOldhostreference(null);
		financeMainDAO.saveHostRef(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateRejectFinanceMain() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updateRejectFinanceMain(fm, TableType.MAIN_TAB, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateRejectFinanceMain1() {
		// recordcount = 0
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setFinID(500);
		financeMainDAO.updateRejectFinanceMain(fm, TableType.MAIN_TAB, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateCustChange() {
		// recordcount = 0
		financeMainDAO.updateCustChange(1, 1, 500, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdatePmay() {
		// recordcount = 0
		financeMainDAO.updatePmay(500, true, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetDetailsByOfferID() {
		// CustID doesnot exist in FinJointAccountDetails(setJointAccountDetails)
		financeMainDAO.getDetailsByOfferID("");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateEHFinanceMain() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updateEHFinanceMain(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateEHFinanceMain1() {
		// record count = 0
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setFinID(500);
		financeMainDAO.updateEHFinanceMain(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateDeductFeeDisb() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updateDeductFeeDisb(fm, TableType.MAIN_TAB);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateDeductFeeDisb1() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5345, "_Temp", false);
		financeMainDAO.updateDeductFeeDisb(fm, TableType.TEMP_TAB);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateDeductFeeDisb2() {
		// recordcount = 0
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setFinID(500);
		financeMainDAO.updateDeductFeeDisb(fm, TableType.MAIN_TAB);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateTdsApplicable() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updateTdsApplicable(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateTdsApplicable1() {
		// record count = 0
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setFinID(500);
		financeMainDAO.updateTdsApplicable(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateRepaymentAmount() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updateRepaymentAmount(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateRepaymentAmount1() {
		// record count = 0
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setFinID(500);
		financeMainDAO.updateRepaymentAmount(fm);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateChildFinance() {
		List<FinanceMain> list = new ArrayList<FinanceMain>();
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		list.add(fm);
		financeMainDAO.updateChildFinance(list, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateRejectFinanceMainList() {
		List<FinanceMain> list = new ArrayList<FinanceMain>();
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		list.add(fm);
		financeMainDAO.updateRejectFinanceMain(list, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateSchdVersion() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updateSchdVersion(fm, true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateSchdVersion1() {
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		financeMainDAO.updateSchdVersion(fm, false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateSchdVersion2() {
		// record count = 0
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fm.setFinID(500);
		financeMainDAO.updateSchdVersion(fm, true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinancesByExpenseType() {
		Date dt1 = DateUtil.parse("05/06/2029", DateFormat.SHORT_DATE);
		Date dt2 = DateUtil.parse("10/06/2029", DateFormat.SHORT_DATE);
		financeMainDAO.getFinancesByExpenseType("IBASL", dt1, dt2);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetOfferIdByFin() {
		DMSQueue dms = new DMSQueue();
		dms.setFinID(5354);
		financeMainDAO.getOfferIdByFin(dms);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinDetailsForHunter() {
		// InCorrect ResultSize
		financeMainDAO.getFinDetailsForHunter("", "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testIsAppNoExists() {
		// Parameters not supplied when main and temp tables are given
		financeMainDAO.isAppNoExists("", TableType.BOTH_TAB);
	}

}