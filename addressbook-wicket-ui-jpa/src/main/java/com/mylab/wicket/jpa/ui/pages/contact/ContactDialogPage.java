package com.mylab.wicket.jpa.ui.pages.contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.Validatable;
import org.apache.wicket.validation.validator.StringValidator;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.form.button.Button;
import com.googlecode.wicket.jquery.ui.markup.html.link.Link;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.form.datetime.DatePicker;
import com.mylab.wicket.jpa.sql.Address;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.sql.Contact;
import com.mylab.wicket.jpa.sql.JPAFunctions;
import com.mylab.wicket.jpa.ui.application.SignInSession;
import com.mylab.wicket.jpa.ui.pages.HomePage;
import com.mylab.wicket.jpa.ui.pages.address.ZIPValidator;

public class ContactDialogPage extends WebPage {
	private static final long serialVersionUID = 1L;
	private static Session session;

	public List<Address> addresses;

	public ContactDialogPage(final Contact contact) {

		session = getSession();

		CompoundPropertyModel<Contact> contactModel = new CompoundPropertyModel<Contact>(contact);
		setDefaultModel(contactModel);

		// Add a create Contact form to the page
		EditContactForm editContactForm = new EditContactForm("editContactForm", contactModel);
		add(editContactForm);
		// Add a FeedbackPanel for displaying our messages

		add(new JQueryFeedbackPanel("feedback0"));

		addresses = new ArrayList<Address>();
		addresses.addAll(JPAFunctions.getAddresses(contact.getId()));

		final Form<List<Address>> form = new Form<List<Address>>("form", new ListModel<>(addresses));
		add(form);

		add(new Label("userInfo", getUserInfo(getSession())));

		// FeedbackPanel //
		form.add(new JQueryFeedbackPanel("feedback1"));

		// Dialog //
		final AddressAddDialog adddialog = new AddressAddDialog("adddialog", "Address details") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				Address address = getModelObject();
				ZIPValidator zipvalidator = new ZIPValidator(session);
				Validatable<String> validatable = zipvalidator.validate(address.getZipcode(), address.getCountry());
				List<IValidationError> errors = validatable.getErrors();
				if (errors.isEmpty()) {
					if (!addresses.contains(address)) {
						addresses.add(address);
						boolean success = JPAFunctions.persist_newaddress(address);
						if (success) {
							info(String.format("Address '%s' created", address.getStreet()));
						} else {
							error(String.format("Address '%s' not created", address.getStreet()));
						}
					} else {
						JPAFunctions.persist_existingaddress(address);
						info(String.format("Address '%s' updated", address.getStreet()));
					}
				} else {
					error(String.format("Address '%s' has ZIP errors", address.getStreet()));
				}
			}

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				handler.add(form);
			}
		};

		add(adddialog);

		// Dialog //
		final AddressRemoveDialog removedialog = new AddressRemoveDialog("removedialog", "Address details") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				Address address = getModelObject();

				if (addresses.contains(address)) {
					info(String.format("Address '%s' removed", address.getStreet()));
					addresses.remove(address);
					JPAFunctions.remove_address(address.getId());
				}
			}

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				handler.add(form);
			}
		};

		add(removedialog);

		// ListView //
		form.add(new PropertyListView<Address>("address", form.getModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Address> addressItem) {
				addressItem.add(new Label("street"));
				addressItem.add(new Label("zipcode"));
				addressItem.add(new Label("city"));
				addressItem.add(new Label("country"));
				addressItem.add(new CheckBox("isWorkAddress"));

				addressItem.add(new AjaxButton("edit") {

					private static final long serialVersionUID = 5412191533847334364L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						Address address = addressItem.getModelObject();

						adddialog.setTitle(target, "Update address " + address.getStreet());
						adddialog.setModelObject(address);
						adddialog.open(target);
					}
				});
				addressItem.add(new AjaxButton("remove") {

					private static final long serialVersionUID = 8957408668699107899L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						Address address = addressItem.getModelObject();

						removedialog.setTitle(target, "Remove address " + address.getStreet());
						removedialog.setModelObject(address);
						removedialog.open(target);
					}
				});
			}
		});

		// Buttons //
		form.add(new AjaxButton("create") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				adddialog.setTitle(target, "Create new address");
				Address address = new Address();
				address.setContact(contact);
				adddialog.setModelObject(address);
				adddialog.open(target);
			}
		});

		add(backLink("backLink"));
	}

	public static final class EditContactForm extends Form<Contact> {

		private static final long serialVersionUID = 1L;

		private TextField<String> firstNameField;
		private TextField<String> lastNameField;
		private DatePicker dateOfBirthField;
		private TextField<String> mailAddressField;

		public EditContactForm(final String id, IModel<Contact> contactModel) {
			super(id, contactModel);

			// Contact text fields:
			firstNameField = new TextField<String>("firstName");
			firstNameField.setRequired(true);
			firstNameField.add(StringValidator.maximumLength(30));
			add(firstNameField);

			lastNameField = new TextField<String>("lastName");
			lastNameField.setRequired(true);
			lastNameField.add(StringValidator.maximumLength(30));
			add(lastNameField);

			dateOfBirthField = new DatePicker("dateOfBirth",
					new PropertyModel<Date>((Contact) contactModel.getObject(), "dateOfBirth"));

			dateOfBirthField.add(new BirthDayValidator());

			add(dateOfBirthField);

			mailAddressField = new TextField<String>("mailAddress");
			mailAddressField.setRequired(true);
			mailAddressField.add(StringValidator.maximumLength(30));
			add(mailAddressField);

			add(new Button("editcontactbutton", Model.of("Submit")));

		}

		@Override
		public final void onSubmit() {

			final Contact contact = getModelObject();
			Date birthday = dateOfBirthField.getConvertedInput();
			contact.setDateOfBirth(birthday);
			String language = session.getLocale().getLanguage();
			boolean success = JPAFunctions.query_mail_existingcontact(contact);
			if (!success) {
				JPAFunctions.persist_existingcontact(contact);
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
			} else {
				switch (language) {
				case "nl":
					getSession().error(
							"Het Contact '" + contact.getFirstName() + "' is niet opgeslagen (mailadres niet uniek)!");
					break;
				case "de":
					getSession().error("Der Kontact '" + contact.getFirstName()
							+ "' ist nicht gespeichert (mailadress nicht einzigartig)!");
					break;
				default:
					getSession().error(
							"The Contact '" + contact.getFirstName() + "' was not saved (mailaddress not unique)!");
					break;
				}
			}
		}
	}

	protected String getUserInfo(final Session session) {
		final AddressBookUser user = ((SignInSession) session).getUser();
		if (null != user) {
			return "User: " + user.getUsername() + " || Role: " + user.getRole();
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
}