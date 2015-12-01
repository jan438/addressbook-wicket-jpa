package com.mylab.wicket.jpa.ui.pages.user;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import com.googlecode.wicket.jquery.ui.form.button.Button;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.ui.application.SignIn;

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

		AddressBookUser u = new AddressBookUser();
		CompoundPropertyModel<AddressBookUser> userModel = new CompoundPropertyModel<AddressBookUser>(u);
		setDefaultModel(userModel);

		// Create and add feedback panel to page
		add(new FeedbackPanel("feedback"));

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
		
		@PersistenceContext
		public static boolean persist(AddressBookUser user) {
			boolean success = false;
			EntityManagerFactory entityManagerFactory = Persistence
					.createEntityManagerFactory("sampleJPALoadScriptSourcePU");
			EntityManager em = entityManagerFactory.createEntityManager();
			em.getTransaction().begin();
			Query q = em.createNativeQuery("select nextval('addressbookuser_id_seq')");
			long result = (long) q.getSingleResult();
			if (result > 0) {
				user.setId(result);
				try {
					em.persist(user);
					em.getTransaction().commit();
					success = true;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			em.close();
			return success;
		}

		@Override
		public final void onSubmit() {

			final AddressBookUser u = getModelObject();
			Boolean saveSuccessful = persist(u);

			if (saveSuccessful) {

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