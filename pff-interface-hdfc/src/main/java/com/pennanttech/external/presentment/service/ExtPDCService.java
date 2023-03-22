package com.pennanttech.external.presentment.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.fileutil.TextFileUtil;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtPDCService extends TextFileUtil implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(ExtPDCService.class);

	private ExtPresentmentDAO externalPresentmentDAO;

	private static final String SPACE_SEPARATOR = "         ";
	private static final String CAP_SEPARATOR = "^";
	private static final String TOP_ROW = "CHEQUES PRESENTATION REPORT (Detail)";
	private static final String ALL_BRANCHES_STR = "All Branches";
	private static final String INSTRUMENT_STR = "Instrument:^External Pdcs";
	private static final String NO_OF_CHEQUES = "Number of Cheques present";
	private static final String AMOUNT_OF_CHEQUES = "Amount of Cheques:";
	private static final String END_OF_REPORT = "(*** End Of Report ***)";
	private static final String NEW_LINE = "\n";
	private static final int EXT_PDC_CCY = 2;

	public void processExtPDCPresentments(ExternalConfig config, List<ExtPresentmentFile> presentmentList,
			Date appDate) {
		logger.debug(Literal.ENTERING);
		// Seperate presentment list by clusterId and generate files..
		HashMap<String, List<ExtPresentmentFile>> clusterPrmntMap = getClusterBasedList(presentmentList);
		// For each cluster, generate file with filtered presentment list
		for (String clusterId : clusterPrmntMap.keySet()) {
			writeRecordsToFile(config, clusterPrmntMap.get(clusterId), clusterId, appDate);
		}
		logger.debug(Literal.LEAVING);
	}

	private void writeRecordsToFile(ExternalConfig config, List<ExtPresentmentFile> presentmentList, String clusterId,
			Date appDate) {
		logger.debug(Literal.ENTERING);
		try {
			List<StringBuilder> itemList = new ArrayList<StringBuilder>();
			StringBuilder firstRow = new StringBuilder();
			firstRow.append(TOP_ROW);
			itemList.add(firstRow);
			firstRow = new StringBuilder();
			String datesHeader = getDatesHeader(presentmentList, appDate);
			firstRow.append(datesHeader);//
			itemList.add(firstRow);
			firstRow = new StringBuilder();
			firstRow.append(ALL_BRANCHES_STR);
			itemList.add(firstRow);
			firstRow = new StringBuilder();
			itemList.add(firstRow);
			firstRow = new StringBuilder();
			firstRow.append(INSTRUMENT_STR);
			itemList.add(firstRow);
			firstRow = new StringBuilder();
			itemList.add(firstRow);

			StringBuilder header = generateHeader();
			itemList.add(header);

			long cnt = 0;
			for (ExtPresentmentFile data : presentmentList) {

				cnt = cnt + 1;

				StringBuilder item = new StringBuilder();
				item.append(cnt);
				item.append(CAP_SEPARATOR);
				item.append(data.getFinBranchId());
				item.append(CAP_SEPARATOR);
				item.append(data.getFinBranchName());
				item.append(CAP_SEPARATOR);
				item.append(data.getProduct());
				item.append(CAP_SEPARATOR);
				item.append(data.getAgreementId());
				item.append(CAP_SEPARATOR);
				item.append(data.getCustomerName());
				item.append(CAP_SEPARATOR);

				item.append(data.getCityId());
				item.append(CAP_SEPARATOR);
				item.append(data.getCityName());
				item.append(CAP_SEPARATOR);
				item.append(data.getBankId());
				item.append(CAP_SEPARATOR);
				item.append(data.getBankName());
				item.append(CAP_SEPARATOR);
				item.append(data.getBankBranchId());
				item.append(CAP_SEPARATOR);
				item.append(data.getBankBranchName());
				item.append(CAP_SEPARATOR);
				item.append(data.getMicr());
				item.append(CAP_SEPARATOR);

				item.append(data.getBatchReference());
				item.append(CAP_SEPARATOR);
				item.append(data.getAccountNo());
				item.append(CAP_SEPARATOR);
				item.append(formatChequeNo(data.getChequeSerialNo()));// 6 digit
				item.append(CAP_SEPARATOR);
				item.append("External PDCs");
				item.append(CAP_SEPARATOR);
				item.append(new SimpleDateFormat("dd/MM/yyyy").format(data.getChequeDate()));
				item.append(CAP_SEPARATOR);
				item.append(new SimpleDateFormat("dd/MM/yyyy").format(data.getSchDate()));
				item.append(CAP_SEPARATOR);
				item.append(convertAmount(data.getSchAmtDue(), EXT_PDC_CCY));//
				item.append(CAP_SEPARATOR);
				item.append(data.getBankCode());
				item.append(CAP_SEPARATOR);
				item.append("");
				itemList.add(item);
			}

			if (cnt > 0) {
				BigDecimal totalAmount = presentmentList.stream().map(ExtPresentmentFile::getSchAmtDue)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				StringBuilder footer = new StringBuilder();

				footer = new StringBuilder();
				footer.append(NEW_LINE);
				itemList.add(footer);

				footer = new StringBuilder();
				footer.append(NO_OF_CHEQUES);
				footer.append(CAP_SEPARATOR);
				footer.append(cnt);
				itemList.add(footer);

				footer = new StringBuilder();
				footer.append(AMOUNT_OF_CHEQUES);
				footer.append(CAP_SEPARATOR);
				footer.append(convertAmount(totalAmount, EXT_PDC_CCY));
				itemList.add(footer);

				footer = new StringBuilder();
				footer.append(NEW_LINE);
				itemList.add(footer);

				footer = new StringBuilder();
				footer.append(END_OF_REPORT);
				itemList.add(footer);

				long fileSeq = externalPresentmentDAO.getSeqNumber(SEQ_PRMNT_PDC);
				String fileSeqName = StringUtils.leftPad(String.valueOf(fileSeq), 4, "0");
				String baseFilePath = App.getResourcePath(config.getFileLocation());

				StringBuilder clusterBuilder = new StringBuilder();
				clusterBuilder.append(clusterId);
				clusterBuilder.append("_");

				String fileName = baseFilePath + File.separator + config.getFilePrepend() + clusterBuilder
						+ new SimpleDateFormat(config.getDateFormat()).format(appDate) + config.getFilePostpend()
						+ fileSeqName + config.getFileExtension();

				super.writeDataToFile(fileName, itemList);

			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private String formatChequeNo(String chequeNo) {
		return StringUtils.leftPad(String.valueOf(chequeNo), 6, "0");
	}

	private HashMap<String, List<ExtPresentmentFile>> getClusterBasedList(List<ExtPresentmentFile> prmntList) {
		HashMap<String, List<ExtPresentmentFile>> clusterSeparatedMap = new HashMap<String, List<ExtPresentmentFile>>();
		if (!prmntList.isEmpty()) {
			for (ExtPresentmentFile eachPrmnt : prmntList) {
				String clusterId = eachPrmnt.getClusterId();
				if (clusterSeparatedMap.containsKey(clusterId)) {
					clusterSeparatedMap.get(clusterId).add(eachPrmnt);
				} else {
					List<ExtPresentmentFile> newPrmntList = new ArrayList<ExtPresentmentFile>();
					newPrmntList.add(eachPrmnt);
					clusterSeparatedMap.put(clusterId, newPrmntList);
				}
			}
		}
		return clusterSeparatedMap;
	}

	private String getDatesHeader(List<ExtPresentmentFile> presentmentList, Date appDate) {
		try {
			Date prmntDate = null;
			if (presentmentList != null && !presentmentList.isEmpty()) {
				prmntDate = presentmentList.get(0).getSchDate();
			}

			StringBuilder sb = new StringBuilder();
			SimpleDateFormat pmntateFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sysDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
			sb.append("As on date :").append(CAP_SEPARATOR).append(pmntateFormat.format(prmntDate))
					.append(CAP_SEPARATOR).append(SPACE_SEPARATOR).append("Creation Date :").append(CAP_SEPARATOR)
					.append(sysDateFormat.format(appDate));
			return sb.toString();
		} catch (Exception e) {
			return "";
		}

	}

	private StringBuilder generateHeader() {
		StringBuilder item = new StringBuilder();
		item = new StringBuilder();
		item.append("Sr No");
		item.append(CAP_SEPARATOR);
		item.append("Branch Id");
		item.append(CAP_SEPARATOR);
		item.append("Branch Name");
		item.append(CAP_SEPARATOR);
		item.append("Product");
		item.append(CAP_SEPARATOR);
		item.append("Agmt No");
		item.append(CAP_SEPARATOR);
		item.append("Customer Name");
		item.append(CAP_SEPARATOR);
		item.append("City Id");
		item.append(CAP_SEPARATOR);
		item.append("City Name");
		item.append(CAP_SEPARATOR);
		item.append("Bank Id");
		item.append(CAP_SEPARATOR);
		item.append("Bank Name");
		item.append(CAP_SEPARATOR);
		item.append("BankBranchId");
		item.append(CAP_SEPARATOR);
		item.append("BankBranch Name");
		item.append(CAP_SEPARATOR);
		item.append("MICR Code");
		item.append(CAP_SEPARATOR);
		item.append("Batch Number");
		item.append(CAP_SEPARATOR);
		item.append("FinwareAcno.");
		item.append(CAP_SEPARATOR);
		item.append("ChequeSno.");
		item.append(CAP_SEPARATOR);
		item.append("InstrumentType");
		item.append(CAP_SEPARATOR);
		item.append("Chq.Date");
		item.append(CAP_SEPARATOR);
		item.append("Prestn.Dt.");
		item.append(CAP_SEPARATOR);
		item.append("Chq. Amount");
		item.append(CAP_SEPARATOR);
		item.append("Bank Code");
		item.append(CAP_SEPARATOR);
		item.append("CBOP Agrno");
		return item;
	}

	public void setExternalPresentmentDAO(ExtPresentmentDAO externalPresentmentDAO) {
		this.externalPresentmentDAO = externalPresentmentDAO;
	}

}
