package com.jku.bpmn.controllers;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.jku.bpmn.model.User;
import com.jku.bpmn.services.UserService;
import com.jku.bpmn.util.MailManager;

@RequestMapping("/signup")
@SessionAttributes("user")
@Controller
public class SignUpFormController {

	private static final Logger logger = LoggerFactory
			.getLogger(SignUpFormController.class);

	@Resource(name = "userService")
	private UserService userService;

	@RequestMapping(method = RequestMethod.GET)
	public String showform(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("error", "style='display: none;'");
		return "signup";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitform(@ModelAttribute("user") User user,
			BindingResult result, Model model) {

		if (userService.getUser(user.getUserName()) != null) {
			model.addAttribute("error", "");
			return "signup";
		}
		userService.add(user);
		MailManager.getInstance().send(user.getEmail(), "BPMN Modeler registration", "Congratulations, "+user.getUserName()+"!\n\nYou successfully registered in the BPMN Modeler system.\n\nBest regards, BPMN Modeler team!");
		return "success";
	}
}
