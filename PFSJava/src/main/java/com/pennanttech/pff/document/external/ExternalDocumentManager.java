package com.pennanttech.pff.document.external;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.AMedia;
import org.zkoss.zkplus.spring.SpringUtil;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pff.external.DocumentManagementService;

public class ExternalDocumentManager {
	private static final Logger logger = LogManager.getLogger(ExternalDocumentManager.class);

	@Autowired(required = false)
	private DocumentManagementService documentManagementService;

	private DMSService dMSService;

	public AMedia setDocContent(DocumentDetails documentDetails) {
		AMedia amedia = null;
		if (documentDetails == null) {
			return amedia;
		}

		if (documentDetails.getDocImage() != null) {
			amedia = new AMedia(documentDetails.getDocName(), null, null, documentDetails.getDocImage());
		}

		if (documentDetails.getDocImage() == null && documentDetails.getDocRefId() != null) {
			documentDetails.setDocImage(getDocumentImage(documentDetails.getDocRefId()));
			amedia = new AMedia(documentDetails.getDocName(), null, null, documentDetails.getDocImage());
		}

		if (documentDetails.getDocImage() == null && StringUtils.isNotBlank(documentDetails.getDocUri())) {
			// Fetch document from interface
			String custCif = documentDetails.getLovDescCustCIF();
			DocumentDetails extdetail = getExternalDocument(documentDetails.getDocName(), documentDetails.getDocUri(),
					custCif);
			if (extdetail != null && extdetail.getDocImage() != null) {
				amedia = new AMedia(extdetail.getDocName(), null, null, extdetail.getDocImage());
			}
		}
		return amedia;
	}

	public AMedia getDocumentMedia(String fileName, String docRefId, String reference) {
		DocumentDetails detail = getExternalDocument(fileName, docRefId, reference);
		if (detail != null && detail.getDocImage() != null) {
			AMedia amedia = new AMedia(detail.getDocName(), null, null, detail.getDocImage());
			return amedia;
		} else {
			return null;
		}

	}

	public DocumentDetails getExternalDocument(String fileName, String docRefId, String reference) {
		logger.info("Get the document from External DMS File Name {}, Doc RefId {}, Reference {}", fileName, docRefId,
				reference);

		DocumentDetails returndetails = null;
		if (documentManagementService == null) {
			return returndetails;
		}

		String[] docRefIds = null;
		List<DocumentDetails> documentDetailList = new ArrayList<>(1);
		if (StringUtils.contains(docRefId, ",")) {
			docRefIds = docRefId.split(",");
		} else {
			docRefIds = new String[1];
			docRefIds[0] = docRefId;
		}

		for (String docExternalRefId : docRefIds) {
			DocumentDetails details = documentManagementService.getExternalDocument(docExternalRefId, reference);
			documentDetailList.add(details);
		}

		if (documentDetailList.isEmpty()) {
			logger.info("Docuemnt not found in Exterenal DMS.");
			return returndetails;
		}

		DocumentDetails documentDet = documentDetailList.get(0);
		if (documentDetailList.size() == 1) {
			documentDet.setDocName(fileName);
			documentDet.setDocImage(PennantApplicationUtil.decode(documentDet.getDocImage()));
			returndetails = documentDet;
		} else {
			try {
				boolean isImage = false;
				boolean isPDF = false;

				for (DocumentDetails documentDetails : documentDetailList) {
					URL u = new URL(documentDetails.getDocUri());
					URLConnection uc = u.openConnection();

					String contentType = StringUtils.trimToEmpty(uc.getContentType());
					if (contentType.toUpperCase().contains("IMAGE")) {
						isImage = true;
						break;
					} else if (contentType.toUpperCase().contains("PDF")) {
						isPDF = true;
						break;
					}
				}

				if (isImage) {
					try (ByteArrayOutputStream outsream = mergeImages(documentDetailList)) {
						documentDet.setDocImage(outsream.toByteArray());
						documentDet.setDocName("document.png");
					}

				} else if (isPDF) {
					try (ByteArrayOutputStream outsream = mergePDF(documentDetailList)) {
						documentDet.setDocImage(outsream.toByteArray());
						documentDet.setDocName("document.pdf");
					}
				}
				returndetails = documentDet;

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

		return returndetails;
	}

	private ByteArrayOutputStream mergeImages(List<DocumentDetails> documentDetailList) throws IOException {
		BufferedImage[] images = new BufferedImage[documentDetailList.size()];
		for (int i = 0; i < documentDetailList.size(); i++) {
			DocumentDetails doc = documentDetailList.get(i);
			URL u = new URL(doc.getDocUri());
			BufferedImage img = ImageIO.read(u);
			images[i] = img;
		}

		int ht = getMaxheightValue(images);
		int wt = getMaxwidthValue(images);
		// Create the output image.
		// It is the same size as the first
		// input image.  I had to specify the type
		// so it would keep it's transparency.
		BufferedImage output = new BufferedImage(wt, ht * images.length, BufferedImage.TYPE_INT_ARGB);

		// Draw each of the input images onto the
		// output image.
		Graphics g = output.getGraphics();
		for (int i = 0; i < images.length; i++) {
			g.drawImage(images[i], 0, (i * ht), wt / 2, ht / 2, null);
		}
		ByteArrayOutputStream outsream = new ByteArrayOutputStream();
		ImageIO.write(output, "png", outsream);
		return outsream;
	}

	public ByteArrayOutputStream mergePDF(List<DocumentDetails> documentDetailList) throws IOException {
		PDFMergerUtility pdfMerge = new PDFMergerUtility();
		pdfMerge.setDestinationStream(new ByteArrayOutputStream());

		for (DocumentDetails details : documentDetailList) {
			String docUri = details.getDocUri();
			URL u = new URL(docUri);
			URLConnection uc = u.openConnection();

			//String password = StringUtils.trimToEmpty(details.getPassword());
			pdfMerge.addSource(uc.getInputStream());
		}

		pdfMerge.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

		return (ByteArrayOutputStream) pdfMerge.getDestinationStream();

	}

	public static int getMaxheightValue(BufferedImage[] bufImage) {
		int[] array = new int[bufImage.length];
		for (int j = 0; j < bufImage.length; j++) {
			if (bufImage[j] != null) {
				array[j] = bufImage[j].getHeight();
			}
		}
		int maxValue = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > maxValue) {
				maxValue = array[i];
			}
		}
		return maxValue;
	}

