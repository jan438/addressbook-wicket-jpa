package com.mylab.wicket.jpa.ui.pages.select2;

public enum Country_us {
	NL("Netherlands"), US("America"), DE("Germany");

	private final String displayName;

	private Country_us(String name) {
		this.displayName = name;
	}

	public String getDisplayName() {
		return displayName;
	}
}