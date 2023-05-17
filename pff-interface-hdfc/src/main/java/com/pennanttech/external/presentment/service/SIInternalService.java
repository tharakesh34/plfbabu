package com.pennanttech.external.presentment.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.external.presentment.model.ExtPresentmentData;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class SIInternalService extends TextFileUtil implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(SIService.class);

	private ExtPresentmentDAO externalPresentmentDAO;

	public void processSiInternalPresentments(FileInterfaceConfig config, List<ExtPresentmentFile> presentmentList,
			Date appDate) {
		logger.debug(Literal.ENTERING);
		try {
			List<StringBuilder> itemList = new ArrayList<StringBuilder>();
			for (ExtPresentmentFile data : presentmentList) {

				StringBuilder item = new StringBuilder();
				item.append("2");// default
				item.append(fileSeperator);
				item.append(StringUtils.leftPad(data.getAccountNo(), 14));
				item.append(fileSeperator);
				item.append(StringUtils.leftPad(convertAmount(data.getSchAmtDue(), ccyFromat), 16));
				item.append(fileSeperator);
				item.append(StringUtils.leftPad(data.getPresentmentRef(), 20));
				item.append(fileSeperator);
				String formattedDate = "";
				try {

					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
					formattedDate = dateFormat.format(data.getSchDate());

				} catch (Exception e) {
					formattedDate = "";
				}
				StringBuilder sb = new StringBuilder();
				// EMI {finref} Chq {id}/{emi.no}-{no.of.terms} 01/22
				sb.append("EMI ");
				sb.append(data.getFinReference());
				sb.append(" Chq");
				sb.append(" ");
				sb.append(data.getUtrNumber());
				sb.append("/");
				sb.append(data.getEmiNo());
				sb.append("-");
				sb.append(data.getNumberOfTerms());
				sb.append(" ");
				sb.append(formattedDate);
				item.append(StringUtils.rightPad(sb.toString(), 40));
				item.append(fileSeperator);
				item.append(StringUtils.leftPad(data.getUtrNumber(), 20));
				appendData(item, fileSeperator, 4);
				item.append(config.getHodlType());
				itemList.add(item);

			}

			if (itemList.size() > 0) {

				StringBuilder footer = new StringBuilder();
				footer.append("3");
				footer.append(fileSeperator);
				footer.append(itemList.size());
				itemList.add(footer);

				long fileSeq = externalPresentmentDAO.getSeqNumber(SEQ_PRMNT_SI_INTERNAL);
				String fileSeqName = StringUtils.leftPad(String.valueOf(fileSeq), 4, "0");
				String baseFilePath = App.getResourcePath(config.getFileLocation());

				String fileName = baseFilePath + File.separator + config.getFilePrepend()
						+ new SimpleDateFormat(config.getDateFormat()).format(appDate) + config.getFilePostpend()
						+ fileSeqName + config.getFileExtension();

				super.writeDataToFile(fileName, itemList);

			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	public ExtPresentmentFile prepareResponseObject(FileInterfaceConfig extConfig, ExtPresentmentData extPresentmentData,
			String fileName) {

		logger.debug(Literal.ENTERING);

		String filePrepend = extConfig.getFilePrepend();
		String fileExtension = extConfig.getFileExtension();

		if (fileName.startsWith(filePrepend.concat(extConfig.getSuccessIndicator()))
				&& fileName.endsWith(fileExtension)) {
			// Success file
			return prepareSuccessResponse(extPresentmentData.getRecord());
		}

		if (fileName.startsWith(filePrepend.concat(extConfig.getFailIndicator())) && fileName.endsWith(fileExtension)) {
			// Rejected file
			return prepareRejectResponse(extPresentmentData.getRecord());
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	public ExtPresentmentFile prepareSuccessResponse(String stringBuilder) {

		logger.debug(Literal.ENTERING);
		ExtPresentmentFile presentment = new ExtPresentmentFile();
		String[] dataArray = stringBuilder.split("~");
		if (dataArray.length >= 8) {
			presentment.setChequeSerialNo(dataArray[7]);
		}

		long txnCode = -1;

		if (dataArray.length >= 7) {
			txnCode = getLongValue(dataArray[6]);
		}

		presentment.setTxnReference(txnCode);
		presentment.setStatus(SUCCESS);

		logger.debug(Literal.LEAVING);
		return presentment;

	}

	public ExtPresentmentFile prepareRejectResponse(String stringBuilder) {
		logger.debug(Literal.ENTERING);
		ExtPresentmentFile presentment = new ExtPresentmentFile();
		String[] dataArray = stringBuilder.split("~");

		long txnRef = -1;
		if (dataArray.length >= 3) {
			txnRef = getLongValue(dataArray[2]);
		}

		presentment.setTxnReference(txnRef);

		if (dataArray.length >= 7) {
			presentment.setChequeSerialNo(dataArray[6]);
		}
		if (dataArray.length >= 6) {
			presentment.setBounceReason(dataArray[5]);
		}

		String bouceReturnCode = null;

		if (dataArray.length >= 5) {
			bouceReturnCode = dataArray[4];
		}
		presentment.setBounceRetrunCode(bouceReturnCode);
		presentment.setStatus(FAIL);

		logger.debug(Literal.LEAVING);
		return presentment;
	}

	private static Long getLongValue(String strLong) {
		try {
			return Long.parseLong(strLong);
		} catch (Exception e) {
			return (long) -1;
		}
	}

	public void setExternalPresentmentDAO(ExtPresentmentDAO externalPresentmentDAO) {
		this.externalPresentmentDAO = externalPresentmentDAO;
	}

}