package com.mylab.wicket.jpa.ui.pages.error;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import com.googlecode.wicket.jquery.ui.plugins.emoticons.EmoticonsBehavior;
import com.mylab.wicket.jpa.ui.pages.HomePage;

public class NotAllowedPage extends WebPage {

	private static final long serialVersionUID = 1L;

	public NotAllowedPage() {
		String language = getSession().getLocale().getLanguage();
		switch (language) {
		case "nl":
		    add(new Label("notification", "Het is U niet toegestaan deze pagina te bekijken!"));
			break;
		case "de":
		    add(new Label("notification", "Sie dürfen diese Seite nicht ansehen!"));
			break;
		default:
		    add(new Label("notification", "You are not allowed to view this page!"));
			break;
		}
		
		add(new EmoticonsBehavior("#notification"));
	    
	    add(new Link<Void>("back"){
        	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
        	public void onClick() {

        		setResponsePage(new HomePage());
        	}
        });
	}
}