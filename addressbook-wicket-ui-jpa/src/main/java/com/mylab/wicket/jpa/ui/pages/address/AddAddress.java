package com.mylab.wicket.jpa.ui.pages.address;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.Validatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;
import com.mylab.wicket.jpa.sql.Address;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.sql.Contact;
import com.mylab.wicket.jpa.sql.JPAFunctions;
import com.mylab.wicket.jpa.ui.application.SignInSession;
import com.mylab.wicket.jpa.ui.pages.contact.EditContact;
import com.mylab.wicket.jpa.ui.pages.select2.ChoiceProvider;
import com.mylab.wicket.jpa.ui.pages.select2.City;
import com.mylab.wicket.jpa.ui.pages.select2.Country_de;
import com.mylab.wicket.jpa.ui.pages.select2.Country_nl;
import com.mylab.wicket.jpa.ui.pages.select2.Country_us;
import com.mylab.wicket.jpa.ui.pages.select2.Response;
import com.mylab.wicket.jpa.ui.pages.select2.Select2Choice;

/**
 * Page with a form to create a new address
 */
public class AddAddress extends WebPage {

	private static final long serialVersionUID = 1L;
	static Session session;
	private static final int PAGE_SIZE = 10;
	@SuppressWarnings("unused")
	private City city0 = City.Assendelft;

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 */
	public AddAddress(Contact contact) {

		session = getSession();

		// Show user's name and role:
		add(new Label("userInfo", getUserInfo(getSession())));

		CompoundPropertyModel<Contact> contactModel = new CompoundPropertyModel<Contact>(contact);
		setDefaultModel(contactModel);

		// Create and add feedback panel to page
		add(new FeedbackPanel("feedback"));

		// Add a create Contact form to the page
		add(new CreateAddressForm("createAddressForm", contactModel));

		// single-select no minimum example
		add(new Label("city0", new PropertyModel<>(this, "city0")));

		Select2Choice<City> city0 = new Select2Choice<>("city0", new PropertyModel<City>(this, "city0"),
				new CitiesProvider());
		city0.getSettings().setPlaceholder("Please select city").setAllowClear(true);
		add(new Form<Void>("single0").add(city0));

	}

	protected String getUserInfo(final Session session) {
		final AddressBookUser user = ((SignInSession) session).getUser();
		if (null != user) {
			return "User: " + user.getUsername() + " || Role: " + user.getRole();
		} else {
			return "No AddressBookUser data available.";
		}
	}

	private static List<City> cityqueryMatches(String term, int page, int pageSize) {
		List<City> result = new ArrayList<>();
		term = term == null ? "" : term.toUpperCase();
		final int offset = page * pageSize;

		int matched = 0;
		for (City city : City.values()) {
			if (result.size() == pageSize) {
				break;
			}

			if (city.getDisplayName().toUpperCase().contains(term)) {
				matched++;
				if (matched > offset) {
					result.add(city);
				}
			}
		}
		return result;
	}

	private static List<String> countrystringqueryMatches(String term, int page, int pageSize, Session session) {
		List<String> result = new ArrayList<>();
		term = term == null ? "" : term.toUpperCase();
		final int offset = page * pageSize;
		int matched = 0;
		String language = session.getLocale().getLanguage();
		switch (language) {
		case "nl":
			for (Country_nl country : Country_nl.values()) {
				if (result.size() == pageSize) {
					break;
				}

				if (country.getDisplayName().toUpperCase().contains(term)) {
					matched++;
					if (matched > offset) {
						result.add(country.getDisplayName());
					}
				}
			}
			break;
		case "de":
			for (Country_de country : Country_de.values()) {
				if (result.size() == pageSize) {
					break;
				}

				if (country.getDisplayName().toUpperCase().contains(term)) {
					matched++;
					if (matched > offset) {
						result.add(country.getDisplayName());
					}
				}
			}
			break;
		default:
			for (Country_us country : Country_us.values()) {
				if (result.size() == pageSize) {
					break;
				}

				if (country.getDisplayName().toUpperCase().contains(term)) {
					matched++;
					if (matched > offset) {
						result.add(country.getDisplayName());
					}
				}
			}
			break;
		}
		return result;
	}

	public static class CitiesProvider extends ChoiceProvider<City> {

		private static final long serialVersionUID = 1L;

