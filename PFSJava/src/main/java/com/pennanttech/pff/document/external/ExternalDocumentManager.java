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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.AMedia;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.DocumentManagementService;

public class ExternalDocumentManager {
	private static final Logger			logger	= Logger.getLogger(ExternalDocumentManager.class);

	@Autowired(required = false)
	private DocumentManagementService	documentManagementService;

	public AMedia setDocContent(DocumentDetails documentDetails) {
		AMedia amedia = null;
		if (documentDetails == null) {
			return amedia;
		}
		if (documentDetails.getDocImage() == null && documentDetails.getDocRefId() != Long.MIN_VALUE) {
			documentDetails.setDocImage(PennantApplicationUtil.getDocumentImage(documentDetails.getDocRefId()));
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
		DocumentDetails detail = getExternalDocument(fileName,docRefId,reference);
		AMedia amedia=new AMedia(detail.getDocName(), null, null, detail.getDocImage());
		return amedia;
	}
	

	public DocumentDetails getExternalDocument(String fileName, String docRefId, String reference) {
		logger.debug(Literal.ENTERING);

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

		if (!documentDetailList.isEmpty()) {
			if (documentDetailList.size() == 1) {
				documentDetailList.get(0).setDocName(fileName);
				documentDetailList.get(0).setDocImage(PennantApplicationUtil.decode(documentDetailList.get(0).getDocImage()));
				return documentDetailList.get(0);
			} else {
				try {
					ByteArrayOutputStream outsream = null;
					int arrayIndex = 0;
					BufferedImage[] images = null;
					URL[] pdfurl = null;

					for (DocumentDetails documentDetails : documentDetailList) {
						URL u = new URL(documentDetails.getDocUri());
						URLConnection uc = u.openConnection();
						String contentType = StringUtils.trimToEmpty(uc.getContentType());
						if (contentType.toUpperCase().contains("IMAGE")) {
							if (images == null) {
								images = new BufferedImage[documentDetailList.size()];
							}

							BufferedImage img = ImageIO.read(u);
							images[arrayIndex] = img;
							arrayIndex++;
						} else {
							if (pdfurl == null) {
								pdfurl = new URL[documentDetailList.size()];
							}
							pdfurl[arrayIndex] = u;
							arrayIndex++;
						}
					}
					
					if (images != null) {
						outsream = mergeImages(images);
						documentDetailList.get(0).setDocImage(outsream.toByteArray());
						documentDetailList.get(0).setDocName("document.png");
					} else {
						outsream = mergePDF(pdfurl);
						documentDetailList.get(0).setDocImage(outsream.toByteArray());
						documentDetailList.get(0).setDocName("document.pdf");
					}

				} catch (Exception e) {
					logger.debug(e);
				}
				return documentDetailList.get(0);
			}
		}

		logger.debug(Literal.LEAVING);
		return documentDetailList.get(0);
	}

	private ByteArrayOutputStream mergeImages(BufferedImage[] images) throws IOException {
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

	public ByteArrayOutputStream mergePDF(URL[] pdfurl) throws IOException, DocumentException {

		ByteArrayOutputStream outsream = new ByteArrayOutputStream();
		Document document = new Document();
		PdfCopy pdfcopy = new PdfCopy(document, outsream);
		document.open();
		for (int i = 0; i < pdfurl.length; i++) {
			PdfReader pdfreader = null;
			try {
				pdfreader = new PdfReader(pdfurl[i]);
			} catch (BadPasswordException bpe) {
				pdfreader = new PdfReader(pdfurl[i], "".getBytes());//strPwd.getBytes()
			}

			int number_of_pages = pdfreader.getNumberOfPages();
			for (int page = 0; page < number_of_pages;) {
				pdfcopy.addPage(pdfcopy.getImportedPage(pdfreader, ++page));
			}
		}
		document.close();
		return outsream;

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

}
