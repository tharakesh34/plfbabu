package com.pennanttech.pennapps.pff.test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pff.external.DocumentManagementService;

public class WriteImage {

	@Autowired(required = false)
	private DocumentManagementService documentManagementService;

	@Test(enabled = true)
	public void writeImageUsingURL() {
		BufferedImage image = null;
		try {
			URL url = new URL(
					"http://portaluat.niyogin.in//documents/20152/165138/upload_00001907.png/f016e98a-c368-24db-05d2-97ff22040d14?version=1.0&t=1520238875481");
			image = ImageIO.read(url);

			// ImageIO.write(image, "jpg",new File("C:\\out.jpg"));
			//ImageIO.write(image, "gif",new File("C:\\out.gif"));
			ImageIO.write(image, "png", new File("D:\\opt\\pennant\\PFF\\BASE\\mandates\\request\\out.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}

	@Test(enabled = false)
	public void writeImageUsingByteArray() {
		// Fetch document from DMS by using document reference
		DocumentDetails detail = documentManagementService.getExternalDocument("245928", "test");
		byte[] mandateForm = detail.getDocImage();

		try {
			ByteArrayInputStream input = new ByteArrayInputStream(decodeImage(mandateForm));
			ImageInputStream imageInput = ImageIO.createImageInputStream(input);
			
			BufferedImage image = ImageIO.read(imageInput);
			//File file = new File("D:\\opt\\pennant\\PFF\\BASE\\mandates\\request");
			ImageIO.write(image, "png", new File("D:\\opt\\pennant\\PFF\\BASE\\mandates\\request\\out.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private byte[] decodeImage(byte[] mandateForm) {
		byte[] deCodedData = null;
		if (mandateForm != null && mandateForm.length > 0) {
			Base64 base64 = new Base64();
			deCodedData = base64.decode(mandateForm);
		}
		return deCodedData;
	}
}
