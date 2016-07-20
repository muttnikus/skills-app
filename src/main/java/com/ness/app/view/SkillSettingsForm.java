package com.ness.app.view;

import java.util.List;

import com.ness.app.domain.wrapper.SkillWithSelection;

public class SkillSettingsForm {
	private List<SkillWithSelection> skills;

	public SkillSettingsForm() { }
	
	public SkillSettingsForm(List<SkillWithSelection> skills) {
		this.skills = skills;
	}

	public List<SkillWithSelection> getSkills() {
		return skills;
	}

	public void setSkills(List<SkillWithSelection> skills) {
		this.skills = skills;
	}
}
