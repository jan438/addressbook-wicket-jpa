package com.mylab.wicket.jpa.ui.pages.select2;

public enum City {
	Assendelft("Assendelft"), Krommenie("Krommenie"), Zaandam(
		"Zaandam");

	private final String displayName;

	private City(String name)
	{
		this.displayName = name;
	}

	public String getDisplayName()
	{
		return displayName;
	}

}
