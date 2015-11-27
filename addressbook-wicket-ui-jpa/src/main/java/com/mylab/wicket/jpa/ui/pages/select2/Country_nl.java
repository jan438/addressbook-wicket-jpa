package com.mylab.wicket.jpa.ui.pages.select2;

public enum Country_nl {
	NL("Nederland"), US("Amerika"), DE("Duitsland");

	private final String displayName;

	private Country_nl(String name) {
		this.displayName = name;
	}

	public String getDisplayName() {
		return displayName;
	}
}