package com.pennant.app.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.accounts.AccountsDAO;
import com.pennant.backend.dao.accounts.AccountsHistoryDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.model.accounts.AccountHistoryDetail;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.accounts.AccountsHistory;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class CustomerAccountService extends ServiceHelper {
	private static final long serialVersionUID = 1442146139821584760L;
	private Logger logger = LogManager.getLogger(CustomerAccountService.class);

	private AccountsDAO accountsDAO;
	private AccountsHistoryDAO accountsHistoryDAO;
	private AccountTypeDAO accountTypeDAO;
	private DataSource dataSource;

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processCustomerAccountUpdate() throws Exception {
		/*
		 * logger.debug(" Entering ");
		 * 
		 * Map<String, Accounts> accountMap = new HashMap<String, Accounts>(1); Map<String, AccountsHistory>
		 * accountHistMap = new HashMap<String, AccountsHistory>(1); Map<String, AccountType> accountTypeMap = new
		 * HashMap<String, AccountType>(1);
		 * 
		 * StringBuilder sb = new StringBuilder();
		 * sb.append(" SELECT T1.PostDate, T1.ValueDate, T1.AppDate, T1.AppValueDate, T1.CustAppDate, ");
		 * sb.append(" T1.DrOrCr, T1.Account, T1.ShadowPosting, T1.PostAmount, T1.AcCcy, T1.PostBranch, T1.AccountType "
		 * ); sb.append(" FROM Postings T1 "); sb.append(" WHERE postCategory=? AND PostStatus = ?");
		 * 
		 * Connection connection = null; ResultSet resultSet = null; PreparedStatement sqlStatement = null;
		 * 
		 * try { connection = DataSourceUtils.doGetConnection(dataSource); sqlStatement =
		 * connection.prepareStatement(sb.toString()); sqlStatement.setInt(1, AccountConstants.POSTING_CATEGORY_EOD);
		 * sqlStatement.setString(2, AccountConstants.POSTINGS_SUCCESS); resultSet = sqlStatement.executeQuery(); while
		 * (resultSet.next()) {
		 * 
		 * ReturnDataSet posting = getReturnDataSet(resultSet);
		 * 
		 * if (posting.getPostAmount().compareTo(BigDecimal.ZERO) == 0) { continue; }
		 * 
		 * String acTypeKey = posting.getAccountType(); AccountType accountType = new AccountType();
		 * 
		 * if (!accountTypeMap.containsKey(acTypeKey)) { accountType = accountTypeDAO.getAccountTypeById(acTypeKey, "");
		 * accountTypeMap.put(acTypeKey, accountType); } else { accountType = accountTypeMap.get(acTypeKey); }
		 * 
		 * prepareAccounts(accountMap, posting, accountType); PrepareAccountsHist(accountHistMap, posting); }
		 * 
		 * //Update Accounts for (Entry<String, Accounts> account : accountMap.entrySet()) {
		 * accountsDAO.saveOrUpdate(account.getValue(), ""); }
		 * 
		 * //Update Accounts History for (Entry<String, AccountsHistory> accountHist : accountHistMap.entrySet()) {
		 * accountsHistoryDAO.saveOrUpdate(accountHist.getValue()); }
		 * 
		 * resultSet.close(); sqlStatement.close();
		 * 
		 * } catch (Exception e) { logger.error("Exception: ", e); throw e; } finally {
		 * DataSourceUtils.releaseConnection(connection, dataSource); }
		 * 
		 * getPostingsDAO().updatePostCtg();
		 * 
		 * logger.debug(" Leaving ");
		 */

	}

	public void processCustomerAccountHstyDetails() throws Exception {
		/*
		 * logger.debug(" Entering ");
		 * 
		 * List<AccountHistoryDetail> accHstyDetailList = new ArrayList<>();
		 * 
		 * StringBuilder sb = new StringBuilder();
		 * sb.append(" Select T1.Account AccountID, T1.PostDate, T1.EntityCode, T1.PostBranch, T2.BranchProvince, ");
		 * sb.append(" SUM(Case When DrOrCr = 'D' Then PostAmount * -1 Else 0 End) TodayDebits,  ");
		 * sb.append(" SUM(Case When DrOrCr = 'C' Then PostAmount Else 0 End) TodayCredits,0 TodayNet, ");
		 * sb.append(" 0 ShadowBalance, 0 AcBalance, 0 OpeningBalance from Postings T1  ");
		 * sb.append(" Inner Join RMTBranches T2 On  T1.PostBranch = T2.BranchCode ");
		 * sb.append(" Where T1.PostDate = ? ");
		 * sb.append(" Group BY T1.Account, T1.PostDate,T1.EntityCode, T1.PostBranch, T2.BranchProvince ");
		 * 
		 * Connection connection = null; ResultSet resultSet = null; PreparedStatement sqlStatement = null; try {
		 * connection = DataSourceUtils.doGetConnection(dataSource); sqlStatement =
		 * connection.prepareStatement(sb.toString()); sqlStatement.setDate(1, (Date) DateUtility.getAppDate());
		 * resultSet = sqlStatement.executeQuery(); while (resultSet.next()) {
		 * accHstyDetailList.add(getAccountHistoryDetail(resultSet)); } accountsHistoryDAO.save(accHstyDetailList);
		 * accountsHistoryDAO.update(accHstyDetailList); accountsHistoryDAO.updateCurrAccHstyDetails(accHstyDetailList);
		 * //accountsHistoryDAO.updateFields();
		 * 
		 * } catch (Exception e) { logger.error("Exception: ", e); throw e; } finally {
		 * DataSourceUtils.releaseConnection(connection, dataSource); }
		 * 
		 * logger.debug(" Leaving ");
		 */

	}

	private AccountHistoryDetail getAccountHistoryDetail(ResultSet resultSet) {
		AccountHistoryDetail accountHistoryDetail = new AccountHistoryDetail();
		try {
			accountHistoryDetail.setAccountId(resultSet.getString("AccountId"));
			accountHistoryDetail.setPostDate(resultSet.getDate("PostDate"));
			accountHistoryDetail.setEntityCode(resultSet.getString("EntityCode"));
			accountHistoryDetail.setPostBranch(resultSet.getString("PostBranch"));
			accountHistoryDetail.setBranchProvince(resultSet.getString("BranchProvince"));
			accountHistoryDetail.setTodayDebits(resultSet.getBigDecimal("TodayDebits"));
			accountHistoryDetail.setTodayCredits(resultSet.getBigDecimal("TodayCredits"));
			accountHistoryDetail.setTodayNet(resultSet.getBigDecimal("TodayNet"));
			accountHistoryDetail.setShadowBal(resultSet.getBigDecimal("ShadowBal"));
			accountHistoryDetail.setAcBalance(resultSet.getBigDecimal("AcBalance"));
			accountHistoryDetail.setOpeningBal(resultSet.getBigDecimal("OpeningBalance"));

		} catch (SQLException e) {
		}
		return accountHistoryDetail;
	}

	private ReturnDataSet getReturnDataSet(ResultSet resultSet) {
		ReturnDataSet returnDataSet = new ReturnDataSet();
		try {
			returnDataSet.setDrOrCr(resultSet.getString("DrOrCr"));
			returnDataSet.setAccount(resultSet.getString("Account"));
			returnDataSet.setShadowPosting(resultSet.getBoolean("ShadowPosting"));
			returnDataSet.setPostAmount(resultSet.getBigDecimal("PostAmount"));
			returnDataSet.setAcCcy(resultSet.getString("AcCcy"));
			returnDataSet.setAccountType(resultSet.getString("AccountType"));
			returnDataSet.setPostDate(resultSet.getDate("PostDate"));
			returnDataSet.setValueDate(resultSet.getDate("ValueDate"));
			returnDataSet.setAppDate(resultSet.getDate("AppDate"));
			returnDataSet.setAppValueDate(resultSet.getDate("AppValueDate"));
			returnDataSet.setCustAppDate(resultSet.getDate("CustAppDate"));
			returnDataSet.setPostBranch(resultSet.getString("PostBranch"));
		} catch (SQLException e) {
		}

		return returnDataSet;

	}

	private void prepareAccounts(Map<String, Accounts> accountMap, ReturnDataSet posting, AccountType accountType) {
		String accountKey = posting.getAccount();
		Accounts account = new Accounts();

		if (!accountMap.containsKey(accountKey)) {
			account = prepareAccountData(posting, account, accountType);
		} else {
			account = accountMap.get(accountKey);
		}

		if (posting.isShadowPosting()) {
			if (StringUtils.equals(posting.getDrOrCr(), "D")) {
				account.setShadowBal(account.getShadowBal().subtract(posting.getPostAmount()));
			} else {
				account.setShadowBal(account.getShadowBal().add(posting.getPostAmount()));
			}
		} else {
			if (StringUtils.equals(posting.getDrOrCr(), "D")) {
				account.setAcBalance(account.getAcBalance().subtract(posting.getPostAmount()));
			} else {
				account.setAcBalance(account.getAcBalance().add(posting.getPostAmount()));
			}
		}

		accountMap.put(accountKey, account);
	}

	private Accounts prepareAccountData(ReturnDataSet posting, Accounts account, AccountType accountType) {
		account.setAccountId(posting.getAccount());
		account.setAcCcy(posting.getAcCcy());
		account.setAcType(posting.getAccountType());
		account.setAcBranch(posting.getPostBranch());
		account.setAcCustId(0);
		account.setAcPurpose(accountType.getAcPurpose());
		account.setAcFullName(accountType.getAcTypeDesc());
		account.setAcShortName(accountType.getAcTypeDesc().length() > 20 ? accountType.getAcTypeDesc().substring(0, 18)
				: accountType.getAcTypeDesc());
		account.setInternalAc(accountType.isInternalAc());
		account.setCustSysAc(!accountType.isInternalAc());
		account.setAcOpenDate(SysParamUtil.getAppDate());
		account.setAcLastCustTrnDate(account.getAcOpenDate());
		account.setAcLastSysTrnDate(account.getAcOpenDate());
		account.setAcActive(true);

		account.setVersion(0);
		account.setLastMntBy(0);
		account.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		account.setRecordStatus("Approved");
		account.setRoleCode("");
		account.setNextRoleCode("");
		account.setTaskId("");
		account.setNextTaskId("");
		account.setRecordType("");
		account.setWorkflowId(0);
		return account;
	}

	private void PrepareAccountsHist(Map<String, AccountsHistory> accountHistMap, ReturnDataSet posting) {
		String accountHistKey = posting.getAccount().concat(DateUtility.formatToShortDate(posting.getPostDate()));
		AccountsHistory accountHist = new AccountsHistory();

		if (!accountHistMap.containsKey(accountHistKey)) {
			accountHist.setAccountId(posting.getAccount());
			accountHist.setPostDate(posting.getPostDate());
		} else {
			accountHist = accountHistMap.get(accountHistKey);
		}

		if (posting.isShadowPosting()) {
			if (StringUtils.equals(posting.getDrOrCr(), "D")) {
				accountHist.setShadowBal(accountHist.getShadowBal().subtract(posting.getPostAmount()));
			} else {
				accountHist.setShadowBal(accountHist.getShadowBal().add(posting.getPostAmount()));
			}
		} else {
			if (StringUtils.equals(posting.getDrOrCr(), "D")) {
				accountHist.setTodayDebits(accountHist.getTodayDebits().subtract(posting.getPostAmount()));
				accountHist.setAcBalance(accountHist.getAcBalance().subtract(posting.getPostAmount()));
			} else {
				accountHist.setTodayCredits(accountHist.getTodayCredits().add(posting.getPostAmount()));
				accountHist.setAcBalance(accountHist.getAcBalance().add(posting.getPostAmount()));
			}

			accountHist.setTodayNet(accountHist.getTodayDebits().add(accountHist.getTodayCredits()));

		}

		accountHistMap.put(accountHistKey, accountHist);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setAccountsDAO(AccountsDAO accountsDAO) {
		this.accountsDAO = accountsDAO;
	}

	public AccountsDAO getAccountsDAO() {
		return accountsDAO;
	}

	public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		this.accountTypeDAO = accountTypeDAO;
	}

	public AccountTypeDAO getAccountTypeDAO() {
		return accountTypeDAO;
	}

	public AccountsHistoryDAO getAccountsHistoryDAO() {
		return accountsHistoryDAO;
	}

	public void setAccountsHistoryDAO(AccountsHistoryDAO accountsHistoryDAO) {
		this.accountsHistoryDAO = accountsHistoryDAO;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