	public static int getMaxwidthValue(BufferedImage[] bufImage) {
		int[] array = new int[bufImage.length];
		for (int j = 0; j < bufImage.length; j++) {
			if (bufImage[j] != null) {
				array[j] = bufImage[j].getWidth();
			}
		}
		int maxValue = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > maxValue) {
				maxValue = array[i];
			}
		}
		return maxValue;
	}

	private static byte[] getDocumentImage(long docID) {
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<DocumentManager> searchObject = new JdbcSearchObject<DocumentManager>(DocumentManager.class);

		searchObject.addFilterEqual("Id", docID);
		searchObject.addTabelName("DocumentManager");
		searchObject.addField("DocImage");
		List<DocumentManager> documentManagers = pagedListService.getBySearchObject(searchObject);
		if (documentManagers != null && !documentManagers.isEmpty()) {
			return documentManagers.get(0).getDocImage();
		}
		return null;
	}

	public AMedia getAMedia(DocumentDetails documentDetails) {
		AMedia amedia = null;
		if (documentDetails == null) {
			return amedia;
		}

		if (documentDetails.getDocImage() != null) {
			amedia = new AMedia(documentDetails.getDocName(), null, null, documentDetails.getDocImage());
		}

		if (documentDetails.getDocImage() == null && documentDetails.getDocRefId() != null) {
			documentDetails.setDocImage(dMSService.getById(documentDetails.getDocRefId()));
			amedia = new AMedia(documentDetails.getDocName(), null, null, documentDetails.getDocImage());
		}

		if (documentDetails.getDocImage() == null && StringUtils.isNotBlank(documentDetails.getDocUri())) {
			// Fetch document from interface
			String custCif = documentDetails.getLovDescCustCIF();
			DocumentDetails extdetail = getExternalDocument(documentDetails.getDocName(), documentDetails.getDocUri(),
					custCif);
			if (extdetail != null && extdetail.getDocImage() != null) {
				amedia = new AMedia(extdetail.getDocName(), null, null, extdetail.getDocImage());
			}
		}
		return amedia;
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

}
