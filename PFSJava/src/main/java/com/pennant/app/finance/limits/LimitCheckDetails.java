package com.pennant.app.finance.limits;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.zkoss.util.resource.Labels;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.util.MailUtil;
import com.pennant.backend.dao.limits.LimitInterfaceDAO;
import com.pennant.backend.dao.notifications.NotificationsDAO;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limits.FinanceLimitProcess;
import com.pennant.backend.model.limits.LimitDetail;
import com.pennant.backend.model.limits.LimitUtilization;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.constants.InterfaceConstants;
import com.pennanttech.pennapps.core.InterfaceException;

public class LimitCheckDetails {

	private static final Logger logger = Logger.getLogger(LimitCheckDetails.class);

	private DedupParmService 				dedupParmService;
	private CustomerLimitIntefaceService 	customerLimitIntefaceService;
	private LimitInterfaceDAO 				limitInterfaceDAO;
	private MailUtil 						mailUtil;
	private NotificationsDAO 				notificationsDAO;

	public LimitCheckDetails() {
		super();
	}

	private String[] errorCodes = {"9999"};
	
	
	/**
	 * send DealOnlineRequest to ACP interface
	 * from the response of "DealOnlineRequest" we do the following operations
	 * 1. GO --- send "doReserveUtilization" Request to ACP interface
	 * 2. NOGO-- send "doOverrideAndReserveUtil" Request to ACP interface
	 * 
	 */
	public boolean limitServiceProcess(FinanceDetail aFinanceDetail) throws InterfaceException, InterruptedException{

		LimitUtilization limitUtilReply = null;
		FinanceMain financeMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// prepare LimitUtilization object
		try {
			LimitUtilization limitUtilReq = new LimitUtilization();

			limitUtilReq = prepareLimitUtilReq(financeMain, FinanceConstants.PREDEAL_CHECK);

			limitUtilReply = doPredealCheck(aFinanceDetail, limitUtilReq);

			if(limitUtilReply != null && StringUtils.equals(limitUtilReply.getReturnCode(), PennantConstants.RES_TYPE_SUCCESS)) {
				return true;
			} else {
				return false;
			}
		} catch (InterfaceException e) {
			throw e;
		}

	}


	/**
	 * Method for checking the Limit Details 
	 * 
	 * @param role
	 * @param finType
	 * @return
	 */
	public List<FinanceReferenceDetail> doLimitChek(String role, String finType) {
		logger.debug("Entering");

		FinanceReferenceDetail financeRefDetail = new FinanceReferenceDetail();
		financeRefDetail.setMandInputInStage(role + ",");
		financeRefDetail.setFinType(finType);
		List<FinanceReferenceDetail> queryCodeList = getDedupParmService().getQueryCodeList(financeRefDetail,"_ALDView");

		if(queryCodeList == null || queryCodeList.isEmpty()) {
			return new ArrayList<FinanceReferenceDetail>();
		}

		logger.debug("Leaving");

		return queryCodeList;
	}

