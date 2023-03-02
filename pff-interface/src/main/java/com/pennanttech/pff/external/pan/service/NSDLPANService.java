package com.pennanttech.pff.external.pan.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aspose.pdf.internal.imaging.internal.bouncycastle.jce.provider.BouncyCastleProvider;
import com.pennant.backend.model.PrimaryAccount;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;

@Component
@Qualifier("nsdlPANService")
public class NSDLPANService {
	private static final Logger logger = LogManager.getLogger(NSDLPANService.class);

	@Value("${nsdl.pan.enquiry.certificate.name:#{null}}")
	private String certName = null;

	@Value("${nsdl.pan.enquiry.userid:#{null}}")
	private String panUserId = null;

	@Value("${nsdl.pan.enquiry.password:#{null}}")
	private String password = null;

	@Value("${nsdl.pan.enquiry.proxy:#{null}}")
	private String panCreateProxy = null;

	@Value("${nsdl.pan.enquiry.url:#{null}}")
	private String panUrl = null;

	@Value("${nsdl.pan.enquiry.proxy.host:#{null}}")
	private String panHost = null;

	@Value("${nsdl.pan.enquiry.proxy.port:#{null}}")
	private String panPort = null;

	@Value("${pan.enquiry:false}")
	private boolean panValidationRequired = false;

	private String jksLocation = null;

	private boolean configFound = false;

	@PostConstruct
	public void loadConfiguration() {
		if (panValidationRequired && certName != null) {
			jksLocation = App.getResourcePath(App.CONFIG + File.separator + "certificates", "NSDL", certName);

			File file = new File(jksLocation);
			if (!file.exists()) {
				logger.warn(String.format("%s certificate not available in %s location", certName, file.getParent()));
			} else {
				configFound = true;
			}
		}
	}

	public PrimaryAccount getPANDetails(PrimaryAccount primaryAccount) {
		logger.debug(Literal.ENTERING);

		validatePAN(primaryAccount.getPanNumber());
		getPanResponseDetails(primaryAccount);

		logger.debug(Literal.LEAVING);

		return primaryAccount;

	}

	private void validatePAN(String panNumber) {
		logger.debug(Literal.ENTERING);
		StringBuilder builder = new StringBuilder();

		if (!configFound) {
			builder.append(String.format("%s certificate not available in %s location", certName, jksLocation));
		}

		if (StringUtils.isBlank(password)) {
			builder.append("NSDL Password cannot be blank.");
		}

		if (StringUtils.isBlank(panUserId)) {
			builder.append("NSDL UserId  cannot be blank.");
		}

		if (StringUtils.isBlank(panUrl)) {
			builder.append("NSDL PAN Verification URL cannot be blank");
		}

		if (StringUtils.isNotEmpty(builder.toString())) {
			throw new InterfaceException("8905", builder.toString());
		}

		logger.debug(Literal.LEAVING);

	}

	private void getPanResponseDetails(PrimaryAccount primaryAccount) {
		logger.debug(Literal.ENTERING);

		String signature = null;
		String data = panUserId.concat("^").concat(primaryAccount.getPanNumber());

		try {
			signature = getSignature(data, jksLocation, password);
			SSLContext sslcontext = SSLContext.getInstance("SSL");

			sslcontext.init(new KeyManager[0], new TrustManager[] { new DummyTrustManager() }, new SecureRandom());
			SSLSocketFactory factory = sslcontext.getSocketFactory();

			String urlParameters = "data=";
			urlParameters = urlParameters + URLEncoder.encode(data, "UTF-8") + "&signature="
					+ URLEncoder.encode(signature, "UTF-8");

			HttpsURLConnection connection;
			URL url = new URL(panUrl);
			if (StringUtils.equals(panCreateProxy, "Y")) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(panHost, Integer.parseInt(panPort)));
				connection = (HttpsURLConnection) url.openConnection(proxy);
			} else {
				connection = (HttpsURLConnection) url.openConnection();
			}

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setSSLSocketFactory(factory);
			connection.setHostnameVerifier(new DummyHostnameVerifier());

