package com.mylab.wicket.jpa.ui.pages.user;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.button.Button;
import com.googlecode.wicket.jquery.ui.markup.html.link.Link;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.sql.JPAFunctions;
import com.mylab.wicket.jpa.ui.application.SignIn;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.tooltip.TooltipBehavior;

public class RegisterUser extends WebPage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 *
	 */
	public RegisterUser() {
		
		Options options = new Options();
		options.set("position", "{ my: 'center top+3', at: 'center bottom' }");
//		options.set("track",true); //used to track the mouse

		add(new TooltipBehavior(options));
		
		AddressBookUser u = new AddressBookUser();
		CompoundPropertyModel<AddressBookUser> userModel = new CompoundPropertyModel<AddressBookUser>(u);
		setDefaultModel(userModel);

		// Create and add feedback panel to page
		add(new JQueryFeedbackPanel("feedback"));

		// Add a create Contact form to the page
		add(new CreateUserForm("createUserForm", userModel));

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
				setResponsePage(SignIn.class);
			}
		};
	}

	public static final class CreateUserForm extends Form<AddressBookUser> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CreateUserForm(final String id, IModel<AddressBookUser> userModel) {
			super(id, userModel);

			// AddressBookUser text fields:
			TextField<String> usernameField = new TextField<String>("username");
			usernameField.setRequired(true);
			usernameField.add(StringValidator.maximumLength(30));
			add(usernameField);

			TextField<String> passwordField = new TextField<String>("password");
			passwordField.setRequired(true);
			passwordField.add(StringValidator.maximumLength(30));
			add(passwordField);

			List<String> userRoles = new ArrayList<String>();
			userRoles.add("USER");
			userRoles.add("GUEST");
			userRoles.add("ADMIN");
			DropDownChoice<String> rolesDDC = new DropDownChoice<String>("role", userRoles);
			rolesDDC.setRequired(true);
			add(rolesDDC);

			add(new Button("registerbutton", Model.of("Submit")));

			add(backLink("backLink"));
		}

		@Override
		public final void onSubmit() {

			final AddressBookUser u = getModelObject();
			boolean success = JPAFunctions.query_name_user(u.getUsername());
			if (!success) {
				JPAFunctions.persist_newuser(u);
				// Pass success message to next page:
				String language = getSession().getLocale().getLanguage();
				switch (language) {
				case "nl":
					getSession().info(
							"Dank U '" + u.getUsername() + "' voor registreren! U kunt nu inloggen met Uw wachtwoord.");
					break;
				case "de":
					getSession().info("Vielen Dank '" + u.getUsername()
							+ "' für registrieren! Sie können nun einloggen mit Ihre Codewort.");
					break;
				default:
					getSession().info("Thank you '" + u.getUsername()
							+ "' for registering! You may now sign in with your password.");
					break;
				}
				setResponsePage(SignIn.class);
			} else {
				String language = getSession().getLocale().getLanguage();
				switch (language) {
				case "nl":
					info("'" + u.getUsername() + "' is al in gebruik!");
					break;
				case "de":
					info("'" + u.getUsername() + "' ist schon in gebrauch!");
					break;
				default:
					info("'" + u.getUsername() + "' is already in use!");
					break;
				}
			}
		}
	}
}