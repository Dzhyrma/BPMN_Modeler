package com.jku.bpmn.controllers;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jku.bpmn.social.facebook.IFacebookAccessTokenRetriever;

/**
 * Handles requests for the application home page.
 */
@SuppressWarnings("deprecation")
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);

	private FacebookProfile facebookProfile;

	@Autowired
	private IFacebookAccessTokenRetriever facebookAccessTokenRetriever;

	@RequestMapping(value = "/", method = RequestMethod.POST, params = "facebook")
	public String facebook(Locale locale, Model model) {
		/*
		 * FacebookOAuth2Template a = new FacebookOAuth2Template(
		 * "297412610390709", "19dd0204a1348804a9c3e122a9dac0ed"); Map<String,
		 * List<String>> params = new HashMap<String, List<String>>();
		 * ArrayList<String> uris = new ArrayList<String>();
		 * uris.add("http://localhost:8080/bpmn/facebook");
		 * params.put("redirect_uri", uris);
		 * System.out.println(a.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE,
		 * new OAuth2Parameters(params))); //
		 * System.out.println(FacebookClient.getInstance().getAuthTokenUrl());
		 */
		return facebookAccessTokenRetriever.getCodeRetrieverUri();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (authentication.getAuthorities().contains(
				new GrantedAuthorityImpl("ROLE_USER")))
			return "redirect:welcome";
		if (this.facebookProfile != null)
			model.addAttribute("name", this.facebookProfile.getName());
		model.addAttribute("loginerror", "style='display: none;'");

		return "home";
	}

	@RequestMapping(value = "/facebook", method = RequestMethod.GET)
	public String face(
			@RequestParam(value = "code", required = true) final String code) {
		System.out.println("Code");
		String accessToken = facebookAccessTokenRetriever
				.retrieveAccessToken(code);
		FacebookTemplate facebookTemplate = new FacebookTemplate(accessToken);
		this.facebookProfile = facebookTemplate.fetchObject("me",
				FacebookProfile.class);

		return "redirect:";
	}

	@RequestMapping(value = "/loginError*", method = RequestMethod.GET)
	public String loginError(Model model) {
		model.addAttribute("loginerror", "");
		logger.info("Login error");
		return "home";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(Model model) {
		logger.info("Logout");
		SecurityContextHolder.getContext().setAuthentication(null);
		return "redirect:welcome";
	}

	public static Logger getLogger() {
		return logger;
	}

}
