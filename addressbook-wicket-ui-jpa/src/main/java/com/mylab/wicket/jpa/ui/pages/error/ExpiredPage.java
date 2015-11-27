package com.mylab.wicket.jpa.ui.pages.error;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import com.mylab.wicket.jpa.ui.application.SignIn;

public class ExpiredPage extends WebPage {

	private static final long serialVersionUID = 1L;

	public ExpiredPage() {

		String language = getSession().getLocale().getLanguage();
		switch (language) {
		case "nl":
			add(new Label("notification", "Uw sessie is verlopen!"));
			break;
		case "de":
			add(new Label("notification", "Ihren Session ist abgelaufen!"));
			break;
		default:
			add(new Label("notification", "Your session is expired!"));
			break;
		}

		add(new Link<Void>("back") {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				setResponsePage(new SignIn());
			}
		});
	}

}
