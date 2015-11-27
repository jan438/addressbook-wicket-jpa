package com.mylab.wicket.jpa.ui.pages.select2;

public class JQuery
{
	private JQuery()
	{

	}


	public static String execute(String script, Object... params)
	{
		return "(function($) { " + String.format(script, params) + " })(jQuery);";
	}
}
