package com.ness.app.controller;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ness.app.domain.model.Knowledge;
import com.ness.app.domain.model.User;
import com.ness.app.domain.wrapper.KnowledgeWithSelection;
import com.ness.app.service.KnowledgeService;
import com.ness.app.service.UserService;
import com.ness.app.view.KnowledgeSettingsForm;
import com.ness.app.view.PasswordSettingsForm;
import com.ness.app.view.ProfileSettingsForm;

@Controller
@RequestMapping("/settings")
public class SettingsController extends BaseController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private KnowledgeService knowledgeService;
	
	@RequestMapping(value = { "", "/", "/profile" }, method = RequestMethod.GET)
	@PreAuthorize("#editableUser == principal.username or hasRole('HR')")
	public String showProfileSettings(
			@RequestParam("editableUser") String editableUser, 
			@RequestParam(name = "userUpdated", required = false, defaultValue = "false") Boolean userUpdated, 
			Model model) {
		
		model.addAttribute("activePage", "profile");
		model.addAttribute("editableUser", editableUser);
		model.addAttribute("userUpdated", userUpdated);
		
		User user = userService.findUserByUsername(editableUser);
		model.addAttribute("profileSettingsForm", new ProfileSettingsForm(user));
		
		return "settings/profile";
	}
	
	@RequestMapping(value = "/profile", method = RequestMethod.POST)
	@PreAuthorize("#editableUser == principal.username or hasRole('HR')")
	public String updateProfileSettings(
			@RequestParam("editableUser") String editableUser,
			@Valid @ModelAttribute ProfileSettingsForm profileSettingsForm, 
			BindingResult result,
			Model model) {
		
		model.addAttribute("editableUser", profileSettingsForm.getUsername());
		
		if (result.hasErrors()) {
			return "settings/profile";
		}
		
		userService.updateUserProfile(profileSettingsForm);
		
		model.addAttribute("userUpdated", true);
		
		return "redirect:/settings/profile";
	}
	
	@RequestMapping(value = "/password", method = RequestMethod.GET)
	@PreAuthorize("#editableUser == principal.username or hasRole('HR')")
	public String showPasswordSettings(
			@RequestParam("editableUser") String editableUser, 
			@RequestParam(name = "userUpdated", required = false, defaultValue = "false") Boolean userUpdated, 
			Model model) {
		
		model.addAttribute("activePage", "password");
		model.addAttribute("editableUser", editableUser);
		model.addAttribute("userUpdated", userUpdated);
		
		User user = userService.findUserByUsername(editableUser);
		model.addAttribute("passwordSettingsForm", new PasswordSettingsForm(user));
		
		return "settings/password";
	}
	
	@RequestMapping(value = "/password", method = RequestMethod.POST)
	@PreAuthorize("#editableUser == principal.username or hasRole('HR')")
	public String updatePasswordSettings(
			@RequestParam("editableUser") String editableUser,
			@Valid @ModelAttribute PasswordSettingsForm passwordSettingsForm,
			BindingResult result,
			Model model) {
		
		model.addAttribute("editableUser", editableUser);
		
		if (result.hasErrors()) {
			return "settings/password";
		}
		
		userService.updateUserPassword(passwordSettingsForm);
		model.addAttribute("userUpdated", true);
		
		return "redirect:/settings/password";
	}

	
	@RequestMapping(value = "/knowledges", method = RequestMethod.GET)
	@PreAuthorize("#editableUser == principal.username or hasRole('HR')")
	public String showKnowledgesSettings(
			@RequestParam("editableUser") String editableUser, 
			@RequestParam(name = "userUpdated", required = false, defaultValue = "false") Boolean userUpdated,
			Model model) {
		
		model.addAttribute("activePage", "knowledges");
		model.addAttribute("editableUser", editableUser);
		model.addAttribute("userUpdated", userUpdated);
		
		List<KnowledgeWithSelection> allKnowledgesWithSelectionForUser = 
				knowledgeService.findAllKnowledgesSelectedForUser(editableUser);
			
		model.addAttribute("knowledgesSettingsForm", new KnowledgeSettingsForm(allKnowledgesWithSelectionForUser));
		
		return "settings/knowledges";
	}
	
	@RequestMapping(value = "/knowledges", method = RequestMethod.POST)
	@PreAuthorize("#editableUser == principal.username or hasRole('HR')")
	public String updateKnowledgesSettings(
			@RequestParam("editableUser") String editableUser,
			@ModelAttribute KnowledgeSettingsForm knowledgesSettingsForm,
			BindingResult result,
			Model model) {
		
		model.addAttribute("editableUser", editableUser);
		
		if (result.hasErrors()) {
			return "settings/knowledges";
		}
		
		User user = userService.findUserByUsername(editableUser);
		
		Set<Knowledge> savedKnowledges = knowledgesSettingsForm.getKnowledges().stream()
				.filter(k -> k.getSelected())
				.map(k -> k.getKnowledgeId())
				.map(knowledgeService::findKnowledgeById)
				.collect(Collectors.toSet());
		
		user.setKnowledges(savedKnowledges);
		userService.save(user);
		model.addAttribute("userUpdated", true);
		
		return "redirect:/settings/knowledges";
	}
}
