package com.pennant.app.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.aspose.words.BreakType;
import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.FieldMergingArgs;
import com.aspose.words.IFieldMergingCallback;
import com.aspose.words.ImageFieldMergingArgs;
import com.aspose.words.License;
import com.aspose.words.PageSetup;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.RelativeVerticalPosition;
import com.aspose.words.WrapType;

public class PDFConversion implements IFieldMergingCallback{
	private static final Logger logger = Logger.getLogger(PDFConversion.class);
	
	public static void generatePDFFromWord(byte[] inputByteData,
			String destinationPDFDocPath) throws Exception {

		License wordlic = new License();

		//wordlic.setLicense("./Aspose.Words.lic");
		wordlic.setLicense("D:/Products/PFF/ahb/trunk/PFSJava/src/main/java/com/pennant/app/util/Aspose.Words.lic");

		Document doc = new Document(new ByteArrayInputStream(inputByteData));

		doc.getMailMerge().setFieldMergingCallback(new PDFConversion());

		doc.getMailMerge().execute(new String[] {}, new String[] {});

		doc.save(destinationPDFDocPath);
	}

	public static void generatePDFFromImage(byte[] inputByteData,
			String outputFileName) throws Exception {
	
		Document doc = new Document();
		DocumentBuilder builder = new DocumentBuilder(doc);
		
		ImageInputStream iis = ImageIO
				.createImageInputStream(new ByteArrayInputStream(inputByteData));
		ImageReader reader = ImageIO.getImageReaders(iis).next();
		reader.setInput(iis, false);

		try {
			// Get the number of frames in the image.
			int framesCount = reader.getNumImages(true);

			// Loop through all frames.
			for (int frameIdx = 0; frameIdx < framesCount; frameIdx++) {
				
				if (frameIdx != 0)
					builder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
				
				BufferedImage image = reader.read(frameIdx);
				
				PageSetup ps = builder.getPageSetup();

				ps.setPageWidth(ConvertUtil.pixelToPoint(image.getWidth()));
				ps.setPageHeight(ConvertUtil.pixelToPoint(image.getHeight()));
				
				builder.insertImage(image, RelativeHorizontalPosition.PAGE, 0,
						RelativeVerticalPosition.PAGE, 0, ps.getPageWidth(),
						ps.getPageHeight(), WrapType.NONE);
			}
		}

		finally {
			iis.close();
			reader.dispose();
		}

		doc.save(outputFileName);
	}
	
	
	public static void generatePdfFromPdf(byte[] inputByteData,
			String destinationPDFDocPath)  {

		try {
			//Document doc = new Document(new ByteArrayInputStream(inputByteData));
			
			OutputStream out = new FileOutputStream(destinationPDFDocPath);
			out.write(inputByteData);
			out.close();
			
			//doc.save(destinationPDFDocPath);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}
	
	@Override
	public void fieldMerging(FieldMergingArgs e) throws Exception {
		if (e.getFieldValue() == null) {
            DocumentBuilder builder = new DocumentBuilder(e.getDocument());
            builder.moveToMergeField(e.getDocumentFieldName());
            builder.write("");
		} else {
			if (StringUtils.isEmpty(e.getFieldValue().toString())) {
	            DocumentBuilder builder = new DocumentBuilder(e.getDocument());
	            builder.moveToMergeField(e.getDocumentFieldName());
	            builder.write("Default Value");
	        }
		}	
		
	}

	@Override
	public void imageFieldMerging(ImageFieldMergingArgs arg0) throws Exception {
		//
	}
}
