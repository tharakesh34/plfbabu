package com.pennant.pff.receipt.validate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.LoanClosure;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.receipt.dao.LoanClosureUploadDAO;
import com.pennant.pff.receipt.model.CreateReceiptUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class LoanClosureUploadValidateRecord implements ProcessRecord {
	private static final Logger logger = LogManager.getLogger(CreateReceiptUploadDataValidator.class);

	private LoanClosureUploadDAO loanClosureUploadDAO;

	public LoanClosureUploadValidateRecord() {
		super();
	}

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table)
			throws Exception {
		logger.debug(Literal.ENTERING);

		Sheet sheet = attributes.getSheet();

		Row headerRow = sheet.getRow(0);

		Row row = attributes.getRow();

		LoanClosure cu = new LoanClosure();

		Long headerID = (Long) attributes.getParameterMap().get("HEADER_ID");

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		cu.setHeaderId(headerID);

		int readColumn = 0;

		Cell rowCell = null;

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
				cu.setReference(rowCell.toString());
				break;
			case 1:
				cu.setRemarks(rowCell.toString());
				break;
			case 2:
				cu.setReasonCode(Long.parseLong(toString()));
				break;
			case 3:
				cu.setClosureType(rowCell.toString());
				break;

			case 4:
				String strAmount = rowCell.toString();
				if (strAmount != null) {
					cu.setPrincipal_W(PennantApplicationUtil.unFormateAmount(strAmount, 2));
				}
				break;
			case 5:
				String interestAmt = rowCell.toString();
				if (interestAmt != null) {
					cu.setInterest_W(PennantApplicationUtil.unFormateAmount(interestAmt, 2));
				}
				break;
			case 6:
				String bounceAmt = rowCell.toString();
				if (bounceAmt != null) {
					cu.setBounce_W(PennantApplicationUtil.unFormateAmount(bounceAmt, 2));
				}
				break;
			case 7:
				String lppAmt = rowCell.toString();
				if (lppAmt != null) {
					cu.setLpp_W(PennantApplicationUtil.unFormateAmount(lppAmt, 2));
				}
				break;
			case 8:
				String futIntAmt = rowCell.toString();
				if (futIntAmt != null) {
					cu.setFtInterest_W(PennantApplicationUtil.unFormateAmount(futIntAmt, 2));
				}
				break;
			case 9:
				String futPriAmt = rowCell.toString();
				if (futPriAmt != null) {
					cu.setFtPrincipal_W(PennantApplicationUtil.unFormateAmount(futPriAmt, 2));
				}
				break;
			case 10:
				cu.setFeeTypeCode(rowCell.toString());
				break;

			default:
				break;
			}
		}

		long uploadID = loanClosureUploadDAO.save(cu);

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
						throw new AppException("Invalid amount");
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

		// validate(cu, header);

		if (cu.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", cu.getErrorCode());
			record.addValue("ERRORDESC", cu.getErrorDesc());

			List<LoanClosure> details = new ArrayList<>();
			details.add(cu);

			loanClosureUploadDAO.update(details);
		}

		logger.debug(Literal.LEAVING);
	}

	public void setLoanClosureUploadDAO(LoanClosureUploadDAO loanClosureUploadDAO) {
		this.loanClosureUploadDAO = loanClosureUploadDAO;
	}

}
