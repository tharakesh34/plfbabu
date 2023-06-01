package com.pennant.backend.service.ckyc.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.FileSystemUtils;

import com.pennant.backend.dao.ckyc.CKYCDAO;
import com.pennant.backend.model.cky.CKYCDtl20;
import com.pennant.backend.model.cky.CKYCDtl30;
import com.pennant.backend.model.cky.CKYCDtl60;
import com.pennant.backend.model.cky.CKYCDtl70;
import com.pennant.backend.model.cky.CKYCHeader;
import com.pennant.backend.model.cky.CKYCLog;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.ckyc.CKYCService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.external.dms.model.ExternalDocument;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.DocumentManagementService;

public class CKYCServiceImpl extends GenericService implements CKYCService {

	private static Logger logger = LogManager.getLogger(CKYCServiceImpl.class);
	private static int emailRemove;
	private static int addrRemove;
	private static int phoneRemove;
	private CKYCDAO ckycDAO;
	protected DocumentManagementService dmsManagementService;

	public void setCkycDAO(CKYCDAO ckycDAO) {
		this.ckycDAO = ckycDAO;
	}

	public boolean prepareData(List<Long> id) {
		boolean flag = false;
		if (cleanData()) {
			CKYCLog logFile = new CKYCLog();
			CKYCHeader ckycHeader = new CKYCHeader();
			int lineNo = 1;

			long batchNo = ckycDAO.getBatchNO();
			String batch = updateBatchNo(batchNo);
			ckycHeader.setRecordType(10);
			ckycHeader.setBatchNo(batch);
			ckycHeader.setFiCode(App.getProperty("external.interface.cKYC.FICode"));
			ckycHeader.setRegionCode(App.getProperty("external.interface.cKYC.RegionCode"));
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ckycHeader.setCreateDate(timestamp);
			ckycHeader.setVersion(App.getProperty("external.interface.cKYC.Version"));
			ckycHeader.setHeaderFiller1("");
			ckycHeader.setHeaderFiller2("");
			ckycHeader.setHeaderFiller3("");
			ckycHeader.setHeaderFiller4("");
			List<CKYCDtl20> ckycDtl20s = new ArrayList<>();
			String fname = null;
			for (Long custId : id) {
				Customer customer = ckycDAO.getCustomerDetail(custId);
				CKYCDtl20 ckycdtl20 = getDetails20(customer);
				logFile.setCustId(ckycdtl20.getCustId());
				logFile.setCustCif(ckycdtl20.getCustCif());
				ckycdtl20.setLineNo(lineNo);
				List<CKYCDtl30> ckyc30 = null;
				List<CKYCDtl60> ckyc60 = null;
				List<CKYCDtl70> ckyc70 = null;
				if (ckycdtl20.getApplicationType().equalsIgnoreCase("01")) {
					ckyc30 = getDetails30(custId, ckycdtl20.getCkycNo());
					ckyc60 = getDetails60(custId, ckycdtl20.getCkycNo());
					ckyc70 = getDetails70(customer, ckycdtl20.getCkycNo());
				} else {
					if (StringUtils.equalsIgnoreCase(ckycdtl20.getApplicationType(), "03")) {
						if (StringUtils.equalsIgnoreCase(ckycdtl20.getImgUpdFlag(), "01")) {
							ckyc70 = getDetails70(customer, ckycdtl20.getCkycNo());
						}
						if (StringUtils.equalsIgnoreCase(ckycdtl20.getIdentityUpdFlag(), "01")) {
							ckyc30 = getDetails30(custId, ckycdtl20.getCkycNo());
						}
						if ((StringUtils.equalsIgnoreCase(ckycdtl20.getAddrUpdFlag(), "01"))
								|| (StringUtils.equalsIgnoreCase(ckycdtl20.getContactUpdFlag(), "01"))) {
							ckyc60 = getDetails60(custId, ckycdtl20.getCkycNo());
						}

						if (!StringUtils.equalsIgnoreCase(ckycdtl20.getContactUpdFlag(), "01")
								&& !StringUtils.equalsIgnoreCase(ckycdtl20.getIdentityUpdFlag(), "01")
								&& !StringUtils.equalsIgnoreCase(ckycdtl20.getAddrUpdFlag(), "01")
								&& !StringUtils.equalsIgnoreCase(ckycdtl20.getContactUpdFlag(), "01")
								&& !StringUtils.equals(ckycdtl20.getNameUpdFlag(), "01")
								&& !StringUtils.equalsIgnoreCase(ckycdtl20.getPersonalUpdFlag(), "01")) {
							continue;
						}
					}
				}

				if (ckyc30 != null && ckyc30.size() != 0)
					ckycdtl20.setNoIdDetail(updateDigit(ckyc30.size()));
				else
					ckycdtl20.setNoIdDetail("");
				if (ckyc60 != null && ckyc60.size() != 0)
					ckycdtl20.setNoLocalAddr(updateDigit(ckyc60.size()));
				else
					ckycdtl20.setNoLocalAddr("");
				if (ckyc70 != null && ckyc70.size() != 0)
					ckycdtl20.setNoImg(updateDigit(ckyc70.size()));
				else
					ckycdtl20.setNoImg("");

				ckycdtl20.setNoControllingPersonOutsideInd("");
				ckycdtl20.setNoRelatedPeople("");

				int save = saveDtl20(ckycdtl20);

				if (save > 0) {
					if (ckyc30 != null && !ckyc30.isEmpty())
						saveDtl30(ckyc30);
					if (ckyc60 != null && !ckyc60.isEmpty())
						saveDtl60(ckyc60);
					if (ckyc70 != null && !ckyc70.isEmpty())
						saveDtl70(ckyc70);
					logFile.setRowNo(lineNo);
					Format formatter = new SimpleDateFormat("ddMMyyyyhhMMss");
					String date = formatter.format(ckycHeader.getCreateDate());
					StringBuilder fileNameBuilder = new StringBuilder();
					fileNameBuilder.append(ckycHeader.getFiCode() + "_" + ckycHeader.getRegionCode() + "_");
					fileNameBuilder.append(date + "_" + App.getProperty("external.interface.cKYC.UserId") + "_U"
							+ ckycHeader.getBatchNo());
					if (fname == null) {
						fname = fileNameBuilder.toString();
					}
					fileNameBuilder.append(".txt");
					String fileName = fileNameBuilder.toString();
					logFile.setFileName(fileName);
					logFile.setCkycNo(ckycdtl20.getCkycNo());
					if (StringUtils.equalsIgnoreCase(ckycdtl20.getApplicationType(), "01")) {
						logFile.setCustId(ckycdtl20.getCustId());
						logFile.setCustCif(ckycdtl20.getCustCif());
						logFile.setCustsalutationcode(ckycdtl20.getCustSalutationCode());
						logFile.setCustfname(ckycdtl20.getCustFName());
						logFile.setCustmname(ckycdtl20.getCustMName());
						logFile.setCustlname(ckycdtl20.getCustLName());
						logFile.setCustfatherName(ckycdtl20.getFatherOrSpouseFirstName());
						logFile.setCustgendercode(ckycdtl20.getCustGenderCode());
						logFile.setCustmaritalsts(ckycdtl20.getCustMaritalsts());
						logFile.setCustnationality(ckycdtl20.getCustNationality());
						logFile.setCustdob(ckycdtl20.getCustDob());
						logFile.setOccupationtype(ckycdtl20.getOccupationType());

					}
					if (logFile != null && StringUtils.equalsIgnoreCase(ckycdtl20.getApplicationType(), "01")) {
						ckycDAO.saveFile(logFile);
					} else {
						if (StringUtils.equalsIgnoreCase(ckycdtl20.getNameUpdFlag(), "01")) {
							logFile.setCustsalutationcode(ckycdtl20.getCustSalutationCode());
							logFile.setCustfname(ckycdtl20.getCustFName());
							logFile.setCustmname(ckycdtl20.getCustMName());
							logFile.setCustlname(ckycdtl20.getCustLName());
							ckycDAO.updatNameFlag(logFile);
						}
						if (StringUtils.equalsIgnoreCase(ckycdtl20.getPersonalUpdFlag(), "01")) {
							logFile.setCustfatherName(ckycdtl20.getFatherOrSpouseFirstName());
							logFile.setCustgendercode(ckycdtl20.getCustGenderCode());
							logFile.setCustmaritalsts(ckycdtl20.getCustMaritalsts());
							logFile.setCustnationality(ckycdtl20.getCustNationality());
							logFile.setCustdob(ckycdtl20.getCustDob());
							logFile.setOccupationtype(ckycdtl20.getOccupationType());
							ckycDAO.updatPersonalFlag(logFile);
						}
						if (StringUtils.equalsIgnoreCase(ckycdtl20.getAddrUpdFlag(), "01")) {
							ckycDAO.addressUpdateFlag(logFile);
						}
						if (StringUtils.equalsIgnoreCase(ckycdtl20.getContactUpdFlag(), "01")) {
							ckycDAO.phoneUpdateFlag(logFile);
							ckycDAO.emailUpdateFlag(logFile);
						}
						if (StringUtils.equalsIgnoreCase(ckycdtl20.getImgUpdFlag(), "01")) {
							ckycDAO.docUpdateFlag(logFile);
						}
					}

				}

				ckycDtl20s.add(ckycdtl20);
				lineNo++;

			}
			ckycHeader.setTotDetailsRecord(ckycDtl20s.size());
			Format formatter = new SimpleDateFormat("dd-MM-yyyy");
			String date = formatter.format(ckycHeader.getCreateDate());
			File fileFolder = null;
			if (fname != null) {
				File folder = new File(App.getProperty("external.interface.cKYC.FileLoaction"));
				if (!folder.exists())
					folder.mkdirs();
				fileFolder = new File(folder.getPath() + "/" + fname);
				fileFolder.mkdir();
			}
			File mainfolder = new File(fileFolder.getPath() + "/" + fname);
			if (!mainfolder.exists()) {
				boolean folderData = false;
				if (mainfolder.mkdir()) {
					File file = new File(mainfolder.getPath() + "/" + fname + ".txt");

					try (FileOutputStream fos = new FileOutputStream(file);
							BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {

						StringBuilder ckycHeaderBuilder = new StringBuilder();
						ckycHeaderBuilder.append(numValueCheck(ckycHeader.getRecordType()) + "|"
								+ stringValueCheck(ckycHeader.getBatchNo()) + "|");
						ckycHeaderBuilder.append(stringValueCheck(ckycHeader.getFiCode()) + "|"
								+ stringValueCheck(ckycHeader.getRegionCode()) + "|"
								+ numValueCheck(ckycHeader.getTotDetailsRecord()) + "|" + date + "|");
						ckycHeaderBuilder.append(stringValueCheck(ckycHeader.getVersion()) + "|"
								+ stringValueCheck(ckycHeader.getHeaderFiller1()) + "|"
								+ stringValueCheck(ckycHeader.getHeaderFiller2()) + "|");
						ckycHeaderBuilder.append(stringValueCheck(ckycHeader.getHeaderFiller3()) + "|"
								+ stringValueCheck(ckycHeader.getHeaderFiller4()));
						bw.write(ckycHeaderBuilder.toString() + "\n");
						int rowNo = 1;
						for (Long custId : id) {
							CKYCDtl20 ckycdtl20 = ckycDAO.getDtl20(custId);
							StringBuilder ckycDtl20Builder = new StringBuilder();

							if (ckycdtl20 != null) {
								folderData = true;
								List<CKYCDtl30> ckyc30 = ckycDAO.getDtls30(custId);
								List<CKYCDtl60> ckyc60 = ckycDAO.getDtl60(custId);
								List<CKYCDtl70> ckyc70 = ckycDAO.getDtl70(custId);
								String ckycNo = ckycdtl20.getCkycNo();
								if (ckycNo == null) {
									ckycdtl20.setCkycNo(ckycdtl20.getCustCif());

								}
								ckycDtl20Builder.append(numValueCheck(ckycdtl20.getRecordType()) + "|");
								ckycDtl20Builder.append(numValueCheck(ckycdtl20.getLineNo()) + "|"
										+ stringValueCheck(ckycdtl20.getApplicationType()) + "|"
										+ stringValueCheck(ckycdtl20.getBranchCode()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getNameUpdFlag()) + "|"
										+ stringValueCheck(ckycdtl20.getPersonalUpdFlag()) + "|"
										+ stringValueCheck(ckycdtl20.getAddrUpdFlag()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getContactUpdFlag()) + "|"
										+ stringValueCheck(ckycdtl20.getRemarksUpdFlag()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getKycUpdFlag()) + "|"
										+ stringValueCheck(ckycdtl20.getIdentityUpdFlag()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getRelatedUpdFlag()) + "|"
										+ stringValueCheck(ckycdtl20.getControlPersonFlag()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getImgUpdFlag()) + "|"
										+ stringValueCheck(ckycdtl20.getConstitutionType()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getAccHolderFlag()) + "|"
										+ stringValueCheck(ckycdtl20.getAccHolderType()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getAccType()) + "|"
										+ stringValueCheck(ckycdtl20.getCkycNo()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getCustSalutationCode()) + "|"
										+ stringValueCheck(ckycdtl20.getCustFName()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getCustMName()) + "|"
										+ stringValueCheck(ckycdtl20.getCustLName()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getCustFullName()) + "|"
										+ stringValueCheck(ckycdtl20.getCustMaidenSalutationCode()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getCustMaidenFName()) + "|"
										+ stringValueCheck(ckycdtl20.getCustMaidenMName()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getCustMaidenLName()) + "|"
										+ stringValueCheck(ckycdtl20.getCustMaidenFullName()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getFatherOrSpouse()) + "|"
										+ stringValueCheck(ckycdtl20.getFatherSalutation()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getFatherOrSpouseFirstName()) + "|"
										+ stringValueCheck(ckycdtl20.getFatherOrSpouseMiddleName()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getFatherOrSpouseLastName()) + "|"
										+ stringValueCheck(ckycdtl20.getCustfatherorSpouseFullName()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getMotherSalutationCode()) + "|"
										+ stringValueCheck(ckycdtl20.getMotherFName()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getMotherMName()) + "|"
										+ stringValueCheck(ckycdtl20.getMotherLName()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getMotherFullName()) + "|"
										+ stringValueCheck(ckycdtl20.getCustGenderCode()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getCustMaritalsts()) + "|"
										+ stringValueCheck(ckycdtl20.getCustNationality()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getOccupationType()) + "|"
										+ dateEmptyorNot(ckycdtl20.getCustDob()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getPlaceOfIncorporation()) + "|"
										+ dateEmptyorNot(ckycdtl20.getDtCommencementBussiness()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getCountryOfIncorporation()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getCountryofResidenceasPerTaxLaw())
										+ "|" + stringValueCheck(ckycdtl20.getOtherConstIdType()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getTin()) + "|"
										+ stringValueCheck(ckycdtl20.getTinIssuingCountry()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getCustcrcpr()) + "|"
										+ stringValueCheck(ckycdtl20.getResStatus()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getCustrestaxJurioutsideIndiaFlag())
										+ "|" + stringValueCheck(ckycdtl20.getJuriresidence()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getJuriTin()) + "|"
										+ stringValueCheck(ckycdtl20.getJuriCob()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getJuriPob()) + "|"
										+ stringValueCheck(ckycdtl20.getPmtAddrType()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getPmtAddrLine1()) + "|"
										+ stringValueCheck(ckycdtl20.getPmtAddrLine2()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getPmtAddrLine3()) + "|"
										+ stringValueCheck(ckycdtl20.getPmtAddrCity()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getPmtAddrDistrict()) + "|"
										+ stringValueCheck(ckycdtl20.getPmtAddrProvince()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getPmtAddrCountry()) + "|"
										+ stringValueCheck(ckycdtl20.getPmtAddrZip()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getPmtPofAddrsubmit()) + "|"
										+ stringValueCheck(ckycdtl20.getPmtPofAddrsubmit1()) + "|");

								ckycDtl20Builder.append(flagValue(ckycdtl20.isPmtAddrsameLocalFlag()) + "|"
										+ stringValueCheck(ckycdtl20.getLocalAddrType()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getLocalAddrLine1()) + "|"
										+ stringValueCheck(ckycdtl20.getLocalAddrLine2()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getLocalAddrLine3()) + "|"
										+ stringValueCheck(ckycdtl20.getLocalAddrCity()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getLocalAddrDistrict()) + "|"
										+ stringValueCheck(ckycdtl20.getLocalAddrProvince()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getLocalAddrCountry()) + "|"
										+ stringValueCheck(ckycdtl20.getLocalAddrZip()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getLocalPofAddrsubmit()) + "|"
										+ stringValueCheck(ckycdtl20.getJuriaddrsamepmtorLocalFlag()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getJuriAddrType()) + "|"
										+ stringValueCheck(ckycdtl20.getJuriAddrLine1()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getJuriAddrLine2()) + "|"
										+ stringValueCheck(ckycdtl20.getJuriAddrLine3()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getJuriAddrCity()) + "|"
										+ stringValueCheck(ckycdtl20.getJuriAddrProvince()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getJuriAddrCountry()) + "|"
										+ stringValueCheck(ckycdtl20.getJuriAddrZip()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getJuriPofAddrsubmit()) + "|"
										+ stringValueCheck(ckycdtl20.getResPhoneCode()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getResPhoneNo()) + "|"
										+ stringValueCheck(ckycdtl20.getOffPhoneCode()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getOffPhoneNo()) + "|"
										+ stringValueCheck(ckycdtl20.getMobPhoneCode()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getMobPhoneNo()) + "|"
										+ stringValueCheck(ckycdtl20.getFaxPhoneCode()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getFaxPhoneNo()) + "|"
										+ stringValueCheck(ckycdtl20.getEmailId()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getRemarks()) + "|"
										+ dateEmptyorNot(ckycdtl20.getKycDod()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getKycPod()) + "|"
										+ dateEmptyorNot(ckycdtl20.getKycVerificationDate()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getKycDocType()) + "|"
										+ stringValueCheck(ckycdtl20.getKycVName()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getKycVDesignation()) + "|"
										+ stringValueCheck(ckycdtl20.getKycVBranch()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getKycEMPCode()) + "|"
										+ stringValueCheck(ckycdtl20.getKycOrgCode()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getKycOrgName()) + "|"
										+ stringValueCheck(ckycdtl20.getNoIdDetail()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getNoRelatedPeople()) + "|"
										+ stringValueCheck(ckycdtl20.getNoControllingPersonOutsideInd()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getNoLocalAddr()) + "|"
										+ stringValueCheck(ckycdtl20.getNoImg()) + "|");

								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getErrorCode()) + "|"
										+ stringValueCheck(ckycdtl20.getFiller1()) + "|");
								ckycDtl20Builder.append(stringValueCheck(ckycdtl20.getFiller2()) + "|"
										+ stringValueCheck(ckycdtl20.getFiller3()) + "|"
										+ stringValueCheck(ckycdtl20.getFiller4()));
								bw.write(ckycDtl20Builder.toString() + '\n');

								if (ckyc30 != null && !ckyc30.isEmpty()) {
									for (CKYCDtl30 ckycdtl30 : ckyc30) {
										StringBuilder ckycDtl30Builder = new StringBuilder();
										if (!StringUtils.endsWithIgnoreCase(ckycdtl30.getIdNo(), null)
												&& !(ckycdtl30.getIdNo()).isEmpty()) {
											ckycDtl30Builder.append(numValueCheck(ckycdtl30.getRecordType()) + "|");

											ckycDtl30Builder.append(numValueCheck(ckycdtl30.getLineNo()) + "|"
													+ stringValueCheck(ckycdtl30.getIdType()) + "|"
													+ stringValueCheck(ckycdtl30.getIdNo()) + "|");

											ckycDtl30Builder.append(dateEmptyorNot(ckycdtl30.getExpDate()) + "|"
													+ stringValueCheck(ckycdtl30.getIdproofsubmit()) + "|"
													+ stringValueCheck(ckycdtl30.getIdVStatus()) + "|");

											ckycDtl30Builder.append(stringValueCheck(ckycdtl30.getFiller1()) + "|"
													+ stringValueCheck(ckycdtl30.getFiller2()) + "|"
													+ stringValueCheck(ckycdtl30.getFiller3()) + "|"
													+ stringValueCheck(ckycdtl30.getFiller4()));

											bw.write(ckycDtl30Builder.toString() + '\n');
										}
									}
								}
								if (ckyc60 != null && !ckyc60.isEmpty()) {
									for (CKYCDtl60 ckycdtl60 : ckyc60) {
										StringBuilder ckycDtl60Builder = new StringBuilder();

										ckycDtl60Builder.append(numValueCheck(ckycdtl60.getRecordType()) + "|"
												+ numValueCheck(ckycdtl60.getLineNo()) + "|");

										ckycDtl60Builder.append(stringValueCheck(ckycdtl60.getBranchCode()) + "|"
												+ stringValueCheck(ckycdtl60.getAddrType()) + "|"
												+ stringValueCheck(ckycdtl60.getLocalAddrLine1()) + "|"
												+ stringValueCheck(ckycdtl60.getLocalAddrLine2()) + "|");

										ckycDtl60Builder.append(stringValueCheck(ckycdtl60.getLocalAddrLine3()) + "|"
												+ stringValueCheck(ckycdtl60.getLocalAddrCity()) + "|"
												+ stringValueCheck(ckycdtl60.getLocalAddrDistrict()) + "|"
												+ stringValueCheck(ckycdtl60.getLocalAddrProvince()) + "|");

										ckycDtl60Builder.append(stringValueCheck(ckycdtl60.getLocalAddrCountry()) + "|"
												+ stringValueCheck(ckycdtl60.getLocalPofAddrSubmit()) + "|"
												+ stringValueCheck(ckycdtl60.getLocalAddrZip()) + "|"
												+ stringValueCheck(ckycdtl60.getResPhoneCode()) + "|");

										ckycDtl60Builder.append(stringValueCheck(ckycdtl60.getResPhoneNo()) + "|"
												+ stringValueCheck(ckycdtl60.getOffPhoneCode()) + "|"
												+ stringValueCheck(ckycdtl60.getOffPhoneNo()) + "|"
												+ stringValueCheck(ckycdtl60.getMobPhoneCode()) + "|");

										ckycDtl60Builder.append(stringValueCheck(ckycdtl60.getMobPhoneNo()) + "|"
												+ stringValueCheck(ckycdtl60.getFaxPhoneCode()) + "|"
												+ stringValueCheck(ckycdtl60.getFaxPhoneNo()) + "|"
												+ stringValueCheck(ckycdtl60.getEmailId()) + "|");

										ckycDtl60Builder.append(dateEmptyorNot(ckycdtl60.getAddrDod()) + "|"
												+ stringValueCheck(ckycdtl60.getAddrPod()) + "|"
												+ stringValueCheck(ckycdtl60.getFiller1()) + "|"
												+ stringValueCheck(ckycdtl60.getFiller2()) + "|");

										ckycDtl60Builder.append(stringValueCheck(ckycdtl60.getFiller3()) + "|"
												+ stringValueCheck(ckycdtl60.getFiller4()));
										bw.write(ckycDtl60Builder.toString() + '\n');
									}
								}

								File insideFileFolder = null;
								File folder = null;

								if (ckyc70 != null && !ckyc70.isEmpty()) {
									for (CKYCDtl70 ckycdtl70 : ckyc70) {
										if (ckycdtl70.getCustDocImage() != null) {
											StringBuilder ckycDtl70Builder = new StringBuilder();
											ckycDtl70Builder.append(numValueCheck(ckycdtl70.getRecordType()) + "|");
											ckycDtl70Builder.append(numValueCheck(ckycdtl70.getLineNo()) + "|"
													+ stringValueCheck(ckycdtl20.getCustCif() + "_" + rowNo + "_"
															+ ckycdtl70.getImgFolderNm())
													+ "|" + stringValueCheck(ckycdtl70.getImgType()) + "|");
											ckycDtl70Builder.append(stringValueCheck(ckycdtl70.getGobalOrLocal()) + "|"
													+ stringValueCheck(ckycdtl70.getBranchCode()) + "|"
													+ stringValueCheck(ckycdtl70.getFiller1()) + "|");
											ckycDtl70Builder.append(stringValueCheck(ckycdtl70.getFiller2()) + "|"
													+ stringValueCheck(ckycdtl70.getFiller3()) + "|"
													+ stringValueCheck(ckycdtl70.getFiller4()));
											if (ckycdtl70.getCustDocImage() != null) {
												insideFileFolder = new File(mainfolder.getPath() + "/"
														+ ckycdtl20.getCustCif() + "_" + rowNo);
												insideFileFolder.mkdir();
												folder = new File(insideFileFolder.getPath() + "/"
														+ ckycdtl20.getCustCif() + "_" + rowNo);
												if (!folder.exists()) {
													folder.mkdir();
												}

												if (folder.exists()) {
													byte barr[] = ckycdtl70.getCustDocImage();
													FileOutputStream fout = new FileOutputStream(
															folder.getPath() + "/" + ckycdtl20.getCustCif() + "_"
																	+ rowNo + "_" + ckycdtl70.getImgFolderNm());
													fout.write(barr);
												}
											}
											bw.write(ckycDtl70Builder.toString() + '\n');
										}
									}

									if (insideFileFolder != null && insideFileFolder.exists()) {
										File directoryToZip = new File(insideFileFolder.getPath());
										try {
											List<File> fileList = new ArrayList<>();

											getAllFiles(directoryToZip, fileList);
											writeZipFile(directoryToZip, fileList);
										} catch (Exception e) {
											logger.warn(Literal.EXCEPTION, e);
										}
									}
								}

								rowNo++;
							}
						}

						bw.flush();
						fos.flush();
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
					}

					if (fileFolder != null && fileFolder.exists() && folderData) {
						File directoryToZip = new File(fileFolder.getPath());
						try {
							List<File> fileList = new ArrayList<>();

							getAllFiles(directoryToZip, fileList);

							List<File> mainList = new ArrayList<>();
							for (File files : fileList) {
								String fileName = files.toString();
								if (fileName.contains(".zip") || fileName.contains(".txt")) {
									mainList.add(files);
								}
							}
							writeZipFile(fileFolder, mainList);
							boolean sucess = deleteDirectory(fileFolder);
							flag = sucess;
						} catch (Exception e) {
							logger.warn(Literal.EXCEPTION, e);
						}
					}

				}
			}
		}
		return flag;
	}

	public int saveDtl20(CKYCDtl20 ckycDtl20) {
		logger.info("Entering");
		int count = 0;
		count = ckycDAO.saveDtl20(ckycDtl20);
		logger.info("Leaving");
		return count;
	}

	public void saveDtl30(List<CKYCDtl30> ckycDtl30) {
		logger.info("Entering");

		for (CKYCDtl30 ckycDtl : ckycDtl30) {
			ckycDAO.saveDtl30(ckycDtl);
		}

		logger.info("Leaving");
	}

	public void saveDtl60(List<CKYCDtl60> ckycDtl60) {
		logger.info("Entering");

		for (CKYCDtl60 ckycDtl : ckycDtl60) {
			ckycDAO.saveDtl60(ckycDtl);
		}

		logger.info("Leaving");
	}

	public void saveDtl70(List<CKYCDtl70> ckycDtl70) {
		logger.info("Entering");

		for (CKYCDtl70 ckycDtl : ckycDtl70) {
			ckycDAO.saveDtl70(ckycDtl);
		}

		logger.info("Leaving");
	}

	public CKYCDtl20 getDetails20(Customer customer) {
		CKYCDtl20 ckycDtl20 = new CKYCDtl20();
		emailRemove = 0;
		phoneRemove = 0;
		addrRemove = 0;

		// IN Query need to add CKYC Column
		// String ckycNo = ckycDAO.getCkycNo(customer.getCustID());
		String ckycNo = customer.getCkycOrRefNo();
		List<CustomerAddres> addres = ckycDAO.getCustomerAddresById(customer.getCustID(), ckycNo);
		List<CustomerPhoneNumber> phoneNumbers = ckycDAO.getCustomerPhoneNumberById(customer.getCustID(), ckycNo);
		List<CustomerEMail> customerEMail = ckycDAO.getCustomerEmailById(customer.getCustID(), ckycNo);
		ckycDtl20.setCustId(customer.getCustID());
		ckycDtl20.setCustCif(customer.getCustCIF());
		ckycDtl20.setRecordType(20);
		ckycDtl20.setBranchCode(App.getProperty("external.interface.cKYC.BranchCode"));
		if (ckycNo == null) {
			ckycDtl20.setApplicationType("01");
			ckycDtl20.setNameUpdFlag("");
			ckycDtl20.setPersonalUpdFlag("");
			ckycDtl20.setAddrUpdFlag("");
			ckycDtl20.setContactUpdFlag("");
			ckycDtl20.setRemarksUpdFlag("");
			ckycDtl20.setIdentityUpdFlag("");
			ckycDtl20.setRelatedUpdFlag("");
			ckycDtl20.setControlPersonFlag("");
			ckycDtl20.setImgUpdFlag("");
			ckycDtl20.setConstitutionType("01");
			ckycDtl20.setAccHolderFlag("02");
			ckycDtl20.setAccHolderType("02");
			ckycDtl20.setAccType("02");
			ckycDtl20.setCkycNo(ckycNo);
			ckycDtl20.setCustSalutationCode(dataSize(customer.getCustSalutationCode(), 5));
			ckycDtl20.setCustFName(dataSize(customer.getCustFName(), 50));
			ckycDtl20.setCustMName(dataSize(customer.getCustMName(), 50));
			ckycDtl20.setCustLName(dataSize(customer.getCustLName(), 50));
			ckycDtl20.setCustFullName(dataSize(customer.getCustShrtName(), 150));
			ckycDtl20.setFatherOrSpouse("01");
			ckycDtl20.setFatherSalutation("Mr");
			ckycDtl20.setFatherOrSpouseFirstName(dataSize(customer.getCustMotherMaiden(), 150));
			ckycDtl20.setCustfatherorSpouseFullName(dataSize(customer.getCustMotherMaiden(), 150));
			if (StringUtils.equalsIgnoreCase(PennantConstants.PFF_CUSTCTG_INDIV, "RETAIL"))
				ckycDtl20.setCustGenderCode(customer.getCustGenderCode());
			else
				ckycDtl20.setCustGenderCode("");
			String maritalStatus = ckycDAO.getCode("CKYCMARITALSTATUS", customer.getCustMaritalSts());
			ckycDtl20.setCustMaritalsts(maritalStatus);
			ckycDtl20.setCustNationality(dataSize(customer.getCustNationality(), 2));
			String profession = ckycDAO.getCode("CKYCProfession", customer.getLovDescCustProfessionName());
			ckycDtl20.setOccupationType(profession);
			ckycDtl20.setCustDob(customer.getCustDOB());
			if (!StringUtils.equalsIgnoreCase(ckycDtl20.getConstitutionType(), "01")) {
				ckycDtl20.setPlaceOfIncorporation("");
				ckycDtl20.setDtCommencementBussiness(null);
				ckycDtl20.setCountryOfIncorporation("");
				ckycDtl20.setCountryofResidenceasPerTaxLaw("");
				ckycDtl20.setOtherConstIdType("");
				ckycDtl20.setTin("");
				ckycDtl20.setTinIssuingCountry("");
				ckycDtl20.setCustcrcpr("");

			}
			ckycDtl20.setResStatus("04");
			ckycDtl20.setCustrestaxJurioutsideIndiaFlag("02");
			if (StringUtils.equalsIgnoreCase(ckycDtl20.getCustrestaxJurioutsideIndiaFlag(), "01")) {
				ckycDtl20.setJuriresidence("");
				ckycDtl20.setJuriTin("");
				ckycDtl20.setJuriCob("");
				ckycDtl20.setJuriPob("");
			}
			if (addres.size() == 1) {
				ckycDtl20.setPmtAddrsameLocalFlag(true);
			}
			ckycDtl20 = getAddressDetails(ckycDtl20, addres);
			ckycDtl20 = getPhoneNumber(ckycDtl20, phoneNumbers);
			ckycDtl20 = getEmailId(ckycDtl20, customerEMail);
			ckycDtl20.setRemarks("");
			ckycDtl20.setKycDod(null);
			ckycDtl20.setKycPod("");
			ckycDtl20.setKycVerificationDate(null);
			ckycDtl20.setKycDocType("01");
			ckycDtl20.setKycVName("");
			ckycDtl20.setKycVDesignation("");
			ckycDtl20.setKycVBranch("");
			ckycDtl20.setKycEMPCode("");
			ckycDtl20.setKycOrgCode("");
			ckycDtl20.setKycOrgName("");
			ckycDtl20.setFiller1("");
			ckycDtl20.setFiller2("");
			ckycDtl20.setFiller3("");
			ckycDtl20.setFiller4("");

		} else {
			ckycDtl20.setCkycNo(ckycNo);
			ckycDtl20.setApplicationType("03");
			CKYCLog nameFlag = ckycDAO.applicantNameFlag(customer.getCustID(), ckycNo);
			if (nameFlag != null) {
				ckycDtl20.setNameUpdFlag("01");
				ckycDtl20.setCustSalutationCode(nameFlag.getCustsalutationcode());
				ckycDtl20.setCustFName(nameFlag.getCustfname());
				ckycDtl20.setCustMName(nameFlag.getCustmname());
				ckycDtl20.setCustLName(nameFlag.getCustlname());
				ckycDtl20.setCustFullName(nameFlag.getCustFullName());
			} else
				ckycDtl20.setNameUpdFlag("02");

			CKYCLog perFlag = ckycDAO.personalDetailFlag(customer.getCustID(), ckycNo);
			if (perFlag != null) {
				ckycDtl20.setPersonalUpdFlag("01");
				ckycDtl20.setFatherOrSpouse("01");
				ckycDtl20.setFatherSalutation("Mr");
				ckycDtl20.setFatherOrSpouseFirstName(perFlag.getCustfatherName());
				ckycDtl20.setCustfatherorSpouseFullName(dataSize(customer.getCustMotherMaiden(), 150));
				if (StringUtils.equalsIgnoreCase(customer.getCustGenderCode(), "M")
						|| StringUtils.equalsIgnoreCase(perFlag.getCustgendercode(), "F"))
					ckycDtl20.setCustGenderCode(perFlag.getCustgendercode());
				else
					ckycDtl20.setCustGenderCode("T");
				ckycDtl20.setCustMaritalsts(perFlag.getCustmaritalsts());
				ckycDtl20.setOccupationType(perFlag.getOccupationtype());
				ckycDtl20.setCustNationality(perFlag.getCustnationality());
				ckycDtl20.setCustDob(perFlag.getCustdob());

			} else
				ckycDtl20.setPersonalUpdFlag("02");

			int addrFlag = ckycDAO.addressDetailFlag(customer.getCustID(), ckycNo);
			if (addrFlag == 01) {
				ckycDtl20.setAddrUpdFlag("01");
				ckycDtl20 = getAddressDetails(ckycDtl20, addres);
			} else {
				ckycDtl20.setAddrUpdFlag("02");
			}
			int contactFlag = ckycDAO.contactFlag(customer.getCustID(), ckycNo);
			if (contactFlag == 01) {
				ckycDtl20.setContactUpdFlag("01");
				ckycDtl20 = getPhoneNumber(ckycDtl20, phoneNumbers);
			}
			int emailFlag = ckycDAO.emailFlag(customer.getCustID(), ckycNo);
			if (emailFlag == 01) {
				ckycDtl20.setContactUpdFlag("01");
				ckycDtl20 = getEmailId(ckycDtl20, customerEMail);
			}
			if (contactFlag != 01 && emailFlag != 01) {
				ckycDtl20.setContactUpdFlag("02");
			}
			int imgFlag = ckycDAO.imgDtlFlag(customer.getCustID(), ckycNo);
			if (imgFlag == 1) {
				ckycDtl20.setImgUpdFlag("01");
				ckycDtl20.setIdentityUpdFlag("01");
			} else {
				ckycDtl20.setImgUpdFlag("02");
				ckycDtl20.setIdentityUpdFlag("02");

			}
			ckycDtl20.setRemarksUpdFlag("02");
			ckycDtl20.setKycUpdFlag("02");
			ckycDtl20.setRelatedUpdFlag("02");
			ckycDtl20.setControlPersonFlag("02");
		}

		return ckycDtl20;
	}

	public CKYCDtl20 getAddressDetails(CKYCDtl20 ckycDtl20, List<CustomerAddres> customerAddres) {

		CKYCDtl20 ckycAddr = ckycDtl20;
		boolean addrpmtFlag = false;
		boolean addrLocalFlag = false;
		boolean addrJuriFlag = false;
		int size = 0;
		List<CustomerDocument> pofAddr = ckycDAO.getPofAddr(ckycDtl20.getCustId());
		if (!customerAddres.isEmpty()) {
			for (CustomerAddres custAdd : customerAddres) {
				if (!addrpmtFlag) {
					if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.ResiOff"),
							custAdd.getCustAddrType())) {
						ckycDtl20.setPmtAddrType("01");
						addrpmtFlag = true;
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.Resi"),
							custAdd.getCustAddrType())) {
						ckycDtl20.setPmtAddrType("02");
						addrpmtFlag = true;
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.Bus"),
							custAdd.getCustAddrType())) {
						ckycDtl20.setPmtAddrType("03");
						addrpmtFlag = true;
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.RegstrdOff"),
							custAdd.getCustAddrType())) {
						ckycDtl20.setPmtAddrType("04");
						addrpmtFlag = true;
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.Unspecified"),
							custAdd.getCustAddrType())) {
						ckycDtl20.setPmtAddrType("05");
						addrpmtFlag = true;
					}
					if (addrpmtFlag) {
						ckycDtl20.setPmtAddrLine1(dataSize(custAdd.getCustAddrLine1(), 55));
						ckycDtl20.setPmtAddrLine2(dataSize(custAdd.getCustAddrLine2(), 55));
						ckycDtl20.setPmtAddrLine3(dataSize(custAdd.getCustAddrLine3(), 55));
						ckycDtl20.setPmtAddrCity(dataSize(custAdd.getCustAddrCity(), 50));
						ckycDtl20.setPmtAddrDistrict(dataSize(custAdd.getCustDistrict(), 50));
						ckycDtl20.setPmtAddrProvince(dataSize(custAdd.getCustAddrProvince(), 2));
						ckycDtl20.setPmtAddrCountry(dataSize(custAdd.getCustAddrCountry(), 2));
						ckycDtl20.setPmtAddrZip(dataSize(custAdd.getCustAddrZIP(), 10));
						if (pofAddr != null && !pofAddr.isEmpty()) {
							CustomerDocument addrProof = pofAddr.get(0);
							String addrCode = ckycDAO.getCode("CKYCProofOfAddress",
									addrProof.getLovDescCustDocCategory());
							ckycDtl20.setPmtPofAddrsubmit(addrCode);
							pofAddr.remove(0);
						} else
							ckycDtl20.setPmtPofAddrsubmit("99");

						if (ckycDtl20.getPmtPofAddrsubmit().equalsIgnoreCase("99")) {
							ckycDtl20.setPmtPofAddrsubmit1("");
						}
						size++;
						continue;
					}

				}

				// check this condition is incorrect

				else if (ckycDtl20.isPmtAddrsameLocalFlag()) {
					ckycDtl20.setLocalAddrType(ckycDtl20.getPmtAddrType());
					ckycDtl20.setLocalAddrLine1(ckycDtl20.getPmtAddrLine1());
					ckycDtl20.setLocalAddrLine2(ckycDtl20.getPmtAddrLine2());
					ckycDtl20.setLocalAddrLine3(ckycDtl20.getJuriAddrLine3());
					ckycDtl20.setLocalAddrCity(ckycDtl20.getPmtAddrCity());
					ckycDtl20.setLocalAddrProvince(ckycDtl20.getPmtAddrProvince());
					ckycDtl20.setLocalAddrCountry(ckycDtl20.getPmtAddrCountry());
					ckycDtl20.setLocalAddrZip(ckycDtl20.getPmtAddrZip());
					ckycDtl20.setLocalAddrDistrict(ckycDtl20.getPmtAddrDistrict());
					addrLocalFlag = true;
					ckycDtl20.setLocalPofAddrsubmit(ckycDtl20.getPmtPofAddrsubmit());
					size++;
				}

				else if (!addrLocalFlag) {

					if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.ResiOff"),
							custAdd.getCustAddrType())) {
						ckycDtl20.setLocalAddrType("01");
						addrLocalFlag = true;
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.curres.Resi"),
							custAdd.getCustAddrType())) {
						ckycDtl20.setLocalAddrType("02");
						addrLocalFlag = true;
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.Bus"),
							custAdd.getCustAddrType())) {
						ckycDtl20.setLocalAddrType("03");
						addrLocalFlag = true;
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.RegstrdOff"),
							custAdd.getCustAddrType())) {
						ckycDtl20.setLocalAddrType("04");
						addrLocalFlag = true;
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.Unspecified"),
							custAdd.getCustAddrType())) {
						ckycDtl20.setLocalAddrType("05");
						addrLocalFlag = true;
					}

					ckycDtl20.setLocalAddrLine1(dataSize(custAdd.getCustAddrLine1(), 55));
					ckycDtl20.setLocalAddrLine2(dataSize(custAdd.getCustAddrLine2(), 55));
					ckycDtl20.setLocalAddrLine3(dataSize(custAdd.getCustAddrLine3(), 55));
					ckycDtl20.setLocalAddrCity(dataSize(custAdd.getCustAddrCity(), 50));
					ckycDtl20.setLocalAddrProvince(dataSize(custAdd.getCustAddrProvince(), 2));
					ckycDtl20.setLocalAddrCountry(dataSize(custAdd.getCustAddrCountry(), 3));
					ckycDtl20.setLocalAddrZip(dataSize(custAdd.getCustAddrZIP(), 10));
					ckycDtl20.setLocalAddrDistrict(dataSize(custAdd.getCustDistrict(), 50));
					if (pofAddr != null && !pofAddr.isEmpty()) {
						CustomerDocument addrProof = pofAddr.get(0);
						String addrCode = ckycDAO.getCode("CKYCProofOfAddress", addrProof.getLovDescCustDocCategory());
						ckycDtl20.setPmtPofAddrsubmit(addrCode);
						pofAddr.remove(0);
					} else
						ckycDtl20.setPmtPofAddrsubmit("99");

					size++;
				}

				/*
				 * else if (!addrJuriFlag) {
				 * 
				 * ckycDtl20.setJuriaddrsamepmtorLocalFlag("");
				 * 
				 * if (StringUtils.equalsIgnoreCase(ckycDtl20. getJuriaddrsamepmtorLocalFlag(), "01") && addrpmtFlag) {
				 * ckycDtl20.setJuriAddrType(ckycDtl20.getPmtAddrType());
				 * ckycDtl20.setJuriAddrLine1(ckycDtl20.getPmtAddrLine1());
				 * ckycDtl20.setJuriAddrLine2(ckycDtl20.getPmtAddrLine2());
				 * ckycDtl20.setJuriAddrLine3(ckycDtl20.getPmtAddrLine3());
				 * ckycDtl20.setJuriAddrCity(ckycDtl20.getPmtAddrCity());
				 * ckycDtl20.setJuriAddrProvince(ckycDtl20.getPmtAddrProvince()) ;
				 * ckycDtl20.setJuriAddrCountry(ckycDtl20.getPmtAddrCountry());
				 * ckycDtl20.setJuriAddrZip(ckycDtl20.getJuriAddrZip());
				 * ckycDtl20.setJuriPofAddrsubmit(ckycDtl20.getPmtPofAddrsubmit( ));
				 * 
				 * } else if (StringUtils.equalsIgnoreCase(ckycDtl20. getJuriaddrsamepmtorLocalFlag(), "02") &&
				 * addrLocalFlag) { { ckycDtl20.setJuriAddrType(ckycDtl20.getLocalAddrType());
				 * ckycDtl20.setJuriAddrLine1(ckycDtl20.getLocalAddrLine1());
				 * ckycDtl20.setJuriAddrLine2(ckycDtl20.getLocalAddrLine2());
				 * ckycDtl20.setJuriAddrLine3(ckycDtl20.getLocalAddrLine3());
				 * ckycDtl20.setJuriAddrCity(ckycDtl20.getLocalAddrCity());
				 * ckycDtl20.setJuriAddrProvince(ckycDtl20.getLocalAddrProvince( ));
				 * ckycDtl20.setJuriAddrCountry(ckycDtl20.getLocalAddrCountry()) ;
				 * ckycDtl20.setJuriAddrZip(ckycDtl20.getJuriAddrZip()); ckycDtl20.setJuriPofAddrsubmit(ckycDtl20.
				 * getLocalPofAddrsubmit());
				 * 
				 * } }
				 * 
				 * else { if (StringUtils.equalsIgnoreCase(App.getProperty( "external.interface.cKYC.ResiOff"),
				 * custAdd.getCustAddrType())) { ckycDtl20.setJuriAddrType("01"); addrJuriFlag = true; } else if
				 * (StringUtils.equalsIgnoreCase(App.getProperty( "external.interface.cKYC.Resi"),
				 * custAdd.getCustAddrType())) { ckycDtl20.setJuriAddrType("02"); addrJuriFlag = true; } else if
				 * (StringUtils.equalsIgnoreCase(App.getProperty( "external.interface.cKYC.Bus"),
				 * custAdd.getCustAddrType())) { ckycDtl20.setJuriAddrType("03"); addrJuriFlag = true; } else if
				 * (StringUtils.equalsIgnoreCase(App.getProperty( "external.interface.cKYC.RegstrdOff"),
				 * custAdd.getCustAddrType())) { ckycDtl20.setJuriAddrType("04"); addrJuriFlag = true; } else if
				 * (StringUtils.equalsIgnoreCase(App.getProperty( "external.interface.cKYC.Unspecified"),
				 * custAdd.getCustAddrType())) { ckycDtl20.setJuriAddrType("05"); addrJuriFlag = true; }
				 * ckycDtl20.setJuriAddrLine1(custAdd.getCustAddrLine1());
				 * ckycDtl20.setJuriAddrLine2(custAdd.getCustAddrLine2());
				 * ckycDtl20.setJuriAddrLine3(custAdd.getCustAddrLine3());
				 * ckycDtl20.setJuriAddrCity(custAdd.getCustAddrCity());
				 * ckycDtl20.setJuriAddrProvince(custAdd.getCustAddrProvince());
				 * ckycDtl20.setJuriAddrCountry(custAdd.getCustAddrCountry());
				 * ckycDtl20.setJuriAddrZip(custAdd.getCustAddrZIP()); ckycDtl20.setJuriPofAddrsubmit(""); size++; } }
				 */else {
					size++;
				}

				if (addrLocalFlag && addrpmtFlag) {
					addrRemove = size;
					break;
				}
			}

		}
		return ckycAddr;
	}

	public CKYCDtl20 getPhoneNumber(CKYCDtl20 ckycDtl20, List<CustomerPhoneNumber> phoneNumber) {
		CKYCDtl20 ckycDtl = ckycDtl20;
		int totalPh = 0;
		boolean resTypePhone = false;
		boolean offTypePhone = false;
		boolean mobTypePhone = false;
		boolean faxTypePhone = false;
		if (!phoneNumber.isEmpty()) {
			for (CustomerPhoneNumber customerPhoneNumber : phoneNumber) {
				if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.PhType.Resi"),
						customerPhoneNumber.getPhoneTypeCode()) && resTypePhone == false) {
					try {

						ckycDtl20.setResPhoneCode(dataSize(customerPhoneNumber.getPhoneAreaCode(), 4));
						ckycDtl20.setResPhoneNo(dataSize(customerPhoneNumber.getPhoneNumber(), 10));
						resTypePhone = true;
						totalPh++;
					} catch (Exception e) {
						logger.info("Residence Phone Number Not Valid");
					}
				} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.PhType.Off"),
						customerPhoneNumber.getPhoneTypeCode()) && offTypePhone == false) {

					try {
						ckycDtl20.setOffPhoneCode(dataSize(customerPhoneNumber.getPhoneAreaCode(), 4));
						ckycDtl20.setOffPhoneNo(dataSize(customerPhoneNumber.getPhoneNumber(), 10));
						offTypePhone = true;
						totalPh++;
					} catch (Exception e) {
						logger.info("Office Phone Number Not Valid");
					}
				} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.PhType.Mobile"),
						customerPhoneNumber.getPhoneTypeCode()) && mobTypePhone == false) {
					try {
						ckycDtl20.setMobPhoneCode(dataSize(customerPhoneNumber.getPhoneAreaCode(), 3));
						ckycDtl20.setMobPhoneNo(dataSize(customerPhoneNumber.getPhoneNumber(), 10));
						mobTypePhone = true;
						totalPh++;
					} catch (Exception e) {
						logger.info("Mobile  Number Not Valid");
					}
				} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.PhType.Fax"),
						customerPhoneNumber.getPhoneTypeCode()) && faxTypePhone == false) {
					try {
						ckycDtl20.setFaxPhoneCode(dataSize(customerPhoneNumber.getPhoneAreaCode(), 4));
						ckycDtl20.setFaxPhoneNo(dataSize(customerPhoneNumber.getPhoneNumber(), 10));
						faxTypePhone = true;
						totalPh++;
					} catch (Exception e) {
						logger.info("Fax Number Not Valid");
					}

				} else {
					totalPh++;
				}

			}

		}
		phoneRemove = totalPh;
		return ckycDtl;
	}

	public CKYCDtl20 getEmailId(CKYCDtl20 ckycDtl20, List<CustomerEMail> email) {
		CKYCDtl20 ckycDtl = ckycDtl20;

		if (!email.isEmpty()) {
			for (CustomerEMail custEmail : email) {
				ckycDtl20.setEmailId(dataSize(custEmail.getCustEMail(), 150));
				emailRemove++;
				break;
			}
		}
		return ckycDtl;
	}

	public List<CKYCDtl30> getDetails30(long custId, String ckycNo) {
		List<CKYCDtl30> ckycDtl30 = new ArrayList<>();
		List<CustomerDocument> custDocs = ckycDAO.getcustDocsByCustId(custId, ckycNo);

		int lineNo = 1;
		if (!custDocs.isEmpty()) {
			for (CustomerDocument document : custDocs) {
				CKYCDtl30 ckycDtl = new CKYCDtl30();
				ckycDtl.setCustId(document.getCustID());
				ckycDtl.setRecordType(30);
				ckycDtl.setLineNo(lineNo);
				String identityType = ckycDAO.getCode("CKYCIdentityType", document.getLovDescCustDocCategory());
				ckycDtl.setIdType(identityType);
				ckycDtl.setIdNo(document.getCustDocTitle());
				ckycDtl.setExpDate(document.getCustDocExpDate());
				if (ckycDtl.getIdType() != null && document.getCustDocImage() != null) {
					ckycDtl.setIdproofsubmit("01");
				} else {
					ckycDtl.setIdproofsubmit("02");
				}
				ckycDtl.setIdVStatus("02");
				ckycDtl30.add(ckycDtl);
				lineNo++;
			}
		}
		return ckycDtl30;
	}

	public List<CKYCDtl60> getDetails60(long custId, String ckyNo) {
		List<CKYCDtl60> ckycDtl60 = new ArrayList<>();
		int findEmailSize = emailRemove;
		int findPhoneSize = phoneRemove;
		int findAddrSize = addrRemove;
		List<CustomerDocument> pofAddr = ckycDAO.getPofAddr(custId);
		List<CustomerEMail> customerEMails = null;
		List<CustomerPhoneNumber> customerPhoneNumbers = null;
		List<CustomerAddres> customerAddres = null;
		if (findEmailSize > 0) {
			customerEMails = ckycDAO.getCustomerEmailById(custId, ckyNo);

		}
		if (findPhoneSize > 0) {
			customerPhoneNumbers = ckycDAO.getCustomerPhoneNumberById(custId, ckyNo);
		}
		if (findAddrSize > 0) {
			customerAddres = ckycDAO.getCustomerAddresById(custId, ckyNo);
		}
		int recordType = 60;
		int lineNo = 1;
		while (customerEMails != null && findEmailSize > 0) {
			customerEMails.remove(0);
			findEmailSize--;
		}
		while (customerPhoneNumbers != null && findPhoneSize > 0) {
			customerPhoneNumbers.remove(0);
			findPhoneSize--;
		}
		while (customerAddres != null && findAddrSize > 0) {
			customerAddres.remove(0);
			if (pofAddr != null && !pofAddr.isEmpty()) {
				pofAddr.remove(0);
			}
			findAddrSize--;
		}
		int emailSize = 0;
		int phoneSize = 0;
		int addrSize = 0;
		if (customerEMails != null) {
			emailSize = customerEMails.size();
		}
		if (customerPhoneNumbers != null) {

			phoneSize = customerPhoneNumbers.size();
		}
		if (customerAddres != null) {
			addrSize = customerAddres.size();
		}
		int maxSize = Math.max(addrSize, Math.max(phoneSize, emailSize));

		while (maxSize > 0) {
			CKYCDtl60 ckyc60 = new CKYCDtl60();
			ckyc60.setCustId(custId);
			ckyc60.setRecordType(recordType);
			ckyc60.setLineNo(lineNo);
			int totalPhType = 0;
			try {
				String totalPhoneType = App.getProperty("external.interface.cKYC.PhTypes.count");
				totalPhType = Integer.parseInt(totalPhoneType);
			} catch (Exception e) {
				logger.warn(e);
			}
			if (customerAddres != null && !customerAddres.isEmpty()) {
				for (CustomerAddres custAdd : customerAddres) {
					if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.ResiOff"),
							custAdd.getCustAddrType())) {
						ckyc60.setAddrType("01");
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.Resi"),
							custAdd.getCustAddrType())) {
						ckyc60.setAddrType("02");
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.Bus"),
							custAdd.getCustAddrType())) {
						ckyc60.setAddrType("03");
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.RegstrdOff"),
							custAdd.getCustAddrType())) {
						ckyc60.setAddrType("04");
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.Unspecified"),
							custAdd.getCustAddrType())) {
						ckyc60.setAddrType("05");
					}
					ckyc60.setLocalAddrLine1(custAdd.getCustAddrLine1());
					ckyc60.setLocalAddrLine2(custAdd.getCustAddrLine2());
					ckyc60.setLocalAddrLine3(custAdd.getCustAddrLine3());
					ckyc60.setLocalAddrCity(custAdd.getCustAddrCity());
					ckyc60.setLocalAddrProvince(custAdd.getCustAddrProvince());
					ckyc60.setLocalAddrCountry(custAdd.getCustAddrCountry());
					ckyc60.setLocalAddrZip(custAdd.getCustAddrZIP());
					ckyc60.setLocalAddrDistrict(custAdd.getCustDistrict());
					if (pofAddr != null && !pofAddr.isEmpty()) {
						CustomerDocument addrProof = pofAddr.get(0);
						String addrCode = ckycDAO.getCode("CKYCProofOfAddress", addrProof.getLovDescCustDocCategory());
						ckyc60.setLocalPofAddrSubmit(addrCode);
						ckyc60.setAddrDod(addrProof.getCustDocIssuedOn());
						ckyc60.setAddrPod(addrProof.getCustDocIssuedCountry());
						pofAddr.remove(0);
					} else {
						ckyc60.setLocalPofAddrSubmit("99");
						ckyc60.setAddrDod(null);
						ckyc60.setAddrPod("");
					}
					customerAddres.remove(0);
					break;
				}
			} else {
				ckyc60.setAddrType("");
				ckyc60.setLocalAddrLine1("");
				ckyc60.setLocalAddrLine2("");
				ckyc60.setLocalAddrLine3("");
				ckyc60.setLocalAddrCity("");
				ckyc60.setLocalAddrProvince("");
				ckyc60.setLocalAddrCountry("");
				ckyc60.setLocalAddrZip("");
				ckyc60.setLocalAddrDistrict("");
			}
			if (customerPhoneNumbers != null && !customerPhoneNumbers.isEmpty()) {
				int i = 0;
				for (CustomerPhoneNumber phoneNumber : customerPhoneNumbers) {

					if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.PhType.Resi"),
							phoneNumber.getPhoneTypeCode())) {
						try {

							ckyc60.setResPhoneCode(phoneNumber.getPhoneAreaCode());
							ckyc60.setResPhoneNo((phoneNumber.getPhoneNumber()));
							i++;
						} catch (Exception e) {
							logger.info("Residence Phone Number Not Valid");
						}
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.PhType.Off"),
							phoneNumber.getPhoneTypeCode())) {

						try {
							ckyc60.setOffPhoneCode(phoneNumber.getPhoneAreaCode());
							ckyc60.setOffPhoneNo(phoneNumber.getPhoneNumber());
							i++;
						} catch (Exception e) {
							logger.info("Office Phone Number Not Valid");
						}
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.PhType.Mobile"),
							phoneNumber.getPhoneTypeCode())) {
						try {

							ckyc60.setMobPhoneCode(phoneNumber.getPhoneCountryCode());
							ckyc60.setMobPhoneNo(phoneNumber.getPhoneNumber());
							i++;
						} catch (Exception e) {
							logger.info("Mobile  Number Not Valid");
						}
					} else if (StringUtils.equalsIgnoreCase(App.getProperty("external.interface.cKYC.PhType.Fax"),
							phoneNumber.getPhoneTypeCode())) {
						try {
							ckyc60.setFaxPhoneCode(phoneNumber.getPhoneAreaCode());
							ckyc60.setFaxPhoneNo(phoneNumber.getPhoneNumber());
							i++;
						} catch (Exception e) {
							logger.info("Fax Phone Number Not Valid");
						}
					}
					if (i == totalPhType) {
						while (i > 0) {
							customerPhoneNumbers.remove(0);
							i--;
						}
						break;
					}
				}

			} else {
				ckyc60.setResPhoneCode("");
				ckyc60.setResPhoneNo("");
				ckyc60.setOffPhoneCode("");
				ckyc60.setOffPhoneNo("");
				ckyc60.setMobPhoneCode("");
				ckyc60.setMobPhoneNo("");
				ckyc60.setFaxPhoneCode("");
				ckyc60.setFaxPhoneNo("");

			}
			if (customerEMails != null && !customerEMails.isEmpty()) {
				for (CustomerEMail custEmail : customerEMails) {
					ckyc60.setEmailId(custEmail.getCustEMail());
					customerEMails.remove(0);
					break;
				}
			} else {
				ckyc60.setEmailId("");

			}

			ckycDtl60.add(ckyc60);
			lineNo++;
			maxSize--;
		}

		return ckycDtl60;
	}

	public List<CKYCDtl70> getDetails70(Customer customer, String ckycNo) {
		List<CKYCDtl70> ckycDtl70 = new ArrayList<>();
		if (App.getProperty("external.interface.ckyc.external.dms").equalsIgnoreCase("TRUE")) {
			ckycDtl70 = getDocDetailsFromDMS(customer, ckycNo);
		} else {
			ckycDtl70 = getDocDetailsFromPLF(customer, ckycNo);
		}

		return ckycDtl70;
	}

	private List<CKYCDtl70> getDocDetailsFromPLF(Customer customer, String ckycNo) {
		List<CKYCDtl70> ckycDtl70 = new ArrayList<>();
		List<CustomerDocument> customerDocuments = ckycDAO.getcustDocsByCustId(customer.getCustID(), ckycNo);
		int lineNo = 1;
		if (customerDocuments != null)
			for (CustomerDocument document : customerDocuments) {
				CKYCDtl70 ckycDtl = new CKYCDtl70();
				ckycDtl.setCustId(customer.getCustID());
				ckycDtl.setRecordType(70);
				ckycDtl.setLineNo(lineNo);
				ckycDtl.setImgFolderNm(document.getCustDocName());
				String identityType = ckycDAO.getCode("CKYCDocumentMaster", document.getLovDescCustDocCategory());
				ckycDtl.setImgType(identityType);
				ckycDtl.setGobalOrLocal("01");
				ckycDtl.setCustDocImage(document.getCustDocImage());
				ckycDtl.setBranchCode(customer.getLovDescCustDftBranchName());
				ckycDtl70.add(ckycDtl);
				lineNo++;
			}
		return ckycDtl70;
	}

	// For Piramal - To get customer KYC documents from DMS based on leadID.
	private List<CKYCDtl70> getDocDetailsFromDMS(Customer customer, String ckycNo) {
		List<CKYCDtl70> ckycDtl70 = new ArrayList<>();
		Map<String, Object> docTypeMasterMap = new HashMap<>();
		ExternalDocument extDocument = new ExternalDocument();
		DocumentDetails documentDetails = new DocumentDetails();
		String leadId = ckycDAO.getLeadIdByCustId(customer.getCustID());
		docTypeMasterMap = ckycDAO.getcKYCdocMaster();

		Timestamp ts = ckycDAO.getLastMntOn(String.valueOf(customer.getCustID()), ckycNo);
		if (ObjectUtils.isNotEmpty(ts)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:MM");
			String updatedAfter = sdf.format(ts);
			extDocument.setRevisedDate(updatedAfter);
		}

		extDocument.setLeadId(leadId);
		List<ExternalDocument> listOfExternalDocs = dmsManagementService.getExternalDocument(extDocument);

		int lineNo = 1;
		for (ExternalDocument externalDocument : listOfExternalDocs) {
			if (!externalDocument.getCategoryOfDocument().equalsIgnoreCase("CUSTOMER")) {
				continue;
			}
			if (StringUtils.equalsIgnoreCase(externalDocument.getCustCIF(), customer.getCustCIF())
					&& docTypeMasterMap.containsKey(externalDocument.getDocumentType())) {

				documentDetails = dmsManagementService.getExternalDocument(externalDocument.getImageIndex(), "CKYC");
				CKYCDtl70 ckycDtl = new CKYCDtl70();
				ckycDtl.setCustId(customer.getCustID());
				ckycDtl.setRecordType(70);
				ckycDtl.setLineNo(lineNo);
				ckycDtl.setImgFolderNm(externalDocument.getDocName());
				String identityType = ckycDAO.getCode("CKYCDocumentMaster", externalDocument.getDocumentType());
				ckycDtl.setImgType(identityType);
				ckycDtl.setGobalOrLocal("01");
				ckycDtl.setCustDocImage(documentDetails.getDocImage());
				ckycDtl.setBranchCode(customer.getLovDescCustDftBranchName());
				ckycDtl70.add(ckycDtl);
				lineNo++;
			}
		}
		return ckycDtl70;
	}

	public boolean cleanData() {
		logger.debug("Entering");

		boolean flag = ckycDAO.cleanData();
		logger.debug("Leaving");

		return flag;
	}

	@Override
	public void updateCkycNo(String ckycNo, String batchNo, String rowNo) {
		ckycDAO.updateCkycNo(ckycNo, batchNo, rowNo);
	}

	public int getCustId(String ckycNo) {
		return ckycDAO.getCustId(ckycNo);
	}

	@Override
	public void updateCustomerWithCKycNo(int custId, String ckycNo) {
		ckycDAO.updateCustomerWithCKycNo(custId, ckycNo);
	}

	private char flagValue(boolean val) {
		if (val) {
			return 'Y';
		} else {
			return 'N';
		}

	}

	private String numValueCheck(long val) {
		if (val > 0) {
			return Long.toString(val);
		} else {
			return "";
		}
	}

	private String stringValueCheck(String val) {
		if (StringUtils.isNotEmpty(val)) {
			return val;
		} else {
			return "";
		}

	}

	String updateBatchNo(long num) {
		int digits = 0;
		String noDigits = App.getProperty("external.interface.cKYC.BatchDigitNo");
		try {
			digits = Integer.parseInt(noDigits);
		} catch (Exception e) {
			logger.warn("No of Digits Incorrect");

		}
		if (digits != 0) {
			StringBuilder upBatchNo = new StringBuilder(digits);
			int zeroes = digits - (int) (Math.log(num) / Math.log(10)) - 1;
			for (int i = 0; i < zeroes; i++) {
				upBatchNo.append(0);
			}
			return upBatchNo.append(num).toString();
		}
		return null;
	}

	String updateDigit(long num) {
		int digits = 2;
		if (digits != 0) {
			StringBuilder upBatchNo = new StringBuilder(digits);
			int zeroes = digits - (int) (Math.log(num) / Math.log(10)) - 1;
			for (int i = 0; i < zeroes; i++) {
				upBatchNo.append(0);
			}
			return upBatchNo.append(num).toString();
		}
		return null;
	}

	private String dateEmptyorNot(Date dt) {
		if (dt != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			return dateFormat.format(dt);
		} else {
			return "";
		}
	}

	@Override
	public List<Long> getId() {
		List<Customer> cust = ckycDAO.getId();
		List<Long> custId = new ArrayList<>();
		for (Customer customer : cust) {
			if (customer.getCustID() != 0)
				custId.add(customer.getCustID());

		}
		return custId;

	}

	public String dataSize(String value, int size) {
		if (value != null) {
			if (value.length() <= size) {
				return value;
			} else
				return value.substring(0, size);
		}
		return "";
	}

	public void getAllFiles(File dir, List<File> fileList) {
		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			if (file.isDirectory()) {
				getAllFiles(file, fileList);
			}
		}
	}

	public void writeZipFile(File directoryToZip, List<File> fileList) {

		try {
			FileOutputStream fos = new FileOutputStream(directoryToZip.getPath() + ".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}

			zos.close();
			fos.close();
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws IOException {

		FileInputStream fis = new FileInputStream(file);

		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());

		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

	public boolean deleteDirectory(File path) throws InterruptedException {
		boolean success = FileSystemUtils.deleteRecursively(path);
		if (!success) {
			Thread.sleep(1000);
			deleteDirectory(path);
		}

		return success;

	}

}