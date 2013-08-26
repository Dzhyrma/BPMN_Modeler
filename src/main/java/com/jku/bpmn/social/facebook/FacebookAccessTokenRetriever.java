package com.jku.bpmn.social.facebook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Component;

import com.jku.bpmn.controllers.HomeController;

@Component("facebookAccessTokenRetriever")
public class FacebookAccessTokenRetriever implements
		IFacebookAccessTokenRetriever {
	private static final String FB_OAUTH_URI = "https://graph.facebook.com/oauth/";
	private static final String FB_ACCESS_TOKEN_URI_FORMAT_STRING = FB_OAUTH_URI
			+ "access_token?client_id=%s&client_secret=%s&redirect_uri=%s&code=%s";
	private static final String FB_CODE_URI_FORMAT_STRING = "redirect:" + FB_OAUTH_URI + "authorize?client_id=%s&display=page&redirect_uri=%s";

	private String secret;
	private String apiKey;
	private String redirectURI;

	private String assembleAuthentication(String authCode) {
		return String.format(FB_ACCESS_TOKEN_URI_FORMAT_STRING, apiKey, secret, redirectURI, authCode);
	}

	public String retrieveAccessToken(String code) {
		String accessToken = null;
		if (null != code) {
			String authURL = assembleAuthentication(code);
			URL uri = null;
			try {
				uri = new URL(authURL);
				String result = crawlToURL(uri);
				String[] values = result.split("&");
				for (String value : values) {
					String[] valuePair = value.split("=");
					if (valuePair.length != 2) {
						throw new RuntimeException("Unexpected auth response");
					} else {
						if (valuePair[0].equals("access_token")) {
							accessToken = valuePair[1];
						}
					}
				}
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		return accessToken;
	}

	private String crawlToURL(URL uri) {
		ByteArrayOutputStream byteOutputStream = null;
		InputStream inputStream = null;
		String stream = null;
		try {
			inputStream = uri.openStream();
			byteOutputStream = new ByteArrayOutputStream();
			int i;
			while ((i = inputStream.read()) != -1)
				byteOutputStream.write(i);
			stream = new String(byteOutputStream.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (null != byteOutputStream)
				try {
					byteOutputStream.close();
				} catch (IOException e) {
					HomeController.getLogger().error(e.getMessage());
				}
			if (null != inputStream)
				try {
					inputStream.close();
				} catch (IOException e) {
					HomeController.getLogger().error(e.getMessage());
				}
		}
		return stream;
	}

	public String getCodeRetrieverUri() {
		return String.format(FB_CODE_URI_FORMAT_STRING, apiKey, redirectURI);
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setApiKey(String clientId) {
		this.apiKey = clientId;
	}

	public void setRedirectURI(String redirectURI) {
		this.redirectURI = redirectURI;
	}

}
