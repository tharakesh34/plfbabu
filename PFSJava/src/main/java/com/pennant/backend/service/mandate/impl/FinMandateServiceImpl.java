/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinMandateServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-10-2016    														*
 *                                                                  						*
 * Modified Date    :  26-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-10-2016       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.backend.service.mandate.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.MandateCheckDigitDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatus;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.MandateProcess;

/**
 * Service implementation for methods that depends on <b>FinMandate</b>.<br>
 * 
 */
public class FinMandateServiceImpl implements FinMandateService {
	private static final Logger	logger	= Logger.getLogger(FinMandateServiceImpl.class);

	private AuditHeaderDAO		auditHeaderDAO;
	private MandateDAO			mandateDAO;
	private MandateStatusDAO	mandateStatusDAO;
	private FinanceMainDAO		financeMainDAO;
	private BankBranchService	bankBranchService;

	@Autowired
	private DocumentManagerDAO documentManagerDAO;

	@Autowired
	private MandateProcess mandateProcess;
	private MandateCheckDigitDAO  mandateCheckDigitDAO;
	private BankBranchDAO bankBranchDAO;

	public FinMandateServiceImpl() {
		super();
	}

	@Override
	public Mandate getMnadateByID(long mandateID) {
		Mandate mandate = mandateDAO.getMandateById(mandateID, "_View");
		if (mandate != null) {
			DocumentManager data = documentManagerDAO.getById(mandate.getDocumentRef());
			if (data != null) {
				mandate.setDocImage(data.getDocImage());
			}
		}

		return mandate;
	}

	@Override
	public List<Mandate> getMnadateByCustID(long custID, long mandateID) {
		return mandateDAO.getMnadateByCustID(custID, mandateID);
	}