		@Override
		public String getDisplayValue(City choice) {
			return choice.getDisplayName();
		}

		@Override
		public String getIdValue(City choice) {
			return choice.name();
		}

		@Override
		public void query(String term, int page, Response<City> response) {
			response.addAll(cityqueryMatches(term, page, PAGE_SIZE));
			response.setHasMore(response.size() == PAGE_SIZE);
		}

		@Override
		public Collection<City> toChoices(Collection<String> ids) {
			ArrayList<City> cities = new ArrayList<>();
			for (String id : ids) {
				cities.add(City.valueOf(id));
			}
			return cities;
		}
	}

	public static class CountriesStringProvider extends ChoiceProvider<String> {
		private static final long serialVersionUID = 1L;

		@Override
		public String getDisplayValue(String choice) {
			return choice;
		}

		@Override
		public String getIdValue(String choice) {
			return choice;
		}

		@Override
		public Collection<String> toChoices(Collection<String> ids) {
			return ids;
		}

		@Override
		public void query(String term, int page, Response<String> response) {
			response.addAll(countrystringqueryMatches(term, page, PAGE_SIZE, session));
			response.setHasMore(response.size() == PAGE_SIZE);
		}

	}

	@SuppressWarnings("serial")
	public static final class CreateAddressForm extends Form<Contact> {

		final Address address = new Address();

		public CreateAddressForm(final String id, IModel<Contact> contactModel) {
			super(id, contactModel);

			TextField<String> streetField = new TextField<String>("street");
			streetField.setDefaultModel(new PropertyModel<Address>(address, "street"));
			streetField.setRequired(true);
			streetField.add(StringValidator.maximumLength(30));
			add(streetField);

			TextField<String> zipcodeField = new TextField<String>("zipcode");
			zipcodeField.setDefaultModel(new PropertyModel<Address>(address, "zipcode"));
			zipcodeField.setRequired(true);
			add(zipcodeField);

			TextField<String> cityField = new TextField<String>("city");
			cityField.setDefaultModel(new PropertyModel<Address>(address, "city"));
			cityField.setRequired(true);
			cityField.add(StringValidator.maximumLength(30));
			add(cityField);

			Select2Choice<String> country = new Select2Choice<>("country",
					new PropertyModel<String>(address, "country"), new CountriesStringProvider());
			String language = session.getLocale().getLanguage();
			switch (language) {
			case "nl":
				country.getSettings().setPlaceholder("A.u.b. een land selecteren").setAllowClear(true);
				break;
			case "de":
				country.getSettings().setPlaceholder("Bitte eines Land auswählen").setAllowClear(true);
				break;
			default:
				country.getSettings().setPlaceholder("Please select country").setAllowClear(true);
				break;
			}
			add(country);

			CheckBox isWorkAddressCB = new CheckBox("isWorkAddress", new Model<Boolean>());
			isWorkAddressCB.setDefaultModel(new PropertyModel<Address>(address, "isWorkAddress"));
			add(isWorkAddressCB);

			add(backLink("backLink", contactModel.getObject()));
		}

		@Override
		public final void onSubmit() {

			Contact contact = getModelObject();
			ZIPValidator zipvalidator = new ZIPValidator(session);
			Validatable<String> validatable = zipvalidator.validate(address.getZipcode(), address.getCountry());
			List<IValidationError> errors = validatable.getErrors();
			if (errors.isEmpty()) {
				address.setContact(contact);
				JPAFunctions.persist_address(address);
				String language = getSession().getLocale().getLanguage();
				switch (language) {
				case "nl":
					getSession().info("Adres toegevoegd aan Contact '" + contact.getFirstName() + "'!");
					break;
				case "de":
					getSession().info("Adress hinzugefügt an Contact '" + contact.getFirstName() + "'!");
					break;
				default:
					getSession().info("Address added to Contact '" + contact.getFirstName() + "'!");
					break;
				}
				setResponsePage(new AddAddress(contact));
			} else {
				ValidationError error = (ValidationError) errors.get(0);
				getSession().info(error.getMessage());
			}
		}

		public static Link<Void> backLink(final String name, final Contact contact) {

			return new Link<Void>(name) {
				/**
				 * @see org.apache.wicket.markup.html.link.Link#onClick()
				 */
				@Override
				public void onClick() {
					setResponsePage(new EditContact(contact));
				}
			};
		}
	}
}