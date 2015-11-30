package com.mylab.wicket.jpa.ui.pages.contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
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
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.Validatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import com.googlecode.wicket.kendo.ui.form.datetime.DatePicker;
import com.mylab.wicket.jpa.sql.Address;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.sql.Contact;
import com.mylab.wicket.jpa.sql.JPAFunctions;
import com.mylab.wicket.jpa.ui.application.SignInSession;
import com.mylab.wicket.jpa.ui.pages.HomePage;
import com.mylab.wicket.jpa.ui.pages.address.AddAddress;
import com.mylab.wicket.jpa.ui.pages.address.ZIPValidator;
import com.mylab.wicket.jpa.ui.pages.select2.City;
import com.mylab.wicket.jpa.ui.pages.select2.Select2Choice;
import com.mylab.wicket.jpa.ui.pages.user.RemoveConfirmation;

public class EditContact extends WebPage {

	private static final long serialVersionUID = 8592212784624072042L;
	private static Session session;
	@SuppressWarnings("unused")
	private City city0 = City.Assendelft;

	public EditContact(final Contact contact) {

		session = getSession();

		// Show user's name and role:
		add(new Label("userInfo", getUserInfo(getSession())));

		CompoundPropertyModel<Contact> contactModel = new CompoundPropertyModel<Contact>(contact);
		setDefaultModel(contactModel);

		// Add a create Contact form to the page
		EditContactForm editContactForm = new EditContactForm("editContactForm", contactModel);
		add(editContactForm);

		// Add a FeedbackPanel for displaying our messages
		FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		add(feedbackPanel);

		// Addresses list view
		List<Address> addresses = new ArrayList<Address>();

		addresses.addAll(JPAFunctions.getAddresses(contact.getId()));

		final PageableListView<Address> listView;

		add(listView = new PageableListView<Address>("addressList", addresses, 10) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Address> addressItem) {

				final Address address = addressItem.getModelObject();

				final CompoundPropertyModel<Address> addressCompoundPropModel = new CompoundPropertyModel<Address>(
						addressItem.getModelObject());
				addressItem.setDefaultModel(addressCompoundPropModel);

				// Add a form to the list view
				Form<Address> form = new UpdateAddressForm("form", addressCompoundPropModel, contact);
				add(form);

				// Add the address' fields to the listview
				TextField<String> streetField = new TextField<String>("street");
				streetField.setRequired(true);
				streetField.setDefaultModel(new PropertyModel<Address>(address, "street"));
				addressItem.add(streetField);
				form.add(streetField);

				TextField<String> zipcodeField = new TextField<String>("zipcode");
				zipcodeField.setRequired(true);
				// zipcodeField.add(new ZIPValidator());
				zipcodeField.setDefaultModel(new PropertyModel<Address>(address, "zipcode"));
				addressItem.add(zipcodeField);
				form.add(zipcodeField);

				TextField<String> cityField = new TextField<String>("city");
				cityField.setRequired(true);
				cityField.setDefaultModel(new PropertyModel<Address>(address, "city"));
				addressItem.add(cityField);
				form.add(cityField);

				Select2Choice<String> country = new Select2Choice<>("country",
						new PropertyModel<String>(address, "country"), new AddAddress.CountriesStringProvider());
				form.add(country);

				CheckBox isWorkAddressCB = new CheckBox("isWorkAddress",
						new PropertyModel<Boolean>(addressItem.getModel(), "isWorkAddress"));
				isWorkAddressCB.setDefaultModel(new PropertyModel<Address>(address, "isWorkAddress"));
				addressItem.add(isWorkAddressCB);
				form.add(isWorkAddressCB);

				// Add a link to remove the chosen address
				addressItem.add(removeAddressLink("removeAddress", address));

				addressItem.add(form);
			}
		});

		// single-select no minimum example
		add(new Label("city0", new PropertyModel<>(this, "city0")));

		Select2Choice<City> city0 = new Select2Choice<>("city0", new PropertyModel<City>(this, "city0"),
				new AddAddress.CitiesProvider());
		city0.getSettings().setPlaceholder("Please select city").setAllowClear(true);
		add(new Form<Void>("single0").add(city0));

		// Page navigator for the pageable listview
		add(new PagingNavigator("pageNavigator", listView));

		add(addAddressLink("addAddressLink", contact));
		add(backLink("backLink"));

	}

	protected String getUserInfo(final Session session) {
		final AddressBookUser user = ((SignInSession) session).getUser();
		if (null != user) {
			return "User: " + user.getUsername() + " || Role: " + user.getRole();
		} else {
			return "No AddressBookUser data available.";
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

	public static Link<Void> addAddressLink(final String name, final Contact contact) {

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
				setResponsePage(new AddAddress(contact));
			}
		};
	}

	public static Link<Void> removeAddressLink(final String name, final Address address) {

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
				setResponsePage(new RemoveConfirmation(address, address.getStreet()));
			}
		};
	}

	public static class UpdateAddressForm extends Form<Address> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UpdateAddressForm(String id, IModel<Address> addressModel, Contact contact) {
			super(id, addressModel);
		}

		@Override
		public void onSubmit() {

			final Address a = getModelObject();
			ZIPValidator zipvalidator = new ZIPValidator(session);
			Validatable<String> validatable = zipvalidator.validate(a.getZipcode(), a.getCountry());
			List<IValidationError> errors = validatable.getErrors();
			if (errors.isEmpty()) {
				JPAFunctions.persist_address(a);
				String language = session.getLocale().getLanguage();
				switch (language) {
				case "nl":
					session.info("Adres opgeslagen!");
					break;
				case "de":
					session.info("Adresse gespeichert!");
					break;
				default:
					session.info("Address saved!");
					break;
				}
			} else {
				ValidationError error = (ValidationError) errors.get(0);
				getSession().info(error.getMessage());
			}
		}
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
		}

		@Override
		public final void onSubmit() {

			final Contact contact = getModelObject();
			Date birthday = dateOfBirthField.getConvertedInput();
			contact.setDateOfBirth(birthday);
			JPAFunctions.persist_existingcontact(contact);
			String language = session.getLocale().getLanguage();
			switch (language) {
			case "nl":
				info("Contact '" + contact.getFirstName() + "' opgeslagen!");
				break;
			case "de":
				info("Contact '" + contact.getFirstName() + "' gespeichert!");
				break;
			default:
				info("Contact '" + contact.getFirstName() + "' saved!");
				break;
			}
		}

	}
}