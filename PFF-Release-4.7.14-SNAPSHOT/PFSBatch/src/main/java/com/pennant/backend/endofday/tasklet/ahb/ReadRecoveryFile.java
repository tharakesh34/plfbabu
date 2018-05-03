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
 *
 * FileName    		:  ReadRecoveryFile.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.endofday.tasklet.ahb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.BatchFileUtil;
import com.pennant.eod.beans.PaymentRecoveryDetail;
import com.pennant.eod.dao.PaymentRecoveryDetailDAO;
import com.pennanttech.pennapps.core.InterfaceException;

public class ReadRecoveryFile implements Tasklet {
	private Logger						logger	= Logger.getLogger(ReadRecoveryFile.class);
	private PaymentRecoveryDetailDAO	paymentRecoveryDetailDAO;

	public ReadRecoveryFile() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date date = DateUtility.getAppValueDate();
		logger.debug("START: Request File Reading for Value Date: " + date);

		boolean fileRecieved = false;
		long reuest = DateUtility.getSysDate().getTime();
		long delay = 1 * 15 * 1000;
		int count = 0;

		while (!fileRecieved) {

			BatchUtil.setExecution(context, "WAIT", "Waiting for response");
			long current = DateUtility.getSysDate().getTime();

			if ((current - reuest) >= delay) {
				File file = readFile();
				if (!file.exists()) {
					reuest = DateUtility.getSysDate().getTime();
					continue;
				}

				BatchUtil.setExecution(context, "WAIT", "Response recieved");
				count = 0;
				boolean valid = false;
				String line = null;
				BufferedReader reader = new BufferedReader(new FileReader(file));
				while ((line = reader.readLine()) != null) {
					String[] details = line.split("[" + BatchFileUtil.DELIMITER + "]");
					String recordIdentifier = details[0];
					count++;
					if (recordIdentifier.equals(BatchFileUtil.FOOTER)) {
						valid = true;
						break;
					}
				}
				reader.close();

				if (!valid) {
					BatchUtil.setExecution(context, "WAIT", "File Not Valid");
					throw new InterfaceException("50001", "Invalid File");
				} else {
					fileRecieved = true;
				}
			}
		}

		BatchUtil.setExecution(context, "TOTAL", String.valueOf(count));

		BufferedReader reader = new BufferedReader(new FileReader(readFile()));
		
		updatePaymentRecoveryDetail(reader, context);
		//move files 
		moveFilestoHistory();
		
		logger.debug("END: Request File Reading for Value Date: " + date);
		return RepeatStatus.FINISHED;

	}

	private void updatePaymentRecoveryDetail(BufferedReader reader, ChunkContext context) throws Exception {

		try {
			String line = null;
			int count = 0;

			while ((line = reader.readLine()) != null) {
				count++;
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(count));
				String[] details = line.split("[" + BatchFileUtil.DELIMITER + "]");

				String recordIdentifier = details[0];
				if (recordIdentifier.equals(BatchFileUtil.DETAILS)) {
					PaymentRecoveryDetail recoveryDetail = readDetails(details);
					getPaymentRecoveryDetailDAO().update(recoveryDetail);
				}

			}
			reader.close();
			logger.debug(" Leaving ");

		} catch (Exception e) {
			logger.debug(e);
			throw e;

		}

	}

	/**
	 * @param details
	 * @return
	 * @throws ParseException 
	 */
	private PaymentRecoveryDetail readDetails(String[] details) throws ParseException {
		logger.debug(" Entering ");

		PaymentRecoveryDetail recoveryDetail = new PaymentRecoveryDetail();

		recoveryDetail.setRecordIdentifier(details[0]);
		recoveryDetail.setTransactionReference(details[1]);
		recoveryDetail.setPrimaryDebitAccount(details[2]);
		recoveryDetail.setSecondaryDebitAccounts(details[3]);
		recoveryDetail.setCreditAccount(details[4]);
		recoveryDetail.setScheduleDate(DateUtility.parse(details[5], PennantConstants.DBDateFormat));
		recoveryDetail.setFinanceReference(details[6]);
		recoveryDetail.setCustomerReference(details[7]);
		recoveryDetail.setDebitCurrency(details[8]);
		recoveryDetail.setCreditCurrency(details[9]);
		recoveryDetail.setPaymentAmount(new BigDecimal(details[10]));
		recoveryDetail.setTransactionPurpose(details[11]);
		recoveryDetail.setFinanceBranch(details[12]);
		recoveryDetail.setFinanceType(details[13]);
		recoveryDetail.setFinancePurpose(details[14]);
		recoveryDetail.setSysTranRef(details[15]);

		int code =  CurrencyUtil.getFormat(recoveryDetail.getDebitCurrency());
		String debitamount = StringUtils.trimToEmpty(details[16]);
		String secordayDebitamount = StringUtils.trimToEmpty(details[17]);

		BigDecimal unfamount = PennantApplicationUtil.unFormateAmount(new BigDecimal(debitamount), code);
		recoveryDetail.setPrimaryAcDebitAmt(unfamount);

		String[] amounts = secordayDebitamount.split(";");
		StringBuilder builder = new StringBuilder();
		for (String amount : amounts) {
			BigDecimal unfsamount = PennantApplicationUtil.unFormateAmount(new BigDecimal(amount), code);
			if (builder.length() == 0) {
				builder.append(unfsamount);
			} else {
				builder.append(";");
				builder.append(unfsamount);
			}
		}
		recoveryDetail.setSecondaryAcDebitAmt(builder.toString());
		recoveryDetail.setPaymentStatus(details[18]);

		logger.debug(" Leaving ");
		return recoveryDetail;

	}

	private void moveFilestoHistory() throws IOException {
		
		File reqfile =  BatchFileUtil.getFile(BatchFileUtil.getAutoPayReqFileName());
		File respfile = BatchFileUtil.getFile(BatchFileUtil.getAutoPayResFileName());

		File reqhsitory = new File(PathUtil.getPath(PathUtil.EOD_FILE_HISTORY) + "/" + BatchFileUtil.getAutoPayReqFileName());
		File resphsitory = new File(PathUtil.getPath(PathUtil.EOD_FILE_HISTORY) + "/" + BatchFileUtil.getAutoPayResFileName());

		Files.move(reqfile.toPath(), reqhsitory.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.move(respfile.toPath(), resphsitory.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	private File readFile() {
		File file = new File(PathUtil.getPath(PathUtil.EOD_FILE_FOLDER) + "/" + BatchFileUtil.getAutoPayResFileName());
		return file;
	}

	private PaymentRecoveryDetailDAO getPaymentRecoveryDetailDAO() {
		return paymentRecoveryDetailDAO;
	}

	public void setPaymentRecoveryDetailDAO(PaymentRecoveryDetailDAO paymentRecoveryDetailDAO) {
		this.paymentRecoveryDetailDAO = paymentRecoveryDetailDAO;
	}

}
