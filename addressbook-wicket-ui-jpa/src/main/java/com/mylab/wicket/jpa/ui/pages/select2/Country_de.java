package com.mylab.wicket.jpa.ui.pages.select2;

public enum Country_de {
	NL("Niederlande"), US("Amerika"), DE("Deutschland");

	private final String displayName;

	private Country_de(String name) {
		this.displayName = name;
	}

	public String getDisplayName() {
		return displayName;
	}
}