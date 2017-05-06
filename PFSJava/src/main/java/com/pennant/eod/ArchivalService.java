package com.pennant.eod;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pennant.app.core.ServiceHelper;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PDFConversion;
import com.pennant.app.util.PathUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class ArchivalService extends ServiceHelper {
	private static final long serialVersionUID = -2106034326949484681L;
	private static Logger logger = Logger.getLogger(ArchivalService.class);

	private int	seqNo	= 0;
	
	public static final String approvedFinances = " SELECT FinReference, custid FROM FinanceMain WHERE FinApprovedDate = ?  ";
	public static final String customerDocuments = " SELECT CustDocCategory, CustDocType, CustDocName, CustDocImage FROM CustomerDocuments WHERE CustID = ? ";
	public static final String financeDocuments = " SELECT DD.Doctype, DD.DocCategory, DD.DocName, DM.DocImage FROM DocumentDetails DD INNER JOIN "
			+ " DocumentManager DM ON DD.DocRefId = DM.Id WHERE ReferenceId = ? ";


	
	/**
	 * Fetch the list approved finances based on value date and process the document archival
	 * 
	 * @param dateAppDate
	 * @throws SQLException
	 * @throws Exception
	 */
	public void processDocumentArchive(Date dateAppDate) throws SQLException, Exception {
		logger.debug("Entering");

		Connection connection = null;
		ResultSet financeResultSet = null;
		PreparedStatement financeStatement = null;

		connection = DataSourceUtils.getConnection(getDataSource());
		financeStatement = connection.prepareStatement(approvedFinances);
		financeStatement.setDate(1, DateUtility.getDBDate(dateAppDate.toString()));
		financeResultSet = financeStatement.executeQuery();

		while (financeResultSet.next()) {
			doDocumentArchive(financeResultSet);
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Fetch Finance and customer level documents to archive into the specified location
	 * 
	 * @param financeResultSet
	 * 				Which contains value date approved finances
	 * @throws Exception
	 */
	public void doDocumentArchive(ResultSet financeResultSet) throws Exception {
		logger.debug("Entering");

		PreparedStatement documentStatement = null;
		PreparedStatement custDocStatement = null;
		ResultSet documentsResultSet = null;
		ResultSet custDocResultSet = null;

		try {
			// get Connection
			Connection connection = DataSourceUtils.getConnection(getDataSource());

			String refNumber = financeResultSet.getString("FinReference");
			String custID = financeResultSet.getString("custid");

			// Fetch Finance level documents
			documentStatement = connection.prepareStatement(financeDocuments);
			documentStatement.setString(1, refNumber);
			documentsResultSet = documentStatement.executeQuery();
			
			while (documentsResultSet.next()) {
				String doctype = documentsResultSet.getString("Doctype");
				String docCategory = documentsResultSet.getString("DocCategory");
				String docName = documentsResultSet.getString("DocName");
				byte[] data = documentsResultSet.getBytes("DocImage");

				conversionToPDF(doctype, docCategory, docName, data, refNumber, custID);
			}

			// Fetch Customer level documents
			custDocStatement = connection.prepareStatement(customerDocuments);
			custDocStatement.setString(1, custID);
			custDocResultSet = custDocStatement.executeQuery();
			
			while (custDocResultSet.next()) {
				String doctype = custDocResultSet.getString("CustDocType");
				String docCategory = custDocResultSet.getString("CustDocCategory");
				String docName = custDocResultSet.getString("CustDocName");
				byte[] data = custDocResultSet.getBytes("CustDocImage");

				conversionToPDF(doctype, docCategory, docName, data, refNumber, custID);
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		} finally {
			if (documentsResultSet != null) {
				documentsResultSet.close();
			}
			if (custDocResultSet != null) {
				custDocResultSet.close();
			}
			if (documentStatement != null) {
				documentStatement.close();
			}
			if (custDocStatement != null) {
				custDocStatement.close();
			}
			
		}

		logger.debug("Leaving");
	}

	/**
	 * Convert the Image, Word and PDF kind of documents to PDF format
	 * 
	 * @param docType
	 * @param docCategory
	 * @param docName
	 * @param data
	 * @param refNumber
	 * @param custID
	 * @throws Exception
	 */
	private void conversionToPDF(String docType, String docCategory, String docName, byte[] data, String refNumber, String custID) throws Exception {

		try {
			String fileName = generateName(refNumber, docCategory);

			if (("IMG").equals(docType) && data != null) {
				PDFConversion.generatePDFFromImage(data, PathUtil.getPath(PathUtil.ECMS_ARCHIVEDOC_LOCATION) + fileName + ".PDF");
			} else if (("WORD").equals(docType) && data != null) {
				PDFConversion.generatePDFFromWord(data, PathUtil.getPath(PathUtil.ECMS_ARCHIVEDOC_LOCATION) + fileName + ".PDF");
			} else if (("PDF").equals(docType) && data != null) {
				PDFConversion.generatePdfFromPdf(data, PathUtil.getPath(PathUtil.ECMS_ARCHIVEDOC_LOCATION) + fileName + ".PDF");
			}
			prepareXMl(custID, refNumber, docCategory, docName, fileName);
		} catch (SQLException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	/**
	 * 
	 * @param cifID
	 * @param reference
	 * @param docuType
	 * @param docName
	 * @param fileName
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	private void prepareXMl(String cifID, String reference, String docuType, String docName, String fileName) throws ParserConfigurationException, TransformerException {

		final String xmlFilePath = PathUtil.getPath(PathUtil.ECMS_ARCHIVEDOC_LOCATION) + fileName + ".xml";
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("xml");
			doc.appendChild(rootElement);

			Element firstname = doc.createElement("class");
			firstname.appendChild(doc.createTextNode("CustomerFinanceFile"));
			rootElement.appendChild(firstname);

			Element cif = doc.createElement("CIFNumber");
			cif.appendChild(doc.createTextNode(cifID));
			rootElement.appendChild(cif);

			Element lDNumber = doc.createElement("LDNumber");
			lDNumber.appendChild(doc.createTextNode(reference));
			rootElement.appendChild(lDNumber);

			Element fileNumber = doc.createElement("FileNumber");
			rootElement.appendChild(fileNumber);

			Element docType = doc.createElement("DocumentType");
			docType.appendChild(doc.createTextNode(docuType));
			rootElement.appendChild(docType);

			Element bookingDate = doc.createElement("Financebookingdate");
			bookingDate.appendChild(doc.createTextNode(DateUtility.getAppValueDate(DateFormat.LONG_DATE)));
			rootElement.appendChild(bookingDate);

			Element clasification = doc.createElement("CustomerClassification");
			clasification.appendChild(doc.createTextNode("Normal"));
			rootElement.appendChild(clasification);

			Element notes = doc.createElement("Notes");
			notes.appendChild(doc.createTextNode(docName));
			rootElement.appendChild(notes);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(xmlFilePath).getAbsolutePath());

			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			logger.error(pce);
			throw pce;
		} catch (TransformerException tfe) {
			logger.error(tfe);
			throw tfe;
		}
	}

	private String generateName(String financeReferance, String documentCode) {
		StringBuilder fileName = new StringBuilder();
		setSeqNo(getSeqNo() + 1);
		fileName.append(financeReferance);
		fileName.append("_");
		fileName.append(documentCode);
		fileName.append("_");
		fileName.append(DateUtility.getValueDate("yyyymmdd"));
		fileName.append("_");
		fileName.append(getSeqNo());
		return fileName.toString();
	}


	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
}