	/**
	 * Method for fetching the Limit Details
	 * 
	 * @param limitRef
	 * @param branchCode
	 * @throws InterfaceException 
	 */
	public LimitDetail getLimitDetails(String limitRef, String branchCode) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getCustomerLimitIntefaceService().getLimitDetail(limitRef, branchCode);
	}

	/**
	 * Method for sending Deal Online Request to ACP Interface
	 * @param aFinanceDetail 
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException 
	 * @throws InterruptedException
	 * @throws JaxenException 
	 */
	public LimitUtilization doPredealCheck(FinanceDetail aFinanceDetail, LimitUtilization limitUtilReq) 
			throws InterfaceException, InterruptedException {
		logger.debug("Entering");

		// checking for whether Predeal check Request already sent or not 
		LimitUtilization limitUtil = doValidation(limitUtilReq);
		
		if (limitUtil != null) {
			// checking for financeAmount is changed or not
			BigDecimal prevDealAmt = PennantApplicationUtil.unFormateAmount(limitUtil.getDealAmount(), 0);
			BigDecimal currentDealAmt = PennantApplicationUtil.unFormateAmount(limitUtilReq.getDealAmount(), 0);

			if (prevDealAmt.compareTo(currentDealAmt) != 0) {
				// Send predeal check request
				limitUtil = doLimitProcess(aFinanceDetail.getFinScheduleData().getFinanceMain(), FinanceConstants.PREDEAL_CHECK);
			} else {
				limitUtilReq.setNewRecord(false);
				limitUtil = doReserveProcess(aFinanceDetail.getFinScheduleData().getFinanceMain(), limitUtil);
			}
		} else {
			limitUtil = doLimitProcess(aFinanceDetail.getFinScheduleData().getFinanceMain(), FinanceConstants.PREDEAL_CHECK);
		}

		logger.debug("Leaving");
		return limitUtil;
	}

	/**
	 * Method for send Reserve or Override_Reserve Request to ACP Interface
	 * @param aFinanceDetail 
	 * 
	 * @param limitUtilReq
	 * @return
	 * @throws InterfaceException
	 */
	private LimitUtilization doReserveProcess(FinanceMain financeMain, LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");

		if(financeMain == null) {
			return null;
		}

		LimitUtilization limitUtilRply;
		limitUtilRply = null;
		try {
			if(StringUtils.equals(limitUtilReq.getReturnText(), FinanceConstants.LIMIT_GO)) {
				
				limitUtilReq = prepareLimitUtilReq(financeMain, FinanceConstants.RESERVE);
				limitUtilRply = doReserveUtilization(limitUtilReq);

				if(limitUtilRply == null) {
					return null;
				}

				limitUtilRply.setDealType(FinanceConstants.RESERVE);
				limitUtilRply.setNewRecord(limitUtilReq.isNewRecord());
				
				// save the Reserve or OverrideReserve response
				if(limitUtilRply.isNewRecord()) {
					getCustomerLimitIntefaceService().saveFinLimitUtil(getFinanceLimitProcess(limitUtilRply));
				}
				
			} else if(StringUtils.equals(limitUtilReq.getReturnText(), FinanceConstants.LIMIT_NOGO)) {
				
				// Sent mail to RM
				sendMailNotification(limitUtilReq);
			}
		} catch (InterfaceException pfe) {
			throw pfe;
		}
		logger.debug("Leaving");
		return limitUtilRply;
	}

	/**
	 * Method for prepare Limit Utilization object to send request
	 * 
	 * @param financeMain
	 * @param lmtActType
	 * @return
	 */
	private LimitUtilization prepareLimitUtilReq(FinanceMain financeMain, String lmtActType) {
		logger.debug("Entering");

		LimitUtilization limitUtilReq = new LimitUtilization();

		limitUtilReq.setDealID(financeMain.getFinReference());
		limitUtilReq.setDealType(lmtActType);
		limitUtilReq.setCustomerReference(financeMain.getLovDescCustCIF());
		limitUtilReq.setLimitRef(financeMain.getFinLimitRef());
		//limitUtilReq.setUserID(String.valueOf(financeMain.getUserDetails().getLoginUsrID()));
		limitUtilReq.setUserID("Test");
		limitUtilReq.setDealAmount(financeMain.getFinAmount());
		limitUtilReq.setDealCcy(financeMain.getFinCcy());
		limitUtilReq.setDealExpiry(financeMain.getMaturityDate());
		limitUtilReq.setBranchCode(financeMain.getFinBranch());

		logger.debug("Leaving");
		return limitUtilReq;
	}

	/**
	 * Method for process limits based on interface constant<br>
	 * 
	 * Interface constants<br>
	 * 1. RESERVE
	 * 2. CONFIRM
	 * 3. CANCEL_RESERVE
	 * 4. CANCEL_UTILIZATION
	 * 5. AMENDEMENT
	 * 
	 * @param financeMain
	 * @param lmtActionType
	 * @param intLimitType(Interface limit process constant)
	 * @throws InterfaceException 
	 */
	public void doProcessLimits(FinanceMain financeMain, String intLimitType) throws InterfaceException {
		logger.debug("Entering");
		if(!StringUtils.isBlank(financeMain.getFinLimitRef())) {
			doLimitProcess(financeMain, intLimitType);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for sending request to middleware to process limits
	 * 
	 * @param financeMain
	 * @param intLimitType
	 * @throws InterfaceException
	 */
	private LimitUtilization doLimitProcess(FinanceMain financeMain, String intLimitType) throws InterfaceException {
		logger.debug("Entering");

		LimitUtilization limitUtilRply = null;
		try {
			if(StringUtils.equals(intLimitType, FinanceConstants.PREDEAL_CHECK)) {// Predeal check

				LimitUtilization limitUtilReq = prepareLimitUtilReq(financeMain, FinanceConstants.PREDEAL_CHECK);

				limitUtilRply = doPredealCheck(limitUtilReq);

				if(!StringUtils.equals(limitUtilRply.getReturnCode(), InterfaceConstants.SUCCESS_CODE)) {
					sendMailNotification(limitUtilRply);
				}

				limitUtilRply.setDealType(FinanceConstants.PREDEAL_CHECK);
				limitUtilRply.setDealAmount(financeMain.getFinAmount());
				getLimitInterfaceDAO().saveFinLimitUtil(getFinanceLimitProcess(limitUtilRply));

				limitUtilRply = doReserveProcess(financeMain, limitUtilRply);

			} else if(StringUtils.equals(intLimitType, FinanceConstants.CONFIRM)) {// Confirm Reservation

				LimitUtilization limitUtilReq = validateLimitUtilReq(financeMain, FinanceConstants.CONFIRM);
				if(limitUtilReq != null) {
					limitUtilRply = doConfirmReservation(limitUtilReq);

					if(!StringUtils.equals(limitUtilRply.getReturnCode(), InterfaceConstants.SUCCESS_CODE)) {
						sendMailNotification(limitUtilRply);
					}

					limitUtilRply.setDealType(FinanceConstants.CONFIRM);
					getLimitInterfaceDAO().saveFinLimitUtil(getFinanceLimitProcess(limitUtilRply));

				}

			} else if(StringUtils.equals(intLimitType, FinanceConstants.CANCEL_RESERVE)){

				LimitUtilization limitUtilReq = validateLimitUtilReq(financeMain, FinanceConstants.CANCEL_RESERVE);

				if(limitUtilReq != null) {
					limitUtilRply = doCancelReservation(limitUtilReq);

					if(!StringUtils.equals(limitUtilRply.getReturnCode(), InterfaceConstants.SUCCESS_CODE)) {
						sendMailNotification(limitUtilRply);
					}

					limitUtilRply.setDealType(FinanceConstants.CANCEL_RESERVE);
					getLimitInterfaceDAO().saveFinLimitUtil(getFinanceLimitProcess(limitUtilRply));
				}
			} else if(StringUtils.equals(intLimitType, FinanceConstants.CANCEL_UTILIZATION)){

				LimitUtilization limitUtilReq = validateLimitUtilReq(financeMain, FinanceConstants.CANCEL_UTILIZATION);

				if(limitUtilReq != null) {
					limitUtilRply = doCancelUtilization(limitUtilReq);
					limitUtilRply.setDealType(FinanceConstants.CANCEL_UTILIZATION);

					if(!StringUtils.equals(limitUtilRply.getReturnCode(), InterfaceConstants.SUCCESS_CODE)) {
						sendMailNotification(limitUtilRply);
					}

					getLimitInterfaceDAO().saveFinLimitUtil(getFinanceLimitProcess(limitUtilRply));

				}
			} else if(StringUtils.equals(intLimitType, FinanceConstants.AMENDEMENT)){

				LimitUtilization limitUtilReq = validateLimitUtilReq(financeMain, FinanceConstants.AMENDEMENT);

				if(limitUtilReq != null) {
					limitUtilRply = doLimitAmendment(limitUtilReq);

					limitUtilRply.setDealType(FinanceConstants.AMENDEMENT);
					getLimitInterfaceDAO().saveFinLimitUtil(getFinanceLimitProcess(limitUtilRply));

				}
			}
		} catch(InterfaceException pfe) {
			throw pfe;
		}
		logger.debug("Leaving");
		return limitUtilRply;
	}

	/**
	 * Method for sending Reserve Utilization Request to ACP Interface
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException 
	 * @throws JaxenException 
	 */
	public LimitUtilization doReserveUtilization(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");
		LimitUtilization limitUtilization = doValidation(limitUtilReq);

		if(limitUtilization != null) {
			limitUtilization.setNewRecord(false);
			return limitUtilization;
		}

		try {
			limitUtilization = getCustomerLimitIntefaceService().doReserveUtilization(limitUtilReq);
			
			if(!StringUtils.equals(limitUtilization.getReturnCode(), InterfaceConstants.SUCCESS_CODE)) {
				sendMailNotification(limitUtilization);
			}
			
		} catch(InterfaceException pfe) {
			throw pfe;
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		
		logger.debug("Leaving");
		
		return limitUtilization;
	}

	/**
	 * Method for send mail notification when customer limit processing is failed
	 * 
	 * @param limitUtilization
	 * @throws InterfaceException
	 */
	private void sendMailNotification(LimitUtilization limitUtilization) throws InterfaceException {
		logger.debug("Entering");

		for(String code: errorCodes) {
			if(!StringUtils.equals(limitUtilization.getReturnCode(), code)) {
				continue;
			} else {
				List<Long> notificationIdlist = getNotificationsDAO().getTemplateIds("");//TODO:TemplateCode
				String tableType = "_Temp";
				if(StringUtils.equals(limitUtilization.getDealType(), FinanceConstants.CANCEL_UTILIZATION)) {
					tableType = "";
				}
				FinanceMain financeMain = getLimitInterfaceDAO().getFinanceMainByRef(limitUtilization.getDealID(), tableType, false);

				boolean isMailSent;
				try {
					isMailSent = getMailUtil().sendMail(notificationIdlist, financeMain);
				} catch (Exception e) {
					logger.error("Exception: ", e);
					throw new InterfaceException("PTILMT1", e.getMessage());
				}

				if(isMailSent) {
					String errorMsg = Labels.getLabel("MESSAGE_MAIL_NOTIFICATION");
					throw new InterfaceException(limitUtilization.getReturnCode(), errorMsg);
				}
			}
		}

		logger.debug("Leaving");
	}

	
	public LimitUtilization doPredealCheck(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getCustomerLimitIntefaceService().doPredealCheck(limitUtilReq);
	}
	
	/**
	 * Method for sending Override AND Reserve Request to ACP Interface
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException 
	 * @throws JaxenException 
	 */
	public LimitUtilization doOverrideAndReserveUtil(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");

		LimitUtilization limitUtilization = doValidation(limitUtilReq);

		if(limitUtilization != null) {
			limitUtilization.setNewRecord(false);
			return limitUtilization;
		}

		logger.debug("Leaving");
		return getCustomerLimitIntefaceService().doOverrideAndReserveUtil(limitUtilReq);
	}

	/**
	 * Method for sending Confirm Reservation Request to ACP Interface
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException 
	 * @throws JaxenException 
	 */
	public LimitUtilization doConfirmReservation(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getCustomerLimitIntefaceService().doConfirmReservation(limitUtilReq);
	}

	/**
	 * Method for sending Cancel Reservation Request to ACP Interface
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException 
	 * @throws JaxenException 
	 */
	public LimitUtilization doCancelReservation(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getCustomerLimitIntefaceService().doCancelReservation(limitUtilReq);
	}

	/**
	 * Method for sending Cancel Utilization Request to ACP Interface
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException 
	 * @throws JaxenException 
	 */
	public LimitUtilization doCancelUtilization(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getCustomerLimitIntefaceService().doCancelUtilization(limitUtilReq);
	}

	/**
	 * Method for sending Limit Amendment Request to ACP Interface
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException 
	 * @throws JaxenException 
	 */
	public LimitUtilization doLimitAmendment(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getCustomerLimitIntefaceService().doLimitAmendment(limitUtilReq);
	}

	/**
	 * 
	 * @param finLimitProcess
	 */
	public void saveLimitUtilDetails(FinanceLimitProcess finLimitProcess) {
		logger.debug("Entering");

		getCustomerLimitIntefaceService().saveFinLimitUtil(finLimitProcess);

		logger.debug("Leaving");
	}

	/**
	 * Method for save the Customer LimitDetails
	 * 
	 * @param limitDetail
	 */
	public void saveOrUpdate(LimitDetail limitDetail) {
		logger.debug("Entering");

		getCustomerLimitIntefaceService().saveOrUpdate(limitDetail);

		logger.debug("Leaving");
	}

	private LimitUtilization doValidation(LimitUtilization limitUtilReq) { 
		logger.debug("Entering");

		//check ReserveUtilization request already sent or not
		FinanceLimitProcess limitProcess = getCustomerLimitIntefaceService().getLimitUtilDetails(
				getFinanceLimitProcess(limitUtilReq));

		if(limitProcess == null) {
			return null;
		}
		LimitUtilization limitUtilization = new LimitUtilization();
		limitUtilization.setDealID(limitProcess.getFinReference());
		limitUtilization.setDealAmount(limitProcess.getDealAmount());
		limitUtilization.setRequestType(limitProcess.getRequestType());
		limitUtilization.setReferenceNum(limitProcess.getReferenceNum());
		limitUtilization.setCustomerReference(limitProcess.getCustCIF());
		limitUtilization.setLimitRef(limitProcess.getLimitRef());
		limitUtilization.setResponse(limitProcess.getResStatus());
		limitUtilization.setReturnCode(limitProcess.getResStatus());
		limitUtilization.setReturnText(limitProcess.getResStatus());
		limitUtilization.setErrMsg(limitProcess.getErrorMsg());
		limitUtilization.setValueDate(limitProcess.getValueDate());

		logger.debug("Leaving");
		return limitUtilization;
	}

	/**
	 * Method for fetching limit process details
	 * 
	 */
	public static FinanceLimitProcess getFinanceLimitProcess(LimitUtilization limitUtilRply) { 
		logger.debug("Entering");

		FinanceLimitProcess finLimitProcess = new FinanceLimitProcess();
		finLimitProcess.setFinReference(limitUtilRply.getDealID());
		finLimitProcess.setRequestType(limitUtilRply.getDealType());
		finLimitProcess.setReferenceNum(limitUtilRply.getReferenceNum());
		finLimitProcess.setCustCIF(limitUtilRply.getCustomerReference());
		finLimitProcess.setLimitRef(limitUtilRply.getLimitRef());
		if(StringUtils.equals(limitUtilRply.getDealType(), FinanceConstants.PREDEAL_CHECK)) {
			finLimitProcess.setResStatus(limitUtilRply.getReturnText());
			finLimitProcess.setResMessage(limitUtilRply.getMsgBreach());
		} else {
			finLimitProcess.setResStatus(limitUtilRply.getReturnCode());
			finLimitProcess.setResMessage(limitUtilRply.getReturnText());
		}
		finLimitProcess.setErrorCode(limitUtilRply.getReturnCode());
		finLimitProcess.setErrorMsg(limitUtilRply.getErrMsg());
		finLimitProcess.setValueDate(new Timestamp(System.currentTimeMillis()));
		finLimitProcess.setDealAmount(limitUtilRply.getDealAmount());

		logger.debug("Leaving");
		return finLimitProcess;
	}

	/**
	 * Method for fetching the Customer Limit Details by Limit Reference
	 * 
	 * @param limitRef
	 */
	public LimitDetail getCustomerLimitDetails(String limitRef) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getCustomerLimitIntefaceService().getCustomerLimitDetails(limitRef);

	}

	/**
	 * Method for validate Limit utilization request
	 * 
	 * @param financeMain
	 * @param lmtActType
	 */
	private LimitUtilization validateLimitUtilReq(FinanceMain financeMain, String lmtActType) {
		logger.debug("Entering");

		String reqType = FinanceConstants.RESERVE;
		if(StringUtils.equals(lmtActType, FinanceConstants.CANCEL_UTILIZATION) ||
				StringUtils.equals(lmtActType, FinanceConstants.AMENDEMENT)) {
			reqType = FinanceConstants.CONFIRM;
		}

		FinanceLimitProcess finLimitProcess = new FinanceLimitProcess();
		finLimitProcess.setFinReference(financeMain.getFinReference());
		finLimitProcess.setRequestType(reqType);
		finLimitProcess.setCustCIF(financeMain.getLovDescCustCIF());

		LimitUtilization limitUtilReq = null;
		finLimitProcess = getCustomerLimitIntefaceService().getLimitUtilDetails(finLimitProcess);
		
		if(finLimitProcess != null && StringUtils.equals(finLimitProcess.getResStatus(), PennantConstants.RES_TYPE_SUCCESS)) {

			limitUtilReq = prepareLimitUtilReq(financeMain, lmtActType);
		}

		logger.debug("Leaving");

		return limitUtilReq;
	}



	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	public CustomerLimitIntefaceService getCustomerLimitIntefaceService() {
		return customerLimitIntefaceService;
	}

	public void setCustomerLimitIntefaceService(CustomerLimitIntefaceService customerLimitIntefaceService) {
		this.customerLimitIntefaceService = customerLimitIntefaceService;
	}

	public LimitInterfaceDAO getLimitInterfaceDAO() {
		return limitInterfaceDAO;
	}

	public void setLimitInterfaceDAO(LimitInterfaceDAO limitInterfaceDAO) {
		this.limitInterfaceDAO = limitInterfaceDAO;
	}
	
	public MailUtil getMailUtil() {
		return mailUtil;
	}
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public NotificationsDAO getNotificationsDAO() {
		return notificationsDAO;
	}

	public void setNotificationsDAO(NotificationsDAO notificationsDAO) {
		this.notificationsDAO = notificationsDAO;
	}
}
