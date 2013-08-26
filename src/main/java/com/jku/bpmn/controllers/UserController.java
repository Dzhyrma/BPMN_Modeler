package com.jku.bpmn.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.GsonBuilder;
import com.jku.bpmn.model.Diagram;
import com.jku.bpmn.model.Pair;
import com.jku.bpmn.model.flowobject.UserTask;
import com.jku.bpmn.model.json.*;
import com.jku.bpmn.services.UserService;
import com.jku.bpmn.util.ProcessManager;

@Controller
public class UserController {

	private String currentUserName = "";
	@Resource(name = "userService")
	private UserService userService;

	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public String welcome(Model model, Principal principal) {

		currentUserName = principal.getName();
		model.addAttribute("userName", currentUserName);

		return "editor";
	}

	@RequestMapping(value = "/welcome", method = RequestMethod.POST)
	public @ResponseBody
	String welcome(HttpServletRequest request, @RequestParam(value = "json") String json, Principal principal) {
		String id = request.getParameter("id");
		System.out.println(json);
		if (id.equals("noid"))
			return userService.saveDiagram(principal.getName(), json);
		else {
			userService.saveDiagram(principal.getName(), json, id);
			return id;
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public @ResponseBody
	String welcome(HttpServletRequest request, Principal principal) {
		String id = request.getParameter("id");
		if (id == null || id.equals("noid") || id.equals("default"))
			return null;
		else {
			userService.deleteDiagram(principal.getName(), id);
			return "ok";
		}
	}

	@RequestMapping(value = "/getjson", method = RequestMethod.GET)
	public @ResponseBody
	String getJson(HttpServletRequest request, Model model, Principal principal) {
		String id = request.getParameter("id");
		Diagram diagram = userService.getDiagram(principal.getName(), id);
		if (diagram == null)
			return null;
		return new GsonBuilder().create().toJson(diagram.convertToJSONDiagram());// new
																					// GsonBuilder().create().toJson(diagram.convertToJSONDiagram());
	}
	
	@RequestMapping(value = "/getDiagramList", method = RequestMethod.GET)
	public @ResponseBody
	String getJson(Model model, Principal principal) {
		List<Pair<Integer, String>> diagrams = userService.getDiagrams(principal.getName());
		if (diagrams == null)
			return null;
		return new GsonBuilder().create().toJson(diagrams);
	}

	@RequestMapping(value = "/run", method = RequestMethod.GET)
	public @ResponseBody
	String run(HttpServletRequest request, Model model, Principal principal) {
		String id = request.getParameter("id");
		Diagram diagram = userService.getDiagram(principal.getName(), id);
		if (diagram == null)
			return new GsonBuilder().create().toJson("Save your diagram first.");
		Map<String, List<String>> errors = diagram.getErrors();
		Map<String, List<String>> warnings = diagram.getWarnings();
		JSONDiagramRunResponse response = new JSONDiagramRunResponse();
		response.setErrors(errors);
		response.setWarnings(warnings);
		if (errors.size() == 0) {
			response.setResponse("Diagram is running!");
			response.setRunning(true);
			diagram.run(this.userService.getUser(principal.getName()));
		}
		return new GsonBuilder().create().toJson(response);
	}

	@RequestMapping(value = "/validate", method = RequestMethod.GET)
	public @ResponseBody
	String validate(HttpServletRequest request, Model model, Principal principal) {
		String id = request.getParameter("id");
		Diagram diagram = userService.getDiagram(principal.getName(), id);
		if (diagram == null)
			return new GsonBuilder().create().toJson("Save your diagram first.");
		Map<String, List<String>> errors = diagram.getErrors();
		Map<String, List<String>> warnings = diagram.getWarnings();
		JSONDiagramRunResponse response = new JSONDiagramRunResponse();
		response.setErrors(errors);
		response.setWarnings(warnings);
		return new GsonBuilder().create().toJson(response);
	}

	@RequestMapping(value = "/complete", method = RequestMethod.GET)
	public String complete(HttpServletRequest request, Model model, Principal principal) {
		String uuid = request.getParameter("id");
		UserTask userTask = ProcessManager.getInstance().getValidationTask(uuid);
		if (userTask == null)
			return "taskError";
		model.addAttribute("taskName", userTask.getName());
		model.addAttribute("taskDescription", userTask.getDescription());
		model.addAttribute("uuid", uuid);
		Set<String> options = userTask.getOptions();
		if (options.size() > 0) {
			String selectionCode = "<select name='option'>";
			for (String string : options) {
				selectionCode += "<option value='"+string + "'>"+string+"</option>";
			}
			selectionCode += "</select>";
			model.addAttribute("options", selectionCode);
		}
		return "complete";
	}
	
	@RequestMapping(value = "/complete", method = RequestMethod.POST)
	public String completePost(HttpServletRequest request, Model model, Principal principal) {
		String uuid = request.getParameter("id");
		String option = request.getParameter("option");
		UserTask userTask = ProcessManager.getInstance().getValidationTask(uuid);
		if (userTask == null)
			return "taskError";
		model.addAttribute("taskName", userTask.getName());
		ProcessManager.getInstance().checkValidation(uuid, option);
		return "taskCompleted";
	}
}
