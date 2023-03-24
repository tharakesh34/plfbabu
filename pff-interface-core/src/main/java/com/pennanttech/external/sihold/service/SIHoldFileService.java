//package com.pennanttech.external.sihold.service;
//
//import java.io.File;
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import com.pennanttech.external.config.ExtConfig;
//import com.pennanttech.external.config.ExternalConfig;
//import com.pennanttech.external.fileutil.TextFileUtil;
//import com.pennanttech.external.presentment.constants.InterfaceConstants;
//import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
//import com.pennanttech.external.sihold.dao.ExternalSIHoldMarkingDAO;
//import com.pennanttech.external.sihold.model.SIHoldDetails;
//import com.pennanttech.pennapps.core.App;
//import com.pennanttech.pennapps.core.resource.Literal;
//
//public class SIHoldFileService extends TextFileUtil implements InterfaceConstants {
//
//	private static final Logger logger = LogManager.getLogger(SIHoldFileService.class);
//
//	private ExternalSIHoldMarkingDAO externalHoldMarkingDAO;
//	private ExtPresentmentDAO externalPresentmentDAO;
//	private ExtConfig config;
//
//	public void processManualHoldMarkingRequest() {
//		logger.debug(Literal.ENTERING);
//		try {
//			ExternalConfig externalConfig = externalPresentmentDAO.getExternalConfig();
//			if (!externalConfig.getConfigHash().containsKey(SIHOLD)
//					|| externalConfig.getConfigHash().get(SIHOLD) == null) {
//				return;
//			}
//
//			config = externalConfig.getConfigHash().get(SIHOLD);
//
//			List<SIHoldDetails> siHoldDetails = externalHoldMarkingDAO.getHoldRecords(FILE_NOT_WRITTEN);
//
//			List<StringBuilder> itemList = new ArrayList<StringBuilder>();
//
//			if (!siHoldDetails.isEmpty()) {
//
//				for (SIHoldDetails holdDetails : siHoldDetails) {
//
//					if (holdDetails.getAccount() != null && !"".equals(holdDetails.getAccount())
//							&& holdDetails.getLoanRef() != null && !"".equals(holdDetails.getLoanRef())
//							&& holdDetails.getHoldAmt().compareTo(BigDecimal.ZERO) == 1) {
//
//						StringBuilder item = new StringBuilder();
//						item.append("2");// default
//						item.append(fileSeperator);
//						item.append(StringUtils.leftPad(holdDetails.getAccount(), 14));
//						item.append(fileSeperator);
//						item.append(StringUtils.leftPad(parseString(holdDetails.getHoldAmt(), ccyFromat), 16));
//						item.append(fileSeperator);
//						item.append(StringUtils.leftPad(holdDetails.getLoanRef(), 20));
//						item.append(fileSeperator);
//
//						// Narration
//						StringBuilder sb = new StringBuilder();
//						sb.append(holdDetails.getLoanRef());
//						sb.append("-");
//						sb.append("LOAN MANUAL HOLD CHARGE");
//
//						item.append(StringUtils.rightPad(sb.toString(), 40));
//						appendData(item, fileSeperator, 5);
//						item.append(config.getHodlType());
//
//						itemList.add(item);
//
//						externalHoldMarkingDAO.updateHoldRecordFileStatus(holdDetails.getAccount(),
//								holdDetails.getLoanRef(), holdDetails.getSchDate(), FILE_WRITTEN);
//					} else {
//						logger.debug("Neglecting SI Hold request record,  record {}", holdDetails.toString());
//					}
//
//				}
//			}
//
//			if (itemList.size() > 0) {
//
//				StringBuilder footer = new StringBuilder();
//				footer.append("3");
//				footer.append(fileSeperator);
//				footer.append(itemList.size());
//				itemList.add(footer);
//
//				long fileSeq = externalHoldMarkingDAO.getSeqNumber(SILIENSEQ);
//				String fileSeqName = StringUtils.leftPad(String.valueOf(fileSeq), 4, "0");
//				String baseFilePath = App.getResourcePath(config.getFileLocation());
//
//				String fileName = baseFilePath + File.separator + config.getFilePrepend()
//						+ new SimpleDateFormat(config.getDateFormat()).format(new Date()) + config.getFilePostpend()
//						+ fileSeqName + config.getFileExtension();
//
//				super.writeDataToFile(fileName, itemList);
//
//			}
//		} catch (Exception e) {
//			logger.debug("Exception writing SI Hold request record,  record {}", e);
//			e.printStackTrace();
//		}
//		logger.debug(Literal.LEAVING);
//	}
//
//	public static String parseString(BigDecimal amount, int decimals) {
//		return (parse(amount, decimals)).toString();
//	}
//
//	public static BigDecimal parse(BigDecimal amount, int decimals) {
//		BigDecimal bigDecimal = BigDecimal.ZERO;
//
//		if (amount != null) {
//			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, decimals)));
//		}
//		return bigDecimal;
//	}
//
//	public void setExternalHoldMarkingDAO(ExternalSIHoldMarkingDAO externalHoldMarkingDAO) {
//		this.externalHoldMarkingDAO = externalHoldMarkingDAO;
//	}
//
//	public void setExternalPresentmentDAO(ExtPresentmentDAO externalPresentmentDAO) {
//		this.externalPresentmentDAO = externalPresentmentDAO;
//	}
//
//	public void setConfig(ExtConfig config) {
//		this.config = config;
//	}
//
//}
