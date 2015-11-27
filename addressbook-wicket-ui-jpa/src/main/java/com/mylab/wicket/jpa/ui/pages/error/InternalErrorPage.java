package com.mylab.wicket.jpa.ui.pages.error;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import com.mylab.wicket.jpa.ui.pages.HomePage;

public class InternalErrorPage extends WebPage {

	private static final long serialVersionUID = 1L;

	public InternalErrorPage() {

		add(new Label("notification", "An error occured. Please contact the system administrator!"));

		add(new Link<Void>("back") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				setResponsePage(new HomePage());
			}
		});
	}

}