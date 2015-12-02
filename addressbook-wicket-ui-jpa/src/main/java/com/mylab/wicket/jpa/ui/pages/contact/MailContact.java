package com.mylab.wicket.jpa.ui.pages.contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.value.ValueMap;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.sql.Contact;
import com.mylab.wicket.jpa.ui.application.MailClient;
import com.mylab.wicket.jpa.ui.application.SignInSession;
import com.mylab.wicket.jpa.ui.pages.HomePage;
import com.googlecode.wicket.jquery.ui.markup.html.link.Link;

public class MailContact extends WebPage {

	private static final long serialVersionUID = 1L;
	static String to;
	static Contact contact;
	private static final List<Comment> commentList = new ArrayList<>();

	public MailContact(Contact contact) {
		MailContact.contact = contact;

		add(new FeedbackPanel("feedback"));

		// Show user's name and role:
		add(new Label("userInfo", getUserInfo(getSession())));

		// Add comment form
		add(new MailContactForm("mailContactForm"));

		// Add commentListView of existing comments
		add(new PropertyListView<Comment>("comments", commentList) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(final ListItem<Comment> listItem) {
				listItem.add(new Label("date"));
				listItem.add(new MultiLineLabel("text"));
			}
		}).setVersioned(false);
	}

	public static Link<Void> backLink(final String name) {

		return new Link<Void>(name) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(HomePage.class);
			}
		};
	}

	public static final class MailContactForm extends Form<ValueMap> {

		private static final long serialVersionUID = 1L;

		public MailContactForm(final String id) {
			super(id, new CompoundPropertyModel<>(new ValueMap()));
			to = contact.getMailAddress();

			// this is just to make the unit test happy
			setMarkupId("commentForm");

			add(new Label("comment_text", new StringResourceModel("mailtext")));

			// Add text entry widget
			add(new TextArea<>("text").setType(String.class));

			// Add simple automated spam prevention measure.
			add(new TextField<>("comment").setType(String.class));

			add(backLink("backLink"));
		}

		@Override
		public final void onSubmit() {
			ValueMap values = getModelObject();

			// check if the honey pot is filled
			if (StringUtils.isNotBlank((String) values.get("comment"))) {
				error("Caught a spammer!!!");
				return;
			}
			// Construct a copy of the edited comment
			Comment comment = new Comment();

			// Set date of comment to add
			comment.setDate(new Date());
			comment.setText((String) values.get("text"));
			commentList.add(0, comment);

			// Clear out the text component
			values.put("text", "");

			try {
				MailClient client = new MailClient();
				// String server="pop3.mydomain.com";
				String server = "localhost";
				String from = "janboon438@gmail.com";
				String subject = "Test";
				String[] filenames = { "/home/jan/Afbeeldingen/35.png" };
				if (comment.getText() != null) {
					client.sendMail(server, from, to, subject, comment.toString(), filenames);
				}
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
	}

	protected String getUserInfo(final Session session) {
		final AddressBookUser user = ((SignInSession) session).getUser();
		if (null != user) {
			return "User: " + user.getUsername() + " || Role: " + user.getRole();
		} else {
			return "No AddressBookUser data available.";
		}
	}
}