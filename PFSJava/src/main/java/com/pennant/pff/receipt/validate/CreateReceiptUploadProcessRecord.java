package com.pennant.pff.receipt.validate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.MasterDefUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.applicationmaster.RejectDetailDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.document.DocVerificationUtil;
import com.pennant.pff.document.model.DocVerificationHeader;
import com.pennant.pff.receipt.ClosureType;
import com.pennant.pff.receipt.dao.CreateReceiptUploadDAO;
import com.pennant.pff.receipt.model.CreateReceiptUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.model.UploadDetails;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.CommonHeader;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class CreateReceiptUploadProcessRecord implements ProcessRecord {
	private static final Logger logger = LogManager.getLogger(CreateReceiptUploadProcessRecord.class);

	private CreateReceiptUploadDAO createReceiptUploadDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinanceMainDAO financeMainDAO;
	private ReceiptService receiptService;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private PartnerBankDAO partnerBankDAO;
	private BounceReasonDAO bounceReasonDAO;
	private RejectDetailDAO rejectDetailDAO;
	private BankDetailDAO bankDetailDAO;

	@Autowired
	private UploadService createReceiptUploadService;

	public CreateReceiptUploadProcessRecord() {
		super();
	}

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource paramSource, Table table)
			throws Exception {
		logger.debug(Literal.ENTERING);

		Sheet sheet = attributes.getSheet();

		Row headerRow = sheet.getRow(0);

		Row row = attributes.getRow();

		CreateReceiptUpload cru = new CreateReceiptUpload();

		Long headerID = (Long) attributes.getParameterMap().get("HEADER_ID");

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");
		Long recordSeq = (Long) paramSource.getValue("RecordSeq");

		cru.setHeaderId(headerID);
		cru.setRecordSeq(recordSeq);

		int readColumn = 0;

		Cell rowCell = null;
		try {

			for (Cell cell : headerRow) {
				rowCell = row.getCell(readColumn);

				if (cell.getColumnIndex() > 27) {
					break;
				}

				readColumn = cell.getColumnIndex() + 1;
				if (rowCell == null) {
					continue;
				}

				switch (cell.getColumnIndex()) {
				case 0:
					cru.setReference(rowCell.toString());
					break;
				case 1:
					cru.setReceiptPurpose(rowCell.toString());
					break;
				case 2:
					cru.setExcessAdjustTo(rowCell.toString());
					break;
				case 3:
					cru.setAllocationType(rowCell.toString());
					break;

				case 4:
					String strAmount = rowCell.toString();
					if (strAmount != null) {
						cru.setReceiptAmount(PennantApplicationUtil.unFormateAmount(strAmount, 2));
					}
					break;
				case 5:
					String valueDate = rowCell.toString();
					if (valueDate != null) {
						cru.setValueDate(DateUtil.parse(valueDate, DateFormat.LONG_DATE.getPattern()));
					}
					break;
				case 6:
					cru.setReceiptMode(rowCell.toString());
					break;
				case 7:
					cru.setSubReceiptMode(rowCell.toString());
					break;
				case 8:
					cru.setReceiptChannel(rowCell.toString());
					break;
				case 9:
					cru.setEffectSchdMethod(rowCell.toString());
					break;
				case 10:
					cru.setClosureType(rowCell.toString());
					break;
				case 11:
					cru.setReason(rowCell.toString());
					break;
				case 12:
					cru.setRemarks(rowCell.toString());
					break;
				case 13:
					String receiptId = rowCell.toString();
					if (receiptId != null && StringUtils.isNotBlank(receiptId)) {
						cru.setReceiptID(Long.parseLong(receiptId));
					}
					break;
				case 14:
					cru.setChequeNumber(rowCell.toString());
					break;
				case 15:
					cru.setReceiptModeStatus(rowCell.toString());
					break;
				case 16:
					cru.setBankCode(rowCell.toString());
					break;
				case 17:
					cru.setChequeAccountNumber(rowCell.toString());
					break;
				case 18:
					cru.setPaymentRef(rowCell.toString());
					break;
				case 19:
					String depositDate = rowCell.toString();
					if (depositDate != null) {
						cru.setDepositDate(DateUtil.parse(depositDate, DateFormat.LONG_DATE.getPattern()));
					}
					break;
				case 20:
					String realizeDate = rowCell.toString();
					if (realizeDate != null) {
						cru.setRealizationDate(DateUtil.parse(realizeDate, DateFormat.LONG_DATE.getPattern()));
					}
					break;
				case 21:
					cru.setTransactionRef(rowCell.toString());
					break;
				case 22:
					cru.setPanNumber(rowCell.toString());
					break;
				case 23:
					cru.setReceivedFrom(rowCell.toString());
					break;
				case 24:
					String bounceDate = rowCell.toString();
					if (bounceDate != null) {
						cru.setBounceDate(DateUtil.parse(bounceDate, DateFormat.LONG_DATE.getPattern()));
					}
					break;
				case 25:
					cru.setBounceReason(rowCell.toString());
					break;
				case 26:
					cru.setBounceRemarks(rowCell.toString());
					break;
				case 27:
					cru.setPartnerBankCode(rowCell.toString());
					break;

				default:
					break;
				}
			}

			if (StringUtils.isEmpty(cru.getAllocationType())) {
				cru.setAllocationType(AllocationType.AUTO);
			}

			long uploadID = createReceiptUploadDAO.save(cru);
			cru.setId(uploadID);

			List<CreateReceiptUpload> allocations = new ArrayList<>();

			int index = 0;
			for (Cell cell : headerRow) {

				if (index < readColumn) {
					index++;
					continue;
				}

				CreateReceiptUpload alloc = new CreateReceiptUpload();

				String allocationType = cell.toString();

				if (allocationType == null) {
					break;
				}

				if (CommonHeader.isValid(allocationType)) {
					continue;
				}

				alloc.setId(uploadID);
				alloc.setHeaderId(headerID);
				alloc.setCode(allocationType.toUpperCase());

				rowCell = row.getCell(index);

				if (rowCell != null) {

					String strAmount = rowCell.toString();

					if (StringUtils.isNotEmpty(strAmount)) {
						BigDecimal str = BigDecimal.ZERO;

						try {
							str = new BigDecimal(strAmount);
						} catch (NumberFormatException e) {
							throw new AppException("Invalid Amount");
						}

						if (str.compareTo(BigDecimal.ZERO) > 0) {
							alloc.setAmount(PennantApplicationUtil.unFormateAmount(strAmount, 2));
							allocations.add(alloc);
						}
					}
				}
				index++;

				if (index == 23) {
					throw new AppException("Fee Types are exceeded the limit");
				}
			}

			createReceiptUploadDAO.saveAllocations(allocations);

			cru.setAllocations(allocations);

			validate(cru, header);

			if (cru.getProgress() == EodConstants.PROGRESS_FAILED) {
				cru.setStatus("F");
				paramSource.addValue("STATUS", cru.getStatus());
				paramSource.addValue("ERRORCODE", cru.getErrorCode());
				paramSource.addValue("ERRORDESC", cru.getErrorDesc());
			}
		} catch (AppException e) {
			cru.setStatus("F");
			cru.setProgress(EodConstants.PROGRESS_FAILED);

			paramSource.addValue("ERRORCODE", "9999");
			paramSource.addValue("ERRORDESC", e.getMessage());

			paramSource.addValue("STATUS", cru.getStatus());
			paramSource.addValue("PROGRESS", cru.getProgress());
		}

		List<CreateReceiptUpload> details = new ArrayList<>();
		details.add(cru);

		createReceiptUploadDAO.update(details);
		createReceiptUploadService.updateProcess(header, cru, paramSource);

		header.getUploadDetails().add(cru);

		logger.debug(Literal.LEAVING);
	}

	public void validate(CreateReceiptUpload rud, FileUploadHeader header) {
		prepareKeys(rud);

		String reference = rud.getReference();
		FinanceMain fm = financeMainDAO.getFinanceMain(reference, TableType.MAIN_TAB);

		if (fm == null) {
			setError(rud, "NO DATA FOUND WITH  SPECIFIED REFERENCE");
			return;
		}

		rud.setReferenceID(fm.getFinID());

		isFileExists(rud);

		if (StringUtils.isBlank(reference)) {
			setError(rud, "Blanks/Nulls in [REFERENCE] ");
			return;
		}

		String purpose = StringUtils.upperCase(rud.getReceiptPurpose());
		if (StringUtils.isBlank(purpose)) {
			setError(rud, "[RECEIPTPURPOSE] is Mandatory");
			return;
		}

		if (FinanceConstants.EARLYSETTLEMENT.equals(purpose) || FinanceConstants.PARTIALSETTLEMENT.equals(purpose)) {
			if (StringUtils.isNotBlank(createReceiptUploadDAO.getLoanReference(reference, header.getFileName()))) {
				setError(rud, "90273", "Receipt In process for " + reference);
				return;
			}
		}

		if (FinanceConstants.EARLYSETTLEMENT.equals(purpose)
				&& finAdvancePaymentsDAO.getStatusCountByFinRefrence(rud.getReferenceID()) > 0) {
			setError(rud, "90508", rud.getReference());
			return;
		}

		if (!"SP".equals(purpose) && !"EP".equals(purpose) && !"ES".equals(purpose)) {
			setError(rud, "Values other than SP/EP/ES in [RECEIPTPURPOSE] ");
			return;
		}

		String execssAdjust = rud.getExcessAdjustTo();
		if ("SP".equals(purpose)) {
			if (StringUtils.isBlank(execssAdjust)) {
				execssAdjust = "E";
			}

			if (!"E".equals(execssAdjust) && !"T".equals(execssAdjust) && !"A".equals(execssAdjust)
					&& !"S".equals(execssAdjust)) {
				setError(rud, "Values other than E/A/T/S in [EXCESSADJUSTTO] ");
				return;
			} else {
				rud.setExcessAdjustTo(execssAdjust);
			}
		}

		String allocType = rud.getAllocationType();
		if (StringUtils.isBlank(allocType)) {
			allocType = "A";
		}

		if (!"A".equals(allocType) && !"M".equals(allocType)) {
			setError(rud, "Values other than A/M in [ALLOCATIONTYPE] ");
			return;
		} else {
			rud.setAllocationType(allocType);
		}

		if ("M".equals(allocType) && !"SP".equals(rud.getReceiptPurpose())) {
			setError(rud, "Other than Schedule Payment [ALLOCATIONTYPE] should be A ");
			return;
		}

		try {
			BigDecimal precisionAmount = rud.getReceiptAmount();
			BigDecimal actualAmount = precisionAmount;

			precisionAmount = precisionAmount.setScale(0, RoundingMode.HALF_DOWN);

			if (precisionAmount.compareTo(actualAmount) != 0) {
				actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);
				rud.setReceiptAmount(actualAmount);
				setError(rud, "Minor Currency (Decimals) in [RECEIPTAMOUNT] ");
				return;
			} else {
				rud.setReceiptAmount(precisionAmount);
			}

			if (precisionAmount.compareTo(BigDecimal.ZERO) <= 0) {
				setError(rud, "[RECEIPTAMOUNT] with value <= 0");
				return;
			}
		} catch (Exception e) {
			rud.setReceiptAmount(BigDecimal.ZERO);
			setError(rud, "[RECEIPTAMOUNT] is not valid");
			return;
		}

		Date valueDate = rud.getValueDate();
		Date appDate = SysParamUtil.getAppDate();
		if (valueDate == null) {
			setError(rud, "[VALUEDATE] is Mandatory");
			return;
		}

		if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOWED_BACKDATED_RECEIPT)) {
			setError(rud, "[BACKDATED] RECEIPTS are not allowed");
			return;
		}

		if (valueDate.compareTo(appDate) > 0) {
			setError(rud, "[FUTUREDATE] RECEIPTS are not allowed");
			return;
		}

		if (StringUtils.isNotBlank(rud.getRemarks()) && rud.getRemarks().length() > 100) {
			setError(rud, "[REMARKS] with length more than 100 characters");
			return;
		}

		long receiptid = rud.getReceiptID();
		if (((receiptid != 0) && (receiptid != Long.MIN_VALUE)) && Long.toString(receiptid).length() > 50) {
			setError(rud, "[RECEIPTNUMBER] with length more than 50 characters");
			return;
		}

		String receiptMode = rud.getReceiptMode();

		if (StringUtils.isBlank(receiptMode)) {
			setError(rud, "[RECEIPTMODE] is Mandatory");
			return;
		}

		if (receiptMode.length() > 10) {
			setError(rud, "[RECEIPTMODE] with length more than 10 characters");
			return;
		}

		if (!ReceiptMode.CASH.equals(receiptMode) && !ReceiptMode.DD.equals(receiptMode)
				&& !ReceiptMode.ONLINE.equals(receiptMode) && !ReceiptMode.CHEQUE.equals(receiptMode)) {
			setError(rud, "Values other than CASH/ONLINE/CHEQUE/DD in [RECEIPTMODE] ");
			return;
		}

		String subReceiptMode = StringUtils.upperCase(rud.getSubReceiptMode());
		if (ReceiptMode.ONLINE.equals(receiptMode)) {
			if (StringUtils.isBlank(subReceiptMode)) {
				setError(rud, "[SUBRECEIPTMODE] is Mandatory incase of RECEIPTMODE 'ONLINE' ");
				return;
			}

			if (rud.getSubReceiptMode().length() > 11) {
				setError(rud, "[SUBRECEIPTMODE] with length more than 10 characters");
				return;
			}

			if (!ReceiptMode.NEFT.equals(subReceiptMode) && !ReceiptMode.RTGS.equals(subReceiptMode)
					&& !ReceiptMode.IMPS.equals(subReceiptMode) && !ReceiptMode.IFT.equals(subReceiptMode)) {
				setError(rud, "Values other than IMPS/RTGS/NEFT/IFT in [RECEIPTMODE] ");
				return;
			}

			if (StringUtils.isBlank(rud.getTransactionRef())) {
				setError(rud, "[TRANSCATIONREF] is Mandatory For ONLINE Mode");
				return;
			}
		}

		String channel = rud.getReceiptChannel();

		boolean bounceOrCheque = ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode);

		if (ReceiptMode.CASH.equals(receiptMode) || bounceOrCheque) {
			if (StringUtils.isBlank(channel)) {
				channel = "OTC";
			}

			if (channel.length() > 10) {
				throw new AppException("RU0040", "[RECEIPTCHANNEL] with length more than 10 characters");
			}
		}

		String eftSchdMthd = rud.getEffectSchdMethod();

		if (FinanceConstants.PARTIALSETTLEMENT.equals(purpose)) {
			if (StringUtils.isBlank(eftSchdMthd)) {
				setError(rud, "[EFFETIVESHECDULEMETHOD] is Mandatory if “Receipt Purpose” is captured as “EP");
				return;
			}

			if (!CalculationConstants.EARLYPAY_ADJMUR.equals(eftSchdMthd)
					&& !CalculationConstants.EARLYPAY_RECRPY.equals(eftSchdMthd)) {
				setError(rud, "Values other than RECRPY/ADJMUR in [EFFETIVESHECDULEMETHOD] ");
				return;
			}
		}

		if ("ES".equals(purpose)) {
			String closureType = rud.getClosureType();
			if (StringUtils.isBlank(closureType)) {
				setError(rud, "[CLOSURETYPE] is Mandatory if “Receipt Purpose” is captured as “ES");
				return;
			}

			if (ClosureType.getType(closureType) == null) {
				setError(rud, "Values other than Closure/Fore-Closure/Cancel/Repossession/Top Up in [CLOSURETYPE] ");
				return;
			}

			if (fm.getMaturityDate().compareTo(appDate) <= 0) {
				if (ClosureType.isForeClosure(closureType) || ClosureType.isCancel(closureType)) {
					setError(rud, "Values other than Closure/Repossession/Top Up in [CLOSURETYPE] ");
					return;
				}
			} else if (ClosureType.isClosure(closureType)) {
				setError(rud, "Values other than Fore-Closure/Cancel/Repossession/Top Up in [CLOSURETYPE] ");
				return;
			}
		}

		String status = StringUtils.upperCase(rud.getReceiptModeStatus());
		if (ReceiptMode.CASH.equals(receiptMode) && RepayConstants.PAYSTATUS_BOUNCE.equals(status)) {
			setError(rud, "[RECEIPTMODE] with Bounce Not allowed");
			return;
		}

		if (StringUtils.isNotBlank(rud.getPaymentRef()) && rud.getPaymentRef().length() > 50) {
			setError(rud, "[PAYMENTREF] with length more than 50 characters");
			return;
		}

		String chequeNumber = rud.getChequeNumber();
		String bankcode = rud.getBankCode();

		if (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode)) {

			if (StringUtils.isBlank(chequeNumber)) {
				setError(rud, "[CHEQUENUMBER] is Mandatory for CHEQUE or DD");
				return;
			}

			if (chequeNumber != null && chequeNumber.length() > 6) {
				setError(rud, "[CHEQUENUMBER] with length more than 6");
				return;
			}

			if (StringUtils.isNotBlank(rud.getChequeAccountNumber()) && rud.getChequeAccountNumber().length() > 50) {
				setError(rud, "[CHEQUEACNO] with length more than 50");
				return;
			}

			if (StringUtils.isBlank(bankcode)) {
				setError(rud, "[BANKCODE] is Mandatory");
				return;
			}

			BankDetail bd = bankDetailDAO.getBankDetailById(bankcode, "");
			if (bd == null) {
				setError(rud, "[BANKCODE] is Invalid , Enter Valid BankCode");
				return;
			}
		}

		if (!ReceiptMode.CHEQUE.equals(receiptMode) && !ReceiptMode.DD.equals(receiptMode)
				&& rud.getTransactionRef() != null && rud.getTransactionRef().length() > 50) {
			setError(rud, "[TRANSACTIONREF] with length more than 50");
			return;
		}

		String modeStatus = rud.getReceiptModeStatus();
		if (StringUtils.isBlank(modeStatus)) {
			setError(rud, "[STATUS] is Mandatory");
			return;
		}

		if (modeStatus.length() > 1) {
			setError(rud, "[STATUS] with length more than 1");
			return;
		}

		if (!RepayConstants.PAYSTATUS_REALIZED.equals(modeStatus)
				&& !RepayConstants.PAYSTATUS_DEPOSITED.equals(modeStatus)
				&& !RepayConstants.PAYSTATUS_CANCEL.equals(modeStatus)
				&& !RepayConstants.PAYSTATUS_BOUNCE.equals(modeStatus)) {
			setError(rud, "[STATUS] Should be Valid");
			return;
		}

		if (RepayConstants.PAYSTATUS_BOUNCE.equals(modeStatus)
				|| RepayConstants.PAYSTATUS_DEPOSITED.equals(modeStatus)) {
			if (!DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(receiptMode)
					&& !DisbursementConstants.PAYMENT_TYPE_DD.equals(receiptMode)) {
				setError(rud, "[STATUS] 'B' Or 'D' is allowed only when RECEIPTMODE is 'CHEQUE' or 'DD' ");
				return;
			}
		}

		if (StringUtils.isNotBlank(rud.getExternalRef()) && rud.getExternalRef().length() > 50) {
			setError(rud, "[EXTERNALREF] with length more than 1 ");
			return;
		}

		Date depositDate = rud.getDepositDate();
		Date realizedDate = rud.getRealizationDate();

		if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(receiptMode)
				|| DisbursementConstants.PAYMENT_TYPE_DD.equals(receiptMode)) {

			if (depositDate == null) {
				setError(rud, "[DEPOSITDATE] is Mandatory incase of 'CHEQUE' or 'DD' ");
				return;
			}

			if (DateUtil.compare(depositDate, appDate) > 0 || DateUtil.compare(depositDate, appDate) < 0) {
				setError(rud, "[DEPOSITDATE]  Should not be greater than or Less than the Applicaion Date ");
				return;
			}

			if (RepayConstants.PAYSTATUS_REALIZED.equals(modeStatus) && realizedDate == null) {
				setError(rud, "[REALIZATIONDATE] is Mandatory incase of 'CHEQUE' or 'DD' ");
				return;
			}

			if (RepayConstants.PAYSTATUS_REALIZED.equals(modeStatus) && realizedDate != null
					&& DateUtil.compare(realizedDate, depositDate) < 0) {
				setError(rud, "[REALIZATIONDATE]  Should  be greater than [DEPOSITDATE] ");
				return;
			}

		}

		String entityCode = financeMainDAO.getEntityCodeByRef(reference);
		if (!header.getEntityCode().equals(entityCode)) {
			setError(rud, rud.getReference(), "Loan Reference is not matching with entity");
			return;
		}

		if (rud.isDedupCheck()) {
			checkDedup(rud);
		}

		String panNumber = rud.getPanNumber();

		String cashPanLimit = SysParamUtil.getValueAsString(SMTParameterConstants.RECEIPT_CASH_PAN_LIMIT);
		BigDecimal cashLimit = PennantApplicationUtil.unFormateAmount(cashPanLimit, 2);
		if (rud.getReceiptAmount().compareTo(cashLimit) > 0
				&& DisbursementConstants.PAYMENT_TYPE_CASH.equals(receiptMode) && StringUtils.isEmpty(panNumber)) {
			setError(rud, "[PANNUMBER] is Mandatory , ReceiptAmount exceeded the configured Limit i.e"
					.concat(String.valueOf(cashLimit)));
			return;
		}

		if (StringUtils.isNotEmpty(panNumber)) {
			Pattern pattern = Pattern
					.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_PANNUMBER));
			Matcher matcher = pattern.matcher(panNumber);
			if (!matcher.matches()) {
				setError(rud, "[PANNUMBER] invalid format ".concat(String.valueOf(panNumber))
						+ " Format Should be CCCCCNNNNC ");
				return;
			}

			if (MasterDefUtil.isValidationReq(MasterDefUtil.DocType.PAN)) {
				DocVerificationHeader docHeader = new DocVerificationHeader();
				docHeader.setDocNumber(panNumber);
				docHeader.setDocReference(reference);

				ErrorDetail error = DocVerificationUtil.doValidatePAN(docHeader, true);

				if (error != null) {
					setError(rud, error.getMessage());
					return;
				}
			}
		}

		Date bounceDate = rud.getBounceDate();

		if (!RepayConstants.PAYSTATUS_BOUNCE.equals(modeStatus) && bounceDate != null) {
			setError(rud, "[BOUNCEDATE] is Mandatory only when STATUS is 'B' ");
			return;
		}

		if (RepayConstants.PAYSTATUS_DEPOSITED.equals(modeStatus) && realizedDate != null) {
			setError(rud, "[REALIZATIONDATE] is Mandatory only when STATUS is 'R' ");
			return;
		}

		if (RepayConstants.PAYSTATUS_BOUNCE.equals(modeStatus)) {
			if (bounceDate == null) {
				setError(rud, "[BOUNCEDATE] is Mandatory  ");
				return;
			}

			if (DateUtil.compare(bounceDate, depositDate) < 0) {
				setError(rud, "[BOUNCEDATE] Should not be less than the DEPOSITDATE ");
				return;
			}

			if (DateUtil.compare(bounceDate, realizedDate) < 0) {
				setError(rud, "[BOUNCEDATE] Should not be less than the REALIZATIONDATE ");
				return;
			}

			if (DateUtil.compare(bounceDate, realizedDate) < 0) {
				setError(rud, "[BOUNCEDATE] Should not be less than the REALIZATIONDATE ");
				return;
			}

			if (DateUtil.compare(appDate, bounceDate) < 0) {
				setError(rud, "[BOUNCEDATE] Should not be greater than the APPDATE ");
				return;
			}
		}

		String bReason = rud.getBounceReason();
		String bCRemarks = rud.getBounceRemarks();
		if (RepayConstants.PAYSTATUS_BOUNCE.equals(modeStatus) || RepayConstants.PAYSTATUS_CANCEL.equals(modeStatus)) {
			if (StringUtils.isBlank(bReason)) {
				setError(rud, "[BOUNCE/CANCEl REASON] is Mandatory ");
				return;
			}

			if (RepayConstants.PAYSTATUS_BOUNCE.equals(modeStatus)
					&& bounceReasonDAO.getBounceCodeCount(bReason) == 0) {
				setError(rud, "[BOUNCE/CANCEl REASON] Should be Valid ");
				return;

			}

			if (RepayConstants.PAYSTATUS_CANCEL.equals(modeStatus)
					&& rejectDetailDAO.getRejectCodeCount(bReason) == 0) {
				setError(rud, "[BOUNCE/CANCEl REASON] Should be Valid ");
				return;

			}

		} else {
			if (StringUtils.isNotBlank(bReason)) {
				setError(rud, "[BOUNCE/CANCEl REASON] Should be entered only when status is B or C ");
				return;
			}
		}

		if (StringUtils.isNotBlank(bCRemarks) && bCRemarks.length() > 50) {
			setError(rud, "[BOUNCE/CANCEl REMARKS] Should not be greater than 50 Chnaracters ");
			return;
		}

		if (!ReceiptMode.CASH.equals(receiptMode) && StringUtils.isBlank(rud.getPartnerBankCode())) {
			List<FinTypePartnerBank> ftb = partnerBankDAO.getpartnerbankCode(fm.getFinType(), subReceiptMode);

			if (ftb.size() > 1) {
				setError(rud, "[PARTNERBANKCODE] is mandatory ");
				return;
			}

			for (FinTypePartnerBank ftpb : ftb) {
				String partnerbankCode = partnerBankDAO.getPartnerBankCodeById(ftpb.getPartnerBankID());
				rud.setPartnerBankCode(partnerbankCode);
			}

		}

		String pbMode = receiptMode;

		if (ReceiptMode.ONLINE.equals(pbMode)) {
			pbMode = subReceiptMode;
		}

		if (ReceiptMode.CASH.equals(receiptMode)) {
			if (StringUtils.isNotBlank(rud.getPartnerBankCode())) {
				setError(rud, "[PARTNERBANKCODE] is not allowed for CASH payment");
				return;
			}
		}

		if (StringUtils.isNotBlank(rud.getPartnerBankCode())) {
			PartnerBank pb = partnerBankDAO.getPartnerBankByCode(rud.getPartnerBankCode(), "");
			int count = 0;

			if (pb != null) {
				count = partnerBankDAO.getValidPartnerBank(fm.getFinType(), pbMode, pb.getPartnerBankId());
			}

			if (pb == null || count == 0) {
				setError(rud, "[PARTNERBANKCODE] is not Valid ");
				return;
			}
		}

		ErrorDetail error = receiptService.getWaiverValidation(rud.getReferenceID(), purpose, rud.getValueDate());

		if (error != null) {
			setFailureStatus(rud, error);
			return;
		}

		setSuccesStatus(rud);
	}

	private void prepareKeys(CreateReceiptUpload rud) {
		final Set<String> txnKeys = new HashSet<>();
		final Set<String> txnChequeKeys = new HashSet<>();
		final Set<String> receiptValidList = new HashSet<>();

		rud.setTxnKeys(txnKeys);
		rud.setTxnChequeKeys(txnChequeKeys);
		rud.setReceiptValidList(receiptValidList);
	}

	private boolean isReceiptDetailExist(CreateReceiptUpload rud) {
		String receiptMode = rud.getReceiptMode();

		if (isChequeOrDDMode(receiptMode)
				&& RepayConstants.PAYSTATUS_REALIZED.equalsIgnoreCase(rud.getReceiptModeStatus())) {
			if ("SP".equalsIgnoreCase(rud.getReceiptPurpose())) {
				return finReceiptHeaderDAO.isReceiptExists(rud, "");
			} else {
				return finReceiptHeaderDAO.isReceiptExists(rud, "_Temp");
			}
		}

		return false;
	}

	private void checkDedup(CreateReceiptUpload rud) {
		String txnKey = "";

		String receiptMode = rud.getReceiptMode();

		if (DisbursementConstants.PAYMENT_TYPE_ONLINE.equalsIgnoreCase(receiptMode)) {
			txnKey = getOnlineTxnKey(rud);
			if (!rud.getTxnKeys().add(txnKey)) {
				setError(rud, "90273", "With combination REFERENCE/TRANSACTIONREF/SubReceiptMode: " + txnKey);
				return;
			}
		}

		boolean chequeOrDD = isChequeOrDDMode(receiptMode);

		if (chequeOrDD) {
			txnKey = getOfflineTxnKey(rud);
			if (!rud.getTxnChequeKeys().add(txnKey)) {
				setError(rud, "90273", "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey);
				return;
			}
		}

		if (isReceiptDetailExist(rud)) {
			return;
		}

		if (!"B".equals(rud.getStatus()) && chequeOrDD && this.finReceiptHeaderDAO.isChequeExists(rud)) {
			setError(rud, "90273", "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey);
			return;
		}

		if (DisbursementConstants.PAYMENT_TYPE_ONLINE.equalsIgnoreCase(receiptMode)
				&& this.finReceiptHeaderDAO.isOnlineExists(rud)) {
			setError(rud, "90273", "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey);
		}
	}

	private void isFileExists(CreateReceiptUpload rud) {
		List<String> filenameList = createReceiptUploadDAO.isDuplicateExists(rud);

		boolean dedupinFile = !filenameList.isEmpty();

		String dedup = rud.getReference() + rud.getTransactionRef() + rud.getValueDate() + rud.getReceiptAmount();

		logger.info("Checking duplicate Receipt in ReceiptUploadDetails_Temp table..");

		if (StringUtils.isEmpty(rud.getPaymentRef())) {
			return;
		}

		if (!rud.getReceiptValidList().add(dedup) || dedupinFile) {
			StringBuilder message = new StringBuilder();

			message.append("Duplicate Receipt exists with combination of ");
			message.append(" FinReference - ").append(rud.getReference());
			message.append(", Value Date - ").append(DateUtil.format(rud.getValueDate(), DateFormat.FULL_DATE));
			message.append(", Receipt Amount - ")
					.append(PennantApplicationUtil.amountFormate(rud.getReceiptAmount(), 2));

			if (StringUtils.isNotBlank(rud.getTransactionRef())) {
				message.append(" and Transaction Reference").append(rud.getTransactionRef());
			}

			message.append(" already exists");

			if (dedupinFile) {
				message.append(" with filename").append(filenameList.get(0));
			}

			setError(rud, "21005", message.toString());
			logger.info("Duplicate Receipt found in ReceiptUploadDetails_Temp table..");
		}
	}

	private String getOnlineTxnKey(CreateReceiptUpload rud) {
		StringBuilder key = new StringBuilder();
		key.append(rud.getReference()).append("/");
		key.append(rud.getTransactionRef()).append("/");
		key.append(rud.getSubReceiptMode()).append("/");
		key.append(rud.getPaymentRef());

		return key.toString();
	}

	private String getOfflineTxnKey(CreateReceiptUpload rud) {
		StringBuilder key = new StringBuilder();
		key.append(rud.getReference()).append("/");
		key.append(rud.getReceiptMode()).append("/");
		key.append(rud.getBankCode()).append("/");
		key.append(rud.getTransactionRef());

		return key.toString();
	}

	public void setError(CreateReceiptUpload rud, String message) {
		setError(rud, "RU0040", message);
	}

	public void setError(CreateReceiptUpload rud, String code, String message) {
		String[] valueParm = new String[1];
		valueParm[0] = message;
		rud.setProgress(EodConstants.PROGRESS_FAILED);
		ErrorDetail errorDetail = new ErrorDetail(code, message, valueParm);

		rud.setErrorCode(errorDetail.getCode());
		rud.setErrorDesc(errorDetail.getError());
	}

	private boolean isChequeOrDDMode(String receiptMode) {
		return ReceiptMode.CHEQUE.equalsIgnoreCase(receiptMode) || ReceiptMode.DD.equalsIgnoreCase(receiptMode);
	}

	public void validateAllocations(CreateReceiptUpload uad) {
		String allocationType = uad.getAllocationType();

		if (StringUtils.isBlank(allocationType)) {
			setError(uad, "Allocation Sheet: [ALLOCATIONTYPE] with blank value ");
			return;
		}

		String referenceCode = uad.getCode();
		if (StringUtils.isNotBlank(referenceCode) && referenceCode.length() > 8) {
			setError(uad, "Allocation Sheet: [REFERENCECODE] with lenght more than 8 characters ");
			return;
		}

		try {
			BigDecimal precisionAmount = uad.getReceiptAmount();
			precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
			BigDecimal actualAmount = precisionAmount;

			precisionAmount = precisionAmount.setScale(0, RoundingMode.HALF_DOWN);
			if (precisionAmount.compareTo(actualAmount) != 0) {
				actualAmount = actualAmount.multiply(BigDecimal.valueOf(100));
				actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);
				setError(uad, "Allocation Sheet: Minor Currency (Decimals) in [PAIDAMOUNT] ");
				uad.setPaidAmount(actualAmount);
				return;
			} else {
				uad.setPaidAmount(precisionAmount);
			}

			if (precisionAmount.compareTo(BigDecimal.ZERO) < 0) {
				setError(uad, "Allocation Sheet: [PAIDAMOUNT] with value <0 ");
			}
		} catch (Exception e) {
			uad.setPaidAmount(BigDecimal.ZERO);
			setError(uad, "Allocation Sheet: [PAIDAMOUNT] ");
		}
	}

	private void setFailureStatus(CreateReceiptUpload rud, ErrorDetail error) {
		rud.setProgress(EodConstants.PROGRESS_FAILED);
		rud.setStatus("F");
		rud.setErrorCode(error.getCode());
		rud.setErrorDesc(error.getMessage());
	}

	private void setSuccesStatus(UploadDetails rud) {
		rud.setProgress(EodConstants.PROGRESS_SUCCESS);
		rud.setStatus("S");
		rud.setErrorCode(null);
		rud.setErrorDesc(null);
	}

	@Autowired
	public void setCreateReceiptUploadDAO(CreateReceiptUploadDAO createReceiptUploadDAO) {
		this.createReceiptUploadDAO = createReceiptUploadDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	@Autowired
	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	@Autowired
	public void setBounceReasonDAO(BounceReasonDAO bounceReasonDAO) {
		this.bounceReasonDAO = bounceReasonDAO;
	}

	@Autowired
	public void setRejectDetailDAO(RejectDetailDAO rejectDetailDAO) {
		this.rejectDetailDAO = rejectDetailDAO;
	}

	@Autowired
	public void setBankDetailDAO(BankDetailDAO bankDetailDAO) {
		this.bankDetailDAO = bankDetailDAO;
	}

}