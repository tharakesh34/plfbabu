package com.pennanttech.pff.cersai;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.northconcepts.datapipeline.core.Record;
import com.northconcepts.datapipeline.csv.CSVWriter;
import com.pennant.backend.model.cersai.CersaiAddCollDetails;
import com.pennant.backend.model.cersai.CersaiAssetOwners;
import com.pennant.backend.model.cersai.CersaiBorrowers;
import com.pennant.backend.model.cersai.CersaiChargeHolder;
import com.pennant.backend.model.cersai.CersaiFileInfo;
import com.pennant.backend.model.cersai.CersaiHeader;
import com.pennant.backend.model.cersai.CersaiImmovableAsset;
import com.pennant.backend.model.cersai.CersaiIntangibleAsset;
import com.pennant.backend.model.cersai.CersaiModifyCollDetails;
import com.pennant.backend.model.cersai.CersaiMovableAsset;
import com.pennant.backend.model.cersai.CersaiSatisfyCollDetails;
import com.pennant.backend.service.cersai.CERSAIService;
import com.pennant.backend.util.CersaiConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CERSAIDownloadProcess {

	protected static final Logger logger = LoggerFactory.getLogger(CERSAIDownloadProcess.class);

	public static final DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("CERSAI_EXTRACT_STATUS");

	private CersaiFileInfo fileInfo;
	private String filePath;
	private long headerId;
	private long totalRecords;
	private long processedRecords;
	private long successCount;
	private long failedCount;
	private CERSAIService cersaiService;

	public void generateAddReport() throws IOException {
		logger.debug(Literal.ENTERING);

		initlize();
		CersaiChargeHolder cch = cersaiService.getChargeHolderDetails();
		filePath = cch.getFilePath();

		File cersaiFile = createFile();

		final CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter(cersaiFile)));
		writer.setFieldNamesInFirstRow(false);
		writer.setFieldSeparator('|');
		writer.open();
		try {

			List<String> collateralList = cersaiService.getotalRecords(CersaiConstants.CERSAI_ADD);

			totalRecords = collateralList.size();

			CersaiHeader ch = cersaiService.processFileHeader(totalRecords, CersaiConstants.CERSAI_ADD);

			if (totalRecords > 0) {
				new HeaderSegment(writer, ch).write();
			} else {
				MessageUtil.showMessage("No Records Found For Registration");
				return;
			}

			fileInfo = new CersaiFileInfo();
			fileInfo.setFileName(cersaiFile.getName());
			fileInfo.setTotalRecords(totalRecords);
			fileInfo.setProcessedRecords(processedRecords);
			fileInfo.setSuccessCount(successCount);
			fileInfo.setFailedCount(failedCount);
			fileInfo.setRemarks(updateRemarks());
			fileInfo.setFileLocation(filePath);
			fileInfo.setDownloadType(CersaiConstants.CERSAI_ADD);

			cersaiService.logFileInfo(fileInfo);
			headerId = fileInfo.getId();

			// updating the Cersai_File_Info
			cersaiService.updateFileStatus(fileInfo);

			int serialNo = 1;

			String batchRef = cersaiService.generateBatchRef();

			for (String collateralRef : collateralList) {
				EXTRACT_STATUS.setProcessedRecords(processedRecords++);
				serialNo = processRegFile(cch, writer, ch, serialNo, batchRef, collateralRef);
			}
		} catch (Exception e) {
			EXTRACT_STATUS.setStatus("F");
			logger.error(Literal.EXCEPTION, e);
		} finally {
			writer.close();
		}

		if ("F".equals(EXTRACT_STATUS.getStatus())) {
			return;
		}

		EXTRACT_STATUS.setStatus("S");

		String remarks = updateRemarks();
		fileInfo.setRemarks(remarks);
		fileInfo.setStatus(EXTRACT_STATUS.getStatus());
		fileInfo.setTotalRecords(totalRecords);
		fileInfo.setProcessedRecords(processedRecords);
		fileInfo.setFailedCount(failedCount);
		fileInfo.setSuccessCount(successCount);
		fileInfo.setRemarks(remarks);

		cersaiService.updateFileStatus(fileInfo);

		logger.debug(Literal.LEAVING);
	}

	private int processRegFile(CersaiChargeHolder cch, final CSVWriter writer, CersaiHeader ch, int serialNo,
			String batchRef, String collateralRef) {
		try {
			CersaiMovableAsset cma = null;
			CersaiImmovableAsset cima = null;
			CersaiIntangibleAsset cia = null;

			List<CersaiBorrowers> borrowers = cersaiService.getCustomerDetails(collateralRef);

			List<CersaiAssetOwners> assetOwners = cersaiService.getAssetOwnerDetails(collateralRef);

			borrowers = processBorrowers(borrowers, assetOwners);

			assetOwners = processAssetOwners(borrowers, assetOwners);

			long batchId = ch.getBatchId();

			List<CersaiAddCollDetails> ccDtl = cersaiService.processCollateralDetails(collateralRef, batchId,
					borrowers.size(), assetOwners.size(), serialNo, batchRef);

			borrowers = cersaiService.processBorrowers(borrowers, batchId);

			assetOwners = cersaiService.processAssetOwners(assetOwners, batchId);

			String assetCategory = cersaiService.getAssetCategory(ccDtl.get(0).getSiTypeId());
			String collateralType = ccDtl.get(0).getCollateralType();
			if (StringUtils.trimToNull(assetCategory) != null) {
				if (assetCategory.equalsIgnoreCase("Movable")) {
					cma = cersaiService.processMovable(collateralRef, batchId, collateralType);
				} else if (assetCategory.equalsIgnoreCase("Immovable")) {
					cima = cersaiService.processImmovable(collateralRef, batchId, collateralType);
				} else if (assetCategory.equalsIgnoreCase("Intangible")) {
					cia = cersaiService.processInTangible(collateralRef, batchId, collateralType);
				}
			}

			new CollateralDetails(writer, ccDtl).write();

			if (StringUtils.trimToNull(assetCategory) != null) {
				if (assetCategory.equalsIgnoreCase("Movable")) {
					new MovableSegemnt(writer, cma).write();
				} else if (assetCategory.equalsIgnoreCase("Immovable")) {
					new ImMovableSegemnt(writer, cima).write();
				} else if (assetCategory.equalsIgnoreCase("Intangible")) {
					new IntangibleSegemnt(writer, cia).write();
				}
			}

			new BorrowerSegment(writer, borrowers).write();

			new AssetOwnerSegment(writer, assetOwners).write();

			new ChargeHolder(writer, cch).write();

			EXTRACT_STATUS.setSuccessRecords(successCount++);

			serialNo++;
		} catch (Exception e) {
			EXTRACT_STATUS.setFailedRecords(failedCount++);
			EXTRACT_STATUS.setStatus("S");
			cersaiService.logFileInfoException(headerId, String.valueOf(collateralRef), e.getMessage());
			logger.error(Literal.EXCEPTION, e);
		}
		return serialNo;
	}

	public void generateModifyReport() throws IOException {
		logger.debug(Literal.ENTERING);

		initlize();
		CersaiChargeHolder cch = cersaiService.getChargeHolderDetails();
		filePath = cch.getFilePath();
		File cersaiFile = createFile();

		final CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter(cersaiFile)));
		writer.setFieldNamesInFirstRow(false);
		writer.setFieldSeparator('|');
		writer.open();
		try {

			List<String> collateralList = cersaiService.getModifyRecords();

			totalRecords = collateralList.size();

			CersaiHeader ch = cersaiService.processFileHeader(totalRecords, CersaiConstants.CERSAI_MODIFY);

			if (totalRecords > 0) {
				new HeaderSegment(writer, ch).write();
			} else {
				MessageUtil.showMessage("No Records Found For Modification");
				return;
			}

			fileInfo = new CersaiFileInfo();
			fileInfo.setFileName(cersaiFile.getName());
			fileInfo.setTotalRecords(totalRecords);
			fileInfo.setProcessedRecords(processedRecords);
			fileInfo.setSuccessCount(successCount);
			fileInfo.setFailedCount(failedCount);
			fileInfo.setRemarks(updateRemarks());
			fileInfo.setFileLocation(filePath);
			fileInfo.setDownloadType(CersaiConstants.CERSAI_MODIFY);

			cersaiService.logFileInfo(fileInfo);
			headerId = fileInfo.getId();

			// updating the Cersai_File_Info
			cersaiService.updateFileStatus(fileInfo);

			String batchRef = cersaiService.generateBatchRef();

			int serialNo = 1;
			for (String collateralRef : collateralList) {
				EXTRACT_STATUS.setProcessedRecords(processedRecords++);
				serialNo = processModifyFile(writer, ch, batchRef, serialNo, collateralRef);
			}
		} catch (Exception e) {
			EXTRACT_STATUS.setStatus("F");
			logger.error(Literal.EXCEPTION, e);
		} finally {
			writer.close();
		}

		if ("F".equals(EXTRACT_STATUS.getStatus())) {
			return;
		}

		EXTRACT_STATUS.setStatus("S");

		String remarks = updateRemarks();
		fileInfo.setRemarks(remarks);
		fileInfo.setStatus(EXTRACT_STATUS.getStatus());
		fileInfo.setTotalRecords(totalRecords);
		fileInfo.setProcessedRecords(processedRecords);
		fileInfo.setFailedCount(failedCount);
		fileInfo.setSuccessCount(successCount);
		fileInfo.setRemarks(remarks);

		cersaiService.updateFileStatus(fileInfo);

		logger.debug(Literal.LEAVING);
	}

	private int processModifyFile(final CSVWriter writer, CersaiHeader ch, String batchRef, int serialNo,
			String collateralRef) {
		try {
			CersaiModifyCollDetails ccDtl = cersaiService.processModifyCollaterals(collateralRef, ch.getBatchId(),
					serialNo, batchRef);
			new ModifyCollDetails(writer, ccDtl).write();
			serialNo++;

			EXTRACT_STATUS.setSuccessRecords(successCount++);
		} catch (Exception e) {
			EXTRACT_STATUS.setFailedRecords(failedCount++);
			EXTRACT_STATUS.setStatus("F");
			cersaiService.logFileInfoException(headerId, String.valueOf(collateralRef), e.getMessage());
			logger.error(Literal.EXCEPTION, e);
		}
		return serialNo;
	}

	public void generateSatisfactionReport() throws IOException {
		logger.debug(Literal.ENTERING);

		initlize();
		CersaiChargeHolder cch = cersaiService.getChargeHolderDetails();
		filePath = cch.getFilePath();
		File cersaiFile = createFile();

		final CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter(cersaiFile)));
		writer.setFieldNamesInFirstRow(false);
		writer.setFieldSeparator('|');
		writer.open();
		try {

			List<String> collateralList = cersaiService.getSatisfyingRecords(CersaiConstants.CERSAI_SATISFY);

			totalRecords = collateralList.size();

			CersaiHeader ch = cersaiService.processFileHeader(totalRecords, CersaiConstants.CERSAI_SATISFY);

			if (totalRecords > 0) {
				new HeaderSegment(writer, ch).write();
			} else {
				MessageUtil.showMessage("No Records Found For Satisfaction");
				return;
			}

			fileInfo = new CersaiFileInfo();
			fileInfo.setFileName(cersaiFile.getName());
			fileInfo.setTotalRecords(totalRecords);
			fileInfo.setProcessedRecords(processedRecords);
			fileInfo.setSuccessCount(successCount);
			fileInfo.setFailedCount(failedCount);
			fileInfo.setFileLocation(filePath);
			fileInfo.setRemarks(updateRemarks());
			fileInfo.setDownloadType(CersaiConstants.CERSAI_SATISFY);

			cersaiService.logFileInfo(fileInfo);
			headerId = fileInfo.getId();

			// updating the Cersai_File_Info
			cersaiService.updateFileStatus(fileInfo);

			int serialNo = 1;
			String batchRef = cersaiService.generateBatchRef();

			for (String collateralRef : collateralList) {
				serialNo = processSatisfyFile(writer, ch, serialNo, batchRef, collateralRef);
			}
		} catch (Exception e) {
			EXTRACT_STATUS.setStatus("F");
			logger.error(Literal.EXCEPTION, e);
		} finally {
			writer.close();
		}

		if ("F".equals(EXTRACT_STATUS.getStatus())) {
			return;
		}

		EXTRACT_STATUS.setStatus("S");

		String remarks = updateRemarks();
		fileInfo.setRemarks(remarks);
		fileInfo.setStatus(EXTRACT_STATUS.getStatus());
		fileInfo.setTotalRecords(totalRecords);
		fileInfo.setProcessedRecords(processedRecords);
		fileInfo.setFailedCount(failedCount);
		fileInfo.setSuccessCount(successCount);
		fileInfo.setRemarks(remarks);

		cersaiService.updateFileStatus(fileInfo);

		logger.debug(Literal.LEAVING);
	}

	private int processSatisfyFile(final CSVWriter writer, CersaiHeader ch, int serialNo, String batchRef,
			String collateralRef) {
		try {
			EXTRACT_STATUS.setProcessedRecords(processedRecords++);

			CersaiSatisfyCollDetails ccDtl = cersaiService.processSatisfyCollaterals(collateralRef, ch.getBatchId(),
					serialNo, batchRef);
			new SatisfyCollDetails(writer, ccDtl).write();
			serialNo++;

			EXTRACT_STATUS.setSuccessRecords(successCount++);
		} catch (Exception e) {
			EXTRACT_STATUS.setFailedRecords(failedCount++);
			EXTRACT_STATUS.setStatus("F");
			cersaiService.logFileInfoException(headerId, String.valueOf(collateralRef), e.getMessage());
			logger.error(Literal.EXCEPTION, e);
		}
		return serialNo;
	}

	private List<CersaiBorrowers> processBorrowers(List<CersaiBorrowers> borrowers,
			List<CersaiAssetOwners> assetOwners) {
		for (int cb = 0; cb < borrowers.size(); cb++) {
			for (int cao = 0; cao < assetOwners.size(); cao++) {
				long brId = borrowers.get(cb).getCustId();
				long ownId = borrowers.get(cb).getCustId();
				if (brId == ownId) {
					borrowers.get(cb).setAssetOwner(true);
					break;
				}
			}
		}
		return borrowers;
	}

	private List<CersaiAssetOwners> processAssetOwners(List<CersaiBorrowers> borrowers,
			List<CersaiAssetOwners> assetOwners) {
		List<CersaiAssetOwners> newAssetOwners = new ArrayList<CersaiAssetOwners>();
		newAssetOwners.addAll(assetOwners);
		for (int cao = 0; cao < assetOwners.size(); cao++) {
			for (int cb = 0; cb < borrowers.size(); cb++) {
				long brId = borrowers.get(cb).getCustId();
				long ownId = assetOwners.get(cao).getCustId();
				if (brId == ownId) {
					newAssetOwners.remove(assetOwners.get(cao));
					break;
				}
			}
		}
		return newAssetOwners;
	}

	private File createFile() throws IOException {
		logger.debug("Creating the file");
		File reportName = null;
		String reportLocation = filePath;

		File directory = new File(reportLocation);

		String fileSeq = cersaiService.generateFileSeq();

		if (!directory.exists()) {
			directory.mkdirs();
		}
		StringBuilder builder = new StringBuilder(reportLocation);
		builder.append(File.separator);
		builder.append("crs_JC137");
		builder.append("_");
		builder.append(fileSeq);
		builder.append("_");
		builder.append(DateUtil.getSysDate("yyyyMMdd"));
		builder.append(DateUtil.getSysDate("HHmmss"));
		builder.append(".DAT");
		reportName = new File(builder.toString());
		boolean status = reportName.createNewFile();

		if (status) {
			logger.debug("File created sucessfully");
		} else {
			logger.debug("Invalid file path");
		}

		return reportName;
	}

	private void initlize() {
		totalRecords = 0;
		processedRecords = 0;
		successCount = 0;
		failedCount = 0;
		EXTRACT_STATUS.reset();
	}

	private String updateRemarks() {
		StringBuilder remarks = new StringBuilder();
		if (failedCount > 0) {
			remarks.append("Completed with exceptions, total Records: ");
			remarks.append(totalRecords);
			remarks.append(", Sucess: ");
			remarks.append(successCount);
			remarks.append(", Failure: ");
			remarks.append(failedCount);

		} else {
			remarks.append("Completed successfully, total Records: ");
			remarks.append(totalRecords);
			remarks.append(", Sucess: ");
			remarks.append(successCount);
		}
		return remarks.toString();
	}

	public class HeaderSegment {
		private CSVWriter writer;
		private CersaiHeader ch;

		public HeaderSegment(CSVWriter writer, CersaiHeader header) {
			this.writer = writer;
			this.ch = header;
		}

		public void write() throws IOException {
			Record record = new Record();
			/* Segment Identifier */
			addField(record, ch.getFileHeader());

			/* File Type */
			addField(record, ch.getFileType());

			/* Record Count */
			addField(record, String.valueOf(ch.getTotalRecords()));

			/* File Date */
			addField(record, DateUtil.format(ch.getFileDate(), PennantConstants.DBDateFormat));

			writer.write(record);
		}
	}

	public class CollateralDetails {
		private CSVWriter writer;
		private List<CersaiAddCollDetails> ccDtl;

		public CollateralDetails(CSVWriter writer, List<CersaiAddCollDetails> details) {
			this.writer = writer;
			this.ccDtl = details;
		}

		public void write() throws IOException {
			for (CersaiAddCollDetails collateral : ccDtl) {
				Record record = new Record();

				addField(record, collateral.getRowType());
				addField(record, String.valueOf(collateral.getSerialNumber()));
				addField(record, String.valueOf(collateral.getNoOfBrrowers()));
				addField(record, String.valueOf(collateral.getNoOfAssetOwners()));
				addField(record, String.valueOf(collateral.getNoOfConsortiumMemebers()));
				addField(record, String.valueOf(collateral.getSiTypeId()));
				addField(record, collateral.getSiTypeOthers());
				addField(record, collateral.getFinancingTypeId());
				addField(record, DateUtil.format(collateral.getSiCreationDate(), PennantConstants.DBDateFormat));
				addField(record, PennantApplicationUtil.formateAmount(collateral.getTotalSecuredAmt(), 2));
				addField(record, collateral.getEntityMISToken());
				addField(record, collateral.getNarration());
				addField(record, collateral.getTypeOfCharge());
				addField(record, String.valueOf(collateral.isTpm() ? 1 : 0));
				addField(record, collateral.getBatchRefNumber());

				writer.write(record);
			}

		}
	}

	public class BorrowerSegment {
		private CSVWriter writer;
		private List<CersaiBorrowers> brrs;

		public BorrowerSegment(CSVWriter writer, List<CersaiBorrowers> borrowers) {
			this.writer = writer;
			this.brrs = borrowers;
		}

		public void write() throws IOException {
			for (CersaiBorrowers borrower : brrs) {
				Record record = new Record();

				addField(record, borrower.getRowType());
				addField(record, String.valueOf(borrower.getSerialNumber()));
				addField(record, borrower.getBorrowerType());
				int isAssetOwner = borrower.isAssetOwner() ? 1 : 0;
				addField(record, String.valueOf(isAssetOwner));
				addField(record,
						String.valueOf(borrower.getBorrowerUidType() == 0 ? "" : borrower.getBorrowerUidType()));
				addField(record, borrower.getBorrowerUidValue());
				addField(record, borrower.getBorrowerPAN());
				addField(record, String.valueOf(borrower.getBorrowerCKYC() == 0 ? "" : borrower.getBorrowerCKYC()));
				addField(record, borrower.getBorrowerName());
				addField(record, DateUtil.format(borrower.getBorrowerRegDate(), PennantConstants.DBDateFormat));
				addField(record, borrower.getBorrowerRegNumber());
				addField(record, borrower.getIndividualPan());
				addField(record, String.valueOf(borrower.getIndividualCKYC() == 0 ? "" : borrower.getIndividualCKYC()));
				addField(record, borrower.getGender());
				addField(record, borrower.getIndividualName());
				addField(record, borrower.getFatherMotherName());
				addField(record, DateUtil.format(borrower.getDob(), PennantConstants.DBDateFormat));
				addField(record, String.valueOf(borrower.getMobileNo() == 0 ? "" : borrower.getMobileNo()));
				addField(record, borrower.getEmail());
				addField(record, borrower.getAddressLine1());
				addField(record, borrower.getAddressLine2());
				addField(record, borrower.getAddressLine3());
				addField(record, borrower.getCity());
				addField(record, borrower.getDistrict());
				addField(record, borrower.getState());
				addField(record, String.valueOf(borrower.getPincode() == 0 ? "" : borrower.getPincode()));
				addField(record, borrower.getCountry());

				writer.write(record);
			}

		}
	}

	public class AssetOwnerSegment {
		private CSVWriter writer;
		private List<CersaiAssetOwners> ao;

		public AssetOwnerSegment(CSVWriter writer, List<CersaiAssetOwners> owners) {
			this.writer = writer;
			this.ao = owners;
		}

		public void write() throws IOException {
			for (CersaiAssetOwners owner : ao) {
				Record record = new Record();

				addField(record, owner.getRowType());
				addField(record, String.valueOf(owner.getSerialNumber()));
				addField(record, owner.getAssetOwnerType());
				addField(record, String.valueOf(owner.getAssetOwnerUidType() == 0 ? "" : owner.getAssetOwnerUidType()));
				addField(record, owner.getAssetOwnerUidValue());
				addField(record, owner.getAssetOwnerPAN());
				addField(record, String.valueOf(owner.getAssetOwnerCKYC() == 0 ? "" : owner.getAssetOwnerCKYC()));
				addField(record, owner.getAssetOwnerName());
				addField(record, DateUtil.format(owner.getAssetOwnerRegDate(), PennantConstants.DBDateFormat));
				addField(record, owner.getAssetOwnerRegNumber());
				addField(record, owner.getIndividualPan());
				addField(record, String.valueOf(owner.getIndividualCKYC() == 0 ? "" : owner.getIndividualCKYC()));
				addField(record, owner.getGender());
				addField(record, owner.getIndividualName());
				addField(record, owner.getFatherMotherName());
				addField(record, DateUtil.format(owner.getDob(), PennantConstants.DBDateFormat));
				addField(record, String.valueOf(owner.getMobileNo() == 0 ? "" : owner.getMobileNo()));
				addField(record, owner.getEmail());
				addField(record, owner.getAddressLine1());
				addField(record, owner.getAddressLine2());
				addField(record, owner.getAddressLine3());
				addField(record, owner.getCity());
				addField(record, owner.getDistrict());
				addField(record, owner.getState());
				addField(record, String.valueOf(owner.getPincode() == 0 ? "" : owner.getPincode()));
				addField(record, owner.getCountry());

				writer.write(record);
			}

		}
	}

	public class SatisfyCollDetails {
		private CSVWriter writer;
		private CersaiSatisfyCollDetails ccDtl;

		public SatisfyCollDetails(CSVWriter writer, CersaiSatisfyCollDetails details) {
			this.writer = writer;
			this.ccDtl = details;
		}

		public void write() throws IOException {
			Record record = new Record();

			addField(record, ccDtl.getRowType());
			addField(record, String.valueOf(ccDtl.getSerialNumber()));
			addField(record, String.valueOf(ccDtl.getSiId()));
			addField(record, String.valueOf(ccDtl.getAssetId()));
			addField(record, DateUtil.format(ccDtl.getSatisfactionDate(), PennantConstants.DBDateFormat));
			addField(record, ccDtl.getReasonCode());
			addField(record, ccDtl.getReasonOthers());
			addField(record, ccDtl.getBatchRefNumber());
			addField(record, ccDtl.getReasonForDelay());

			writer.write(record);
		}

	}

	public class MovableSegemnt {
		private CSVWriter writer;
		private CersaiMovableAsset movAsset;

		public MovableSegemnt(CSVWriter writer, CersaiMovableAsset detail) {
			this.writer = writer;
			this.movAsset = detail;
		}

		public void write() throws IOException {
			Record record = new Record();

			addField(record, movAsset.getRowType());
			addField(record, String.valueOf(movAsset.getAssetCategoryId()));
			addField(record, String.valueOf(movAsset.getAssetTypeId()));
			addField(record,
					String.valueOf(movAsset.getAssetTypeOthers() == null ? "" : movAsset.getAssetTypeOthers()));
			addField(record, String.valueOf(movAsset.getAssetSubTypeId()));
			addField(record, movAsset.getAssetUniqueId());
			addField(record, movAsset.getAssetSerialNumber());
			addField(record, movAsset.getAssetDescription());
			addField(record, movAsset.getAssetMake());
			addField(record, movAsset.getAssetModel());
			addField(record, movAsset.getAddressLine1());
			addField(record, movAsset.getAddressLine2());
			addField(record, movAsset.getAddressLine3());
			addField(record, movAsset.getCity());
			addField(record, movAsset.getDistrict());
			addField(record, movAsset.getState());
			addField(record, String.valueOf(movAsset.getPincode() == 0 ? "" : movAsset.getPincode()));
			addField(record, movAsset.getCountry());

			writer.write(record);
		}

	}

	public class ImMovableSegemnt {
		private CSVWriter writer;
		private CersaiImmovableAsset immovAsset;

		public ImMovableSegemnt(CSVWriter writer, CersaiImmovableAsset detail) {
			this.writer = writer;
			this.immovAsset = detail;
		}

		public void write() throws IOException {
			Record record = new Record();

			addField(record, immovAsset.getRowType());
			addField(record, String.valueOf(immovAsset.getAssetCategoryId()));
			addField(record, String.valueOf(immovAsset.getAssetTypeId()));
			addField(record,
					String.valueOf(immovAsset.getAssetTypeOthers() == null ? "" : immovAsset.getAssetTypeOthers()));
			addField(record, String.valueOf(immovAsset.getAssetSubTypeId()));
			addField(record, immovAsset.getAssetUniqueId());
			addField(record, immovAsset.getAssetDescription());
			addField(record, immovAsset.getSurveyNumber());
			addField(record, immovAsset.getPlotNumber());
			addField(record, immovAsset.getAssetArea());
			addField(record, immovAsset.getAssetAreaUnit());
			addField(record, immovAsset.getHouseNumber());
			addField(record, immovAsset.getFloorNumber());
			addField(record, immovAsset.getBuildingName());
			addField(record, immovAsset.getProjectName());
			addField(record, immovAsset.getStreetName());
			addField(record, immovAsset.getPocket());
			addField(record, immovAsset.getLocality());
			addField(record, immovAsset.getCity());
			addField(record, immovAsset.getDistrict());
			addField(record, immovAsset.getState());
			addField(record, String.valueOf(immovAsset.getPincode() == 0 ? "" : immovAsset.getPincode()));
			addField(record, immovAsset.getCountry());
			addField(record, immovAsset.getLatitudeLongitude1());
			addField(record, immovAsset.getLatitudeLongitude2());
			addField(record, immovAsset.getLatitudeLongitude3());
			addField(record, immovAsset.getLatitudeLongitude4());

			writer.write(record);
		}

	}

	public class IntangibleSegemnt {
		private CSVWriter writer;
		private CersaiIntangibleAsset intAsset;

		public IntangibleSegemnt(CSVWriter writer, CersaiIntangibleAsset detail) {
			this.writer = writer;
			this.intAsset = detail;
		}

		public void write() throws IOException {
			Record record = new Record();

			addField(record, intAsset.getRowType());
			addField(record, String.valueOf(intAsset.getAssetCategoryId()));
			addField(record, String.valueOf(intAsset.getAssetTypeId()));
			addField(record,
					String.valueOf(intAsset.getAssetTypeOthers() == null ? "" : intAsset.getAssetTypeOthers()));
			addField(record, String.valueOf(intAsset.getAssetSubTypeId()));
			addField(record, intAsset.getAssetUniqueId());
			addField(record, intAsset.getAssetSerialNumber());
			addField(record, intAsset.getAssetDescription());
			addField(record, intAsset.getDairyNumber());
			addField(record, intAsset.getAssetClass());
			addField(record, intAsset.getAssetTitle());
			addField(record, intAsset.getPatentNumber());
			addField(record, DateUtil.format(intAsset.getPatentDate(), PennantConstants.DBDateFormat));
			addField(record, intAsset.getLicenseNumber());
			addField(record, intAsset.getLicenseIssuingAuthority());
			addField(record, intAsset.getLicenseCategory());
			addField(record, intAsset.getDesignNumber());
			addField(record, intAsset.getDesignClass());
			addField(record, intAsset.getTradeMarkAppNumber());
			addField(record, DateUtil.format(intAsset.getTradeMarkAppDate(), PennantConstants.DBDateFormat));

			writer.write(record);
		}

	}

	public class ModifyCollDetails {
		private CSVWriter writer;
		private CersaiModifyCollDetails ccDtl;

		public ModifyCollDetails(CSVWriter writer, CersaiModifyCollDetails detail) {
			this.writer = writer;
			this.ccDtl = detail;
		}

		public void write() throws IOException {
			Record record = new Record();

			addField(record, ccDtl.getRowType());
			addField(record, String.valueOf(ccDtl.getSerialNumber()));
			addField(record, String.valueOf(ccDtl.getSiId()));
			addField(record, DateUtil.format(ccDtl.getDocExecuDate(), PennantConstants.DBDateFormat));
			addField(record, ccDtl.getModifyType());
			addField(record, ccDtl.getFinancingTypeId());
			addField(record, PennantApplicationUtil.formateAmount(ccDtl.getTotalSecuredAmt(), 2));
			addField(record, ccDtl.getTypeOfCharge());
			addField(record, ccDtl.getEntityMISToken());
			addField(record, ccDtl.getNarration());
			addField(record, String.valueOf(ccDtl.getSiTypeId()));
			addField(record, ccDtl.getEntityCode());
			addField(record, ccDtl.getOfficeCode());
			addField(record, ccDtl.getOfficeName());
			addField(record, ccDtl.getAddressLine1());
			addField(record, ccDtl.getAddressLine2());
			addField(record, ccDtl.getAddressLine3());
			addField(record, ccDtl.getCity());
			addField(record, ccDtl.getDistrict());
			addField(record, ccDtl.getState());
			addField(record, String.valueOf(ccDtl.getPincode() == 0 ? "" : ccDtl.getPincode()));
			addField(record, ccDtl.getCountry());
			addField(record, String.valueOf(ccDtl.getBatchRefNumber()));

			writer.write(record);
		}

	}

	public class ChargeHolder {
		private CSVWriter writer;
		private CersaiChargeHolder cch;

		public ChargeHolder(CSVWriter writer, CersaiChargeHolder detail) {
			this.writer = writer;
			this.cch = detail;
		}

		public void write() throws IOException {
			Record record = new Record();

			addField(record, "CHG");
			addField(record, cch.getOfficeCode());
			addField(record, cch.getOfficeName());
			addField(record, cch.getAddressLine1());
			addField(record, cch.getAddressLine2());
			addField(record, cch.getAddressLine3());
			addField(record, cch.getCity());
			addField(record, cch.getDistrict());
			addField(record, cch.getState());
			addField(record, String.valueOf(cch.getPincode() == 0 ? "" : cch.getPincode()));
			addField(record, cch.getCountry());

			writer.write(record);
		}

	}

	private void addField(Record record, String value) {
		if (StringUtils.isNotEmpty(value)) {
			record.addField().setValue(value);
		} else {
			record.addField();
		}
	}

	private void addField(Record record, BigDecimal value) {
		if (value == null) {
			value = BigDecimal.ZERO;
		}

		value = value.setScale(0, BigDecimal.ROUND_DOWN);

		addField(record, value.toString());
	}

	public CERSAIService getCersaiService() {
		return cersaiService;
	}

	public void setCersaiService(CERSAIService cersaiService) {
		this.cersaiService = cersaiService;
	}

}
