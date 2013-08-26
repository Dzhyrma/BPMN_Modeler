package com.jku.bpmn.social.facebook;

public interface IFacebookAccessTokenRetriever {

	public String retrieveAccessToken(String code);

	public String getCodeRetrieverUri();
}