	@Override
	public void saveOrUpdate(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType) {
		logger.debug(" Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
		Mandate mandate = financeDetail.getMandate();
		boolean isMandateReq = checkRepayMethod(finmain);
		if (isMandateReq) {
			if (mandate != null) {
				mandate.setCustID(finmain.getCustID());
				Mandate useExisting = checkExistingMandate(mandate.getMandateID());

				if (useExisting != null) {
					deleteMandate(finmain.getFinReference(), auditDetails);
					//set mandate id to finance
					finmain.setMandateID(mandate.getMandateID());
				} else {
					//check in flow table for new or old record 
					Mandate oldmandate = mandateDAO.getMandateByOrgReference(finmain.getFinReference(),
							MandateConstants.STATUS_FIN, tableType);

					if (oldmandate != null) {
						mandate.setMandateID(oldmandate.getMandateID());
						mandate.setOrgReference(finmain.getFinReference());
						mandate.setStatus(MandateConstants.STATUS_FIN);
						getDocument(mandate);
						mandateDAO.updateFinMandate(mandate, tableType);
						auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_UPD));
					} else {
						mandate.setStatus(MandateConstants.STATUS_FIN);
						mandate.setOrgReference(finmain.getFinReference());
						getDocument(mandate);
						long mandateID = mandateDAO.save(mandate, tableType);
						finmain.setMandateID(mandateID);
						auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_ADD));
					}
				}
			}
		} else {
			deleteMandate(finmain.getFinReference(), auditDetails);
			finmain.setMandateID(0);
		}
		addAudit(auditHeader, auditDetails);
		logger.debug(" Leaving ");
	}

	@Override
	public void doApprove(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType) {
		logger.debug(" Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
		List<CustomerEMail> customer  = new ArrayList<>();
		if (financeDetail.getCustomerDetails() != null) {
			customer = financeDetail.getCustomerDetails().getCustomerEMailList();
		}
		
		Mandate mandate = financeDetail.getMandate();

		boolean isMandateReq = checkRepayMethod(finmain);
		if (isMandateReq) {
			if (mandate != null) {
				mandate.setCustID(finmain.getCustID());
				Mandate useExisting = checkExistingMandate(mandate.getMandateID());
				if (useExisting != null) {
					//set mandate id to finance
					finmain.setMandateID(mandate.getMandateID());
					if (StringUtils.isEmpty(useExisting.getOrgReference())) {
						mandateDAO.updateOrgReferecne(mandate.getMandateID(), finmain.getFinReference(), "");
					}
				} else {
					mandate.setOrgReference(finmain.getFinReference());
					mandate.setStatus(MandateConstants.STATUS_NEW);
					mandate.setRecordType("");
					mandate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					getDocument(mandate);
					long mandateID = mandateDAO.save(mandate, tableType);
					finmain.setMandateID(mandateID);
					auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_ADD));
					MandateStatus mandateStatus = new MandateStatus();
					mandateStatus.setMandateID(mandate.getMandateID());
					mandateStatus.setStatus(mandate.getStatus());
					mandateStatus.setReason(mandate.getReason());
					mandateStatus.setChangeDate(DateUtility.getAppDate());
					mandateStatusDAO.save(mandateStatus, "");
					
					try {
						BigDecimal maxlimt = PennantApplicationUtil.formateAmount(mandate.getMaxLimit(),
								CurrencyUtil.getFormat(mandate.getMandateCcy()));
						mandate.setAmountInWords(NumberToEnglishWords.getNumberToWords(maxlimt.toBigInteger()));
						mandate.setDiDate(finmain.getFinStartDate());
						mandate.setFinType(finmain.getFinType());
						mandate.setAppFormNo(finmain.getApplicationNo());
						mandate.setLoanBranch(finmain.getLovDescFinBranchName());
						
						if (StringUtils.equals(finmain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API) && auditHeader.getApiHeader()!=null) {
							BankBranch bankBranch = bankBranchDAO.getBankBrachByMicr(mandate.getMICR(), "");
							mandate.setBankName(bankBranch.getBankName());
							mandate.setBranchDesc(bankBranch.getBranchDesc());
							mandate.setApprovalID(String.valueOf(mandate.getUserDetails().getUserId()));
						}
						
						
						for (CustomerEMail customerEMail : customer) {
							if (customerEMail.getCustEMailPriority() == 5) {
								mandate.setEmailId(customerEMail.getCustEMail());
							}
						}

						boolean register = mandateProcess.registerMandate(mandate);
						if (register) {
							mandate.setStatus(MandateConstants.STATUS_INPROCESS);
							mandateDAO.updateStatusAfterRegistration(mandate.getMandateID(),
									MandateConstants.STATUS_INPROCESS);
							mandateStatus.setMandateID(mandate.getMandateID());
							mandateStatus.setStatus(mandate.getStatus());
							mandateStatus.setReason(mandate.getReason());
							mandateStatus.setChangeDate(mandate.getInputDate());
							mandateStatusDAO.save(mandateStatus, "");
						}
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
					}
				}
				
			}
		} else {
			finmain.setMandateID(0);
		}

		deleteMandate(finmain.getFinReference(), auditDetails);
		addAudit(auditHeader, auditDetails);
		logger.debug(" Leaving ");
	}

	@Override
	public void doRejct(FinanceDetail financeDetail, AuditHeader auditHeader) {
		logger.debug(" Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
		Mandate mandate = financeDetail.getMandate();
		if (mandate != null) {
			Mandate useExisting = checkExistingMandate(mandate.getMandateID());
			if (useExisting == null) {
				if (mandate.getBankBranchID() != 0 && mandate.getBankBranchID() != Long.MIN_VALUE) {
					mandate.setRecordType("");
					mandate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					mandate.setStatus(MandateConstants.STATUS_REJECTED);
					mandate.setReason(Labels.getLabel("Mandate_Rejected_In_Loan"));
					long mandateID = mandateDAO.save(mandate, "");
					finmain.setMandateID(mandateID);
					auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_ADD));

					MandateStatus mandateStatus = new MandateStatus();
					mandateStatus.setMandateID(mandate.getMandateID());
					mandateStatus.setStatus(mandate.getStatus());
					mandateStatus.setReason(Labels.getLabel("Mandate_Rejected_In_Loan"));
					mandateStatus.setChangeDate(DateUtility.getAppDate());
					mandateStatusDAO.save(mandateStatus, "");
				}

			}
		}
		deleteMandate(finmain.getFinReference(), auditDetails);
		addAudit(auditHeader, auditDetails);
		logger.debug(" Leaving ");
	}

	private void deleteMandate(String finreferece, List<AuditDetail> auditDetails) {
		//Check in temporary queue to get to know that the previous mandate is initiated from loan 
		Mandate oldmandate = mandateDAO.getMandateByOrgReference(finreferece, MandateConstants.STATUS_FIN, "_Temp");
		if (oldmandate != null) {
			//if found delete from temporary
			mandateDAO.delete(oldmandate, "_Temp");
			auditDetails.add(getAuditDetails(oldmandate, 2, PennantConstants.TRAN_DEL));
		}

	}

	private boolean checkRepayMethod(FinanceMain finmain) {
		String rpymentod = StringUtils.trimToEmpty(finmain.getFinRepayMethod());
		if (rpymentod.equals(MandateConstants.TYPE_ECS) || rpymentod.equals(MandateConstants.TYPE_DDM)
				|| rpymentod.equals(MandateConstants.TYPE_NACH)) {
			return true;
		} else {
			return false;
		}
	}

	private Mandate checkExistingMandate(long mandateID) {
		return mandateDAO.getMandateById(mandateID, "");
	}

	private void addAudit(AuditHeader auditHeader, List<AuditDetail> auditDetails) {
		//Add audit if any changes
		if (auditDetails.isEmpty()) {
			return;
		}
		AuditHeader header = getAuditHeader(auditHeader);
		header.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(header);
	}

	/**
	 * Validate the mandate assigned to the finance.
	 * 
	 * @param auditDetail
	 * @param financeDetail
	 * @param financeMain
	 */
	public void validateMandate(AuditDetail auditDetail, FinanceDetail financeDetail) {
		Mandate mandate = financeDetail.getMandate();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		if (checkRepayMethod(financeMain) && !financeDetail.isActionSave() && mandate != null) {

			BigDecimal exposure = BigDecimal.ZERO;

			Date firstRepayDate = null;

			for (FinanceScheduleDetail schedule : financeDetail.getFinScheduleData().getFinanceScheduleDetails()) {
				
				if (exposure.compareTo(schedule.getRepayAmount()) < 0) {
					exposure = schedule.getRepayAmount();
				}
				
				if ((schedule.isRepayOnSchDate() || schedule.isPftOnSchDate()) && !isHoliday(schedule.getBpiOrHoliday())) {
					if (schedule.getSchDate().compareTo(financeMain.getFinStartDate()) > 0 && firstRepayDate == null) {
						firstRepayDate = schedule.getSchDate();
					}
				}
			}

			if (mandate.getMaxLimit() != null && mandate.getMaxLimit().compareTo(BigDecimal.ZERO) > 0) {
				if (mandate.isUseExisting()) {
					exposure = exposure.add(getFinanceMainDAO().getTotalMaxRepayAmount(mandate.getMandateID(),
							financeMain.getFinReference()));
				}

				if (mandate.getMaxLimit().compareTo(exposure) < 0) {
					auditDetail.setErrorDetail(90320);
				}	

			}
			
			//Mandate start date {0} should be before first repayments date {1}.
			if (mandate.getStartDate()!=null && firstRepayDate!=null && firstRepayDate.compareTo(mandate.getStartDate())<0) {
				String[] errParmFrq = new String[2];
				errParmFrq[0] = DateUtility.formatToShortDate(mandate.getStartDate());
				errParmFrq[1] = DateUtility.formatToShortDate(firstRepayDate);

				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "65020", errParmFrq, null), ""));
				
			}

			if (StringUtils.isNotBlank(mandate.getPeriodicity())) {

				if (!validatePayFrequency(financeMain.getRepayFrq().charAt(0), mandate.getPeriodicity().charAt(0))) {

					String[] errParmFrq = new String[2];
					errParmFrq[0] = PennantJavaUtil.getLabel("label_MandateDialog_Periodicity.value");
					errParmFrq[1] = PennantJavaUtil.getLabel("label_FinanceMainDialog_RepayFrq.value");

					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "90220", errParmFrq, null), ""));
				}

				if (financeMain.isFinRepayPftOnFrq()) {
					if (!validatePayFrequency(financeMain.getRepayPftFrq().charAt(0),
							mandate.getPeriodicity().charAt(0))) {

						String[] errParmFrq = new String[2];
						errParmFrq[0] = PennantJavaUtil.getLabel("label_MandateDialog_Periodicity.value");
						errParmFrq[1] = PennantJavaUtil.getLabel("label_FinanceMainDialog_RepayPftFrq.value");

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "90220", errParmFrq, null), ""));

					}
				}
			}

			//If mandate expiry date before fin maturity date--vaidate 
			if (mandate.getExpiryDate() != null && mandate.getExpiryDate()
					.before(financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate())) {

				String[] errParmFrq = new String[2];
				errParmFrq[0] = PennantJavaUtil.getLabel("tab_label_MANDATE") + " "
						+ PennantJavaUtil.getLabel("label_MandateDialog_ExpiryDate.value");
				errParmFrq[1] = PennantJavaUtil.getLabel("label_MaturityDate");

				auditDetail.setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30509", errParmFrq, null), ""));
			}
		}
		if (mandate != null) {
			String barCode = mandate.getBarCodeNumber();
			if (StringUtils.isNotEmpty(barCode)
					&& !StringUtils.trimToEmpty(mandate.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
				String[] errParm1 = new String[1];
				String[] valueParm1 = new String[1];
				valueParm1[0] = barCode;
				errParm1[0] = PennantJavaUtil.getLabel("label_BarCodeNumber") + " : " + valueParm1[0];

				char lastchar = (char) barCode.charAt(barCode.length() - 1);
				int reminder = checkSum(barCode);

				MandateCheckDigit checkDigit = mandateCheckDigitDAO.getMandateCheckDigit(reminder, "");
				//Validation For BarCode CheckSum
				if (checkDigit != null) {
					if (checkDigit.getLookUpValue().charAt(0) != lastchar) {

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "90405", errParm1, valueParm1), null));
					}
				} else {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "90405", errParm1, valueParm1), null));
				}

				/*//BarCode Unique Validation
				int count = mandateDAO.getBarCodeCount(barCode, mandate.getMandateID(), "_View");
				if (count > 0) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm1, valueParm1), null));
				}*/
			}
		}
	
		
		
	}

	private  boolean isHoliday(String bpiOrHoliday) {
		if (StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_HOLIDAY)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_POSTPONE)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_UNPLANNED)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public void promptMandate(AuditDetail auditDetail, FinanceDetail financeDetail) {
		Mandate mandate = financeDetail.getMandate();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		if (checkRepayMethod(financeMain) && !financeDetail.isActionSave() && mandate != null) {
			if (!mandate.isUseExisting()) {
				// prompt for Open Mandate
				int count = getMnadateByCustID(mandate.getCustID(), mandate.getMandateID()).size();
				if (count != 0) {
					String[] errParmMan = new String[2];
					String[] valueParmMan = new String[2];
					valueParmMan[0] = String.valueOf(mandate.getCustCIF());
					valueParmMan[1] = String.valueOf(count);

					errParmMan[0] = " CustCIF : " + valueParmMan[0];
					errParmMan[1] = valueParmMan[1];

					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "65013", errParmMan, valueParmMan), ""));
				}
				
				if (mandate.getBankBranchID() != 0) {
					BankBranch bankBranch = bankBranchService.getBankBranchById(mandate.getBankBranchID());

					String mandateType = StringUtils.trimToEmpty(mandate.getMandateType());
					// prompt for Auto Debit
					if ((MandateConstants.TYPE_ECS.equals(mandateType)
							|| MandateConstants.TYPE_NACH.equals(mandateType)) && bankBranch.isDda()) {

						String[] errParmBranch = new String[1];
						String[] valueParmBranch = new String[1];
						valueParmBranch[0] = bankBranch.getBankName();
						errParmBranch[0] = valueParmBranch[0];

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "65016", errParmBranch, valueParmBranch),
								""));

					} else if (StringUtils.equals(mandateType, MandateConstants.TYPE_ECS) && bankBranch.isNach()) {

						String[] errParmBranch = new String[1];
						String[] valueParmBranch = new String[1];
						valueParmBranch[0] = bankBranch.getBankName();
						errParmBranch[0] = valueParmBranch[0];

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "65017", errParmBranch, valueParmBranch),
								""));
					}
				}
			}
		}
	}

	private Boolean validatePayFrequency(char repayFrq, char mandateFrq) {
		boolean valFrq = true;
		if (repayFrq == mandateFrq) {
			valFrq = true;
		} else {
			switch (repayFrq) {
			case 'D':
				if (mandateFrq != 'D') {
					valFrq = false;
				}
				break;
			case 'W':
				if (mandateFrq != 'D') {
					valFrq = false;
				}
				break;
			case 'X':
				if (mandateFrq != 'D' || mandateFrq != 'W') {
					valFrq = false;
				}
				break;
			case 'F':
				if (mandateFrq != 'D' || mandateFrq != 'W' || mandateFrq != 'X') {
					valFrq = false;
				}
				break;
			case 'M':
				if (mandateFrq == 'B' || mandateFrq == 'Q' || mandateFrq == 'H' || mandateFrq == 'Y') {
					valFrq = false;
				}
				break;
			case 'B':
				if (mandateFrq == 'Q' || mandateFrq == 'H' || mandateFrq == 'Y') {
					valFrq = false;
				}
				break;

			case 'Q':
				if (mandateFrq == 'H' || mandateFrq == 'Y') {
					valFrq = false;
				}
				break;
			case 'H':
				if (mandateFrq == 'Y') {
					valFrq = false;
				}
				break;
			}
		}
		return valFrq;
	}

	public AuditHeader getAuditHeader(AuditHeader auditHeader) {
		AuditHeader newauditHeader = new AuditHeader();
		newauditHeader.setAuditModule(ModuleUtil.getTableName(Mandate.class.getSimpleName()));
		newauditHeader.setAuditReference(auditHeader.getAuditReference());
		newauditHeader.setAuditUsrId(auditHeader.getAuditUsrId());
		newauditHeader.setAuditBranchCode(auditHeader.getAuditBranchCode());
		newauditHeader.setAuditDeptCode(auditHeader.getAuditDeptCode());
		newauditHeader.setAuditSystemIP(auditHeader.getAuditSystemIP());
		newauditHeader.setAuditSessionID(auditHeader.getAuditSessionID());
		newauditHeader.setUsrLanguage(auditHeader.getUsrLanguage());
		return newauditHeader;
	}

	private void getDocument(Mandate mandate) {
		DocumentManager documentManager = new DocumentManager();
		if (mandate.getDocumentRef() != 0 && !mandate.isNewRecord()) {
			DocumentManager olddocumentManager = documentManagerDAO.getById(mandate.getDocumentRef());
			if(olddocumentManager != null) {
				byte[] arr1 = olddocumentManager.getDocImage();
				byte[] arr2 = mandate.getDocImage();
				if (!Arrays.equals(arr1, arr2)) {
					documentManager.setDocImage(arr2);
					mandate.setDocumentRef(documentManagerDAO.save(documentManager));
				}
			}
		} else {
			documentManager.setDocImage(mandate.getDocImage());
			mandate.setDocumentRef(documentManagerDAO.save(documentManager));
		}
	}

	public AuditDetail getAuditDetails(Mandate mandate, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new Mandate(), new Mandate().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], mandate.getBefImage(), mandate);
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public void setMandateStatusDAO(MandateStatusDAO mandateStatusDAO) {
		this.mandateStatusDAO = mandateStatusDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public BankBranchService getBankBranchService() {
		return bankBranchService;
	}

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}
	
	private int checkSum(String barCode) {
		int value = 0;
		for (int i = 0; i < barCode.length() - 1; i++) {
			value += Integer.parseInt(barCode.charAt(i) + "");
		}
		PFSParameter parameter = SysParamUtil.getSystemParameterObject("BARCODE_DIVISOR");
		int divisor = Integer.parseInt(parameter.getSysParmValue().trim());
		int remainder = value % divisor;

		return remainder;
	}
	public MandateCheckDigitDAO getMandateCheckDigitDAO() {
		return mandateCheckDigitDAO;
	}

	public void setMandateCheckDigitDAO(MandateCheckDigitDAO mandateCheckDigitDAO) {
		this.mandateCheckDigitDAO = mandateCheckDigitDAO;
	}

	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}


}