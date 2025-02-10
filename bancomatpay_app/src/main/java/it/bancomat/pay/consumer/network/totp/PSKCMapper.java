package it.bancomat.pay.consumer.network.totp;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import it.bancomat.pay.consumer.storage.model.Pskc;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class PSKCMapper {

	private static final String TAG = PSKCMapper.class.getSimpleName();

	private final static String SEARCH_EXPIRING_DATE = "pskc:ExpiryDate";
	private final static String SEARCH_TIME_INTERVAL = "pskc:TimeInterval";
	private final static String SEARCH_ITERATION_COUNTER = "IterationCount";
	private final static String SEARCH_KEY_LENGTH = "KeyLength";
	private final static String SEARCH_RESPONSE_FORMAT = "pskc:ResponseFormat";
	private final static String SEARCH_LENGTH = "Length";
	private final static String SEARCH_SPECIFIED = "Specified";
	private final static String SEARCH_CIPHER_VALUE = "xenc:CipherValue";
	private final static String SEARCH_MAK_KEY = "pskc:MACKey";
	private final static String SEARCH_ENCRYPTED_VALUE = "pskc:EncryptedValue";
	private final static String SEARCH_ENCRYPTION_METHOD = "xenc:EncryptionMethod";
	private final static String SEARCH_ENCRYPTION_METHOD_VERSION = "Version";

	private static String getElementValue(Document doc, String elementName, int index) {
		NodeList nodesKeyPackage = doc.getElementsByTagName(elementName);
		return nodesKeyPackage.item(index).getFirstChild().getNodeValue();
	}

	private static String getElementValueInAttribute(Document doc, String elementName, String attributeName) {
		NodeList nodesKeyPackage = doc.getElementsByTagName(elementName);
		return nodesKeyPackage.item(0).getAttributes().getNamedItem(attributeName).getNodeValue();
	}

	private static int getIntervalInSeconds(Document doc) {
		NodeList nodesKeyPackage = doc.getElementsByTagName(SEARCH_TIME_INTERVAL);
		return Integer.parseInt(nodesKeyPackage.item(0).getFirstChild().getChildNodes().item(0).getNodeValue());

	}

	public static Pskc buildPskc(String rawPskc) {
		Pskc pskc = new Pskc();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(rawPskc));
			Document doc = db.parse(is);
			pskc.setVersion(getElementValueInAttribute(doc, SEARCH_ENCRYPTION_METHOD, SEARCH_ENCRYPTION_METHOD_VERSION));
			pskc.setExpiredTimestamp(getElementValue(doc, SEARCH_EXPIRING_DATE, 0));
			pskc.setSalt(getElementValue(doc, SEARCH_SPECIFIED, 0));
			pskc.setRequiredDigit(Integer.parseInt(getElementValueInAttribute(doc, SEARCH_RESPONSE_FORMAT, SEARCH_LENGTH)));
			pskc.setKeyLength(Integer.parseInt(getElementValue(doc, SEARCH_KEY_LENGTH, 0)));
			pskc.setIteratorCounter(Integer.parseInt(getElementValue(doc, SEARCH_ITERATION_COUNTER, 0)));
			pskc.setIntervalInSeconds(getIntervalInSeconds(doc));
			pskc.setMacMethodCipValue(getElementValue(doc, SEARCH_CIPHER_VALUE, 0));
			pskc.setKeyPackageCipherValue(getElementValue(doc, SEARCH_CIPHER_VALUE, 1));
			CustomLogger.e(TAG, "PSKC: " + pskc.toString());

		} catch (Exception e) {
			CustomLogger.e(TAG, "buildPskc - Exception: " + e.getMessage());
		}
		return pskc;
	}

	public static Pskc buildPskcV2(String rawPskc) {
		Pskc pskc = new Pskc();

		XmlPullParserFactory parserFactory;

		try {

			InputStream is = new ByteArrayInputStream(rawPskc.getBytes(StandardCharsets.UTF_8));

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder xmlAsString = new StringBuilder(512);
			String line;
			try {
				while ((line = br.readLine()) != null) {
					xmlAsString.append(line.replace("<<", "*").replace(">>", "*"));
				}
			} catch (IOException e) {
				CustomLogger.e(TAG, "Exception in string building");
			}

			parserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserFactory.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, false);
			parser.setInput(new StringReader(xmlAsString.toString()));

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {

				String eltName;

				if(eventType == XmlPullParser.START_TAG) {
					eltName = parser.getName();
					switch (eltName) {
						case SEARCH_EXPIRING_DATE:
							pskc.setExpiredTimestamp(parser.nextText());
							break;
						case SEARCH_SPECIFIED:
							pskc.setSalt(parser.nextText());
							break;
						case SEARCH_RESPONSE_FORMAT:
							try {
								pskc.setRequiredDigit(Integer.valueOf(parser.getAttributeValue(null, SEARCH_LENGTH)));
							} catch(NumberFormatException e) {
								CustomLogger.e(TAG, "Exception :" + e.getMessage());
							}
							break;
						case SEARCH_KEY_LENGTH:
							try {
								String value = parser.nextText();
								pskc.setKeyLength(Integer.valueOf(value));
							} catch(NumberFormatException e) {
								CustomLogger.e(TAG, "Exception :" + e.getMessage());
							}
							break;
						case SEARCH_ITERATION_COUNTER:
							try {
								String value = parser.nextText();
								pskc.setIteratorCounter(Integer.valueOf(value));
							} catch(NumberFormatException e) {
								CustomLogger.e(TAG, "Exception :" + e.getMessage());
							}
							break;
						case SEARCH_TIME_INTERVAL:
							try {
								parser.next();
								String value = parser.nextText();
								pskc.setIntervalInSeconds(Integer.valueOf(value));
							} catch(NumberFormatException e) {
								CustomLogger.e(TAG, "Exception :" + e.getMessage());
							}
							break;
						case SEARCH_MAK_KEY:
							parser.next();
							pskc.setVersion(parser.getAttributeValue(null, SEARCH_ENCRYPTION_METHOD_VERSION));
							parser.next();
							parser.next();
							parser.next();
							pskc.setMacMethodCipValue(parser.nextText());
							break;
						case SEARCH_ENCRYPTED_VALUE:
							parser.next();
							parser.next();
							parser.next();
							pskc.setKeyPackageCipherValue(parser.nextText());
							break;
					}
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException | IOException e) {
			CustomLogger.e(TAG, "buildPskcV2 - Exception: " + e.getMessage());
			return buildPskc(rawPskc);
		}

		CustomLogger.d(TAG, "pskc: " + pskc.toString());
		return pskc;
	}

}
