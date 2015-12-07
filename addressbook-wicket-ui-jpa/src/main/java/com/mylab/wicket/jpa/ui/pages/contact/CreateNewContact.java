package com.mylab.wicket.jpa.ui.pages.contact;

import java.util.Date;
import com.googlecode.wicket.jquery.ui.form.button.Button;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.kendo.ui.form.datetime.DatePicker;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.sql.Contact;
import com.mylab.wicket.jpa.sql.JPAFunctions;
import com.mylab.wicket.jpa.ui.application.SignIn;
import com.mylab.wicket.jpa.ui.application.SignInSession;

/**
 * Homepage with a form to create a new contact
 */
@AuthorizeAction(action = "RENDER", roles = { "ADMIN" })
public class CreateNewContact extends WebPage {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 *
	 */
	public CreateNewContact() {

		// Show user's name and role:
		add(new Label("userInfo", getUserInfo(getSession())));

		Contact contact = new Contact();
		CompoundPropertyModel<Contact> contactModel = new CompoundPropertyModel<Contact>(contact);
		setDefaultModel(contactModel);

		// Create and add feedback panel to page
		add(new JQueryFeedbackPanel("feedback"));

		// Add a create Contact form to the page
		add(new CreateContactForm("createContactForm", contactModel));

	}

	protected String getUserInfo(final Session session) {
		final AddressBookUser user = ((SignInSession) session).getUser();
		if (null != user) {
			return "User: " + user.getUsername() + " || Role: " + user.getRole();
		} else {
			return "No AddressBookUser data available.";
		}
	}

	@SuppressWarnings("serial")
	public static final class CreateContactForm extends Form<Contact> {

		private DatePicker dateOfBirthField;

		public CreateContactForm(final String id, IModel<Contact> contactModel) {
			super(id, contactModel);

			// Contact text fields:
			TextField<String> firstNameField = new TextField<String>("firstName");
			firstNameField.setRequired(true);
			firstNameField.add(StringValidator.maximumLength(30));
			add(firstNameField);

			TextField<String> lastNameField = new TextField<String>("lastName");
			lastNameField.setRequired(true);
			lastNameField.add(StringValidator.maximumLength(30));
			add(lastNameField);

			dateOfBirthField = new DatePicker("dateOfBirth",
					new PropertyModel<Date>((Contact) contactModel.getObject(), "dateOfBirth"));

			dateOfBirthField.add(new BirthDayValidator());

			add(dateOfBirthField);

			TextField<String> mailAddressField = new TextField<String>("mailAddress");
			mailAddressField.setRequired(true);
			mailAddressField.add(StringValidator.maximumLength(30));
			add(mailAddressField);

			add(new Button("createcontactbutton", Model.of("Submit")));

			add(ShowContact.backLink("backLink"));
		}

		@Override
		public final void onSubmit() {

			final Contact contact = getModelObject();

			Date birthday = dateOfBirthField.getConvertedInput();
			contact.setDateOfBirth(birthday);

			String language = getSession().getLocale().getLanguage();
			boolean success = JPAFunctions.query_mail_existingcontacts(contact.getMailAddress());
			if (!success) {
				JPAFunctions.persist_newcontact(contact);
				// Pass success message to next page:
				switch (language) {
				case "nl":
					getSession().info("Het Contact '" + contact.getFirstName() + "' is opgeslagen!");
					break;
				case "de":
					getSession().info("Der Kontact '" + contact.getFirstName() + "' ist gespeichert!");
					break;
				default:
					getSession().info("The Contact '" + contact.getFirstName() + "' was saved!");
					break;
				}
				setResponsePage(new EditContact(contact));
			} else {
				switch (language) {
				case "nl":
					getSession().info("Het Contact '" + contact.getFirstName() + "' is niet opgeslagen!");
					break;
				case "de":
					getSession().info("Der Kontact '" + contact.getFirstName() + "' ist nicht gespeichert!");
					break;
				default:
					getSession().info("The Contact '" + contact.getFirstName() + "' was not saved!");
					break;
				}
			}
		}
	}
}