			String line = null;
			try (OutputStream os = connection.getOutputStream()) {
				try (OutputStreamWriter osw = new OutputStreamWriter(os)) {
					osw.write(urlParameters);
					osw.flush();
				}

				try (InputStream is = connection.getInputStream()) {
					try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
						line = in.readLine();
					}
				}
			}

			if (StringUtils.isNotBlank(line)) {

				List<String> fields = new ArrayList<>();

				do {
					if (line.length() > 0 && !line.contains("^")) {
						fields.add(line);
						line = new String();
					} else {
						fields.add(line.substring(0, line.indexOf("^")));
						line = line.substring(line.indexOf("^") + 1, line.length());
					}
				} while (line.length() > 0);

				if (CollectionUtils.isNotEmpty(fields)) {
					if (fields.size() > 1) {
						if (StringUtils.equals(fields.get(2), "E")) {
							primaryAccount.setCustFName(fields.get(4));
							primaryAccount.setCustLName(fields.get(3));
							primaryAccount.setCustMName(fields.get(5));
						} else {
							throw new InterfaceException("8901", "Invalid Pan");
						}

					} else if (fields.size() == 1) {
						throw new InterfaceException("8901", "Invalid Pan");
					}
				}
			}
		} catch (Exception e) {
			throw new InterfaceException("999", "NSDL", e);
		}

		logger.debug(Literal.LEAVING);
	}

	private String getSignature(String data, String jksFile, String password) {
		logger.debug(Literal.ENTERING);

		String alias = null;
		try {
			KeyStore keystore = KeyStore.getInstance("jks");

			try (InputStream input = new FileInputStream(jksFile)) {
				keystore.load(input, password.toCharArray());

				Enumeration<String> e = keystore.aliases();

				if (e != null) {
					while (e.hasMoreElements()) {
						String n = (String) e.nextElement();
						try {
							if (keystore.isKeyEntry(n)) {
								alias = n;
							}
						} catch (KeyStoreException e1) {
							throw new InterfaceException("8901", "Invalid Pan");
						}
					}
				}
			}

			PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, password.toCharArray());

			X509Certificate myPubCert = (X509Certificate) keystore.getCertificate(alias);

			// data to be signed
			byte[] dataToSign = data.getBytes();
			CMSSignedDataGenerator sgen = new CMSSignedDataGenerator();
			Security.addProvider(new BouncyCastleProvider());
			sgen.addSigner(privateKey, myPubCert, CMSSignedDataGenerator.DIGEST_SHA1);
			Certificate[] certChain = keystore.getCertificateChain(alias);

			ArrayList<Certificate> certList = new ArrayList<>();
			for (int i = 0; i < certChain.length; i++) {
				certList.add(certChain[i]);
			}

			sgen.addCertificatesAndCRLs(
					CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), "BC"));

			CMSSignedData csd = sgen.generate(new CMSProcessableByteArray(dataToSign), true, "BC");

			byte[] signedData = csd.getEncoded();
			byte[] signedData64 = Base64.getEncoder().encode(signedData);

			logger.debug(Literal.LEAVING);
			return new String(signedData64);

		} catch (Exception e1) {
			throw new InterfaceException("NSDL", "", e1);
		}

	}

	public static class DummyTrustManager implements X509TrustManager {
		public DummyTrustManager() {
			super();
		}

		public boolean isClientTrusted(X509Certificate cert[]) {
			return true;
		}

		public boolean isServerTrusted(X509Certificate cert[]) {
			return true;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

		}
	}

	public static class DummyHostnameVerifier implements HostnameVerifier {

		public DummyHostnameVerifier() {
			super();
		}

		public boolean verify(String urlHostname, String certHostname) {
			return true;
		}

		@Override
		public boolean verify(String arg0, SSLSession arg1) {

			return true;
		}
	}

}
