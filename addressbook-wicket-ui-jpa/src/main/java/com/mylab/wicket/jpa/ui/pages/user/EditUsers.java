package com.mylab.wicket.jpa.ui.pages.user;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import com.mylab.wicket.jpa.sql.Address;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.sql.JPAFunctions;
import com.mylab.wicket.jpa.ui.application.SignInSession;
import com.mylab.wicket.jpa.ui.pages.HomePage;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;

//@AuthorizeInstantiation("ADMIN")
//@AuthorizeAction(action = "RENDER", roles = {"ADMIN"})
public class EditUsers extends WebPage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static CompoundPropertyModel<AddressBookUser> userCompoundPropModel;

	public EditUsers() {

		// Show user's name and role:
		add(new Label("userInfo", getUserInfo(getSession())));

		// Add a FeedbackPanel for displaying our messages
		FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		add(feedbackPanel);

		List<AddressBookUser> userList = new ArrayList<AddressBookUser>();
		userList.addAll(JPAFunctions.getAllUsers());

		final PageableListView<AddressBookUser> listView;

		add(listView = new PageableListView<AddressBookUser>("userList", userList, 10) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<AddressBookUser> userItem) {

				final AddressBookUser u = userItem.getModelObject();

				userCompoundPropModel = new CompoundPropertyModel<AddressBookUser>(userItem.getModelObject());
				userItem.setDefaultModel(userCompoundPropModel);

				// Add a form for every User in the list view
				Form<AddressBookUser> form = new UpdateUserForm("form", userCompoundPropModel);
				add(form);

				TextField<String> usernameField = new TextField<String>("username");
				usernameField.setRequired(true);
				usernameField.setDefaultModel(new PropertyModel<Address>(u, "username"));
				userItem.add(usernameField);
				form.add(usernameField);

				TextField<String> passwordField = new TextField<String>("password");
				passwordField.setRequired(true);
				passwordField.setDefaultModel(new PropertyModel<Address>(u, "password"));
				userItem.add(passwordField);
				form.add(passwordField);

				List<String> userRoles = new ArrayList<String>(); // TODO: Read

				userRoles.add("USER");
				userRoles.add("GUEST");
				userRoles.add("ADMIN");

				DropDownChoice<String> rolesDDC = new DropDownChoice<String>("role", userRoles);
				rolesDDC.setRequired(true);
				rolesDDC.setDefaultModel(new PropertyModel<Address>(u, "role"));
				add(rolesDDC);
				form.add(rolesDDC);

				final SignInSession session = (SignInSession) getSession();
				final AddressBookUser userInSession = session.getUser();

				// Add a link to remove the chosen user if it's not the current
				// active user
				if (u.getUsername().equalsIgnoreCase(userInSession.getUsername())) {
					userItem.add(removeUserLink("removeUser", u).setEnabled(false));
				} else {
					userItem.add(removeUserLink("removeUser", u));
				}

				userItem.add(form);

			}
		});

		// Page navigator for the pageable listview
		add(new PagingNavigator("pageNavigator", listView));

		add(backLink("backLink"));

	}
	
	protected String getUserInfo(final Session session) {
		final AddressBookUser user = ((SignInSession) session).getUser();
		if (null != user) {
			return "User: " + user.getUsername() + " || Role: "+ user.getRole();
		} else {
			return "No user data available.";
		}
	}

	public static Link<Void> backLink(final String name) {

		return new Link<Void>(name) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * @see org.apache.wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				setResponsePage(new HomePage());
			}
		};
	}

	public static Link<Void> removeUserLink(final String name, final AddressBookUser u) {

		return new Link<Void>(name) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * @see org.apache.wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				JPAFunctions.remove_user(u.getId());
				// Pass success message to next page:
				String language = getSession().getLocale().getLanguage();
				switch (language) {
				case "nl":
					getSession().info("De Gebruiker '" + u.getUsername() + "' was verwijderd!");
					break;
				case "de":
					getSession().info("Der Benutzer '" + u.getUsername() + "' war entferned!");
					break;
				default:
					getSession().info("The User '" + u.getUsername() + "' was removed!");
					break;
				}
				setResponsePage(new EditUsers());
			}
		};
	}

	public static class UpdateUserForm extends Form<AddressBookUser> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UpdateUserForm(String id, IModel<AddressBookUser> userModel) {
			super(id, userModel);
		}

		@Override
		public void onSubmit() {
			final AddressBookUser u = getModelObject();
			boolean success = JPAFunctions.query_name_user(u.getUsername());
			if (!success) JPAFunctions.persist_newuser(u);
			else JPAFunctions.persist_existinguser(u);

			// A message stored with getSession().info("...") gets automatically
			// picked up by the target page's feedback panel:
			String language = getSession().getLocale().getLanguage();
			switch (language) {
			case "nl":
				getSession().info("Gebruiker opgeslagen!");
				break;
			case "de":
				getSession().info("Benutzer gespeichert!");
				break;
			default:
				getSession().info("User saved!");
				break;
			}
			setResponsePage(new EditUsers());
		}

	}
}