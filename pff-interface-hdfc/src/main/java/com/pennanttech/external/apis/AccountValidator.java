package com.pennanttech.external.apis;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.pennanttech.external.apis.model.AccountDetails;

public class AccountValidator {

	public AccountDetails getAccountDetails(String accountNumber) throws MalformedURLException, IOException {

		String req = generateRequest(accountNumber);

		URL url = new URL("");
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("POST");
		OutputStream os = httpCon.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
		osw.write(req.toString());
		osw.flush();
		osw.close();
		os.close();
		httpCon.connect();

		String result;
		BufferedInputStream bis = new BufferedInputStream(httpCon.getInputStream());
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result2 = bis.read();
		while (result2 != -1) {
			buf.write((byte) result2);
			result2 = bis.read();
		}
		result = buf.toString();

		AccountDetails accDetails = generateResponseBean(result);

		return accDetails;
	}

	private String generateRequest(String accNumber) {
		StringBuilder req = new StringBuilder();
		req.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		req.append("<faml>");
		req.append("<requestdata>");
		req.append("<custacctdetails>");
		req.append("<accountNo>").append(accNumber).append("</accountNo>");
		req.append("</custacctdetails>");
		req.append("</requestdata>");
		req.append("</faml>");
		return req.toString();
	}

	private AccountDetails generateResponseBean(String result) {

		return null;
	}
}
