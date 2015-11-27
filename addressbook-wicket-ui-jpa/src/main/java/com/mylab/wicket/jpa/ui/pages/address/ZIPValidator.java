package com.mylab.wicket.jpa.ui.pages.address;

import java.util.regex.Pattern;
import org.apache.wicket.Session;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.Validatable;
import org.apache.wicket.validation.ValidationError;

public class ZIPValidator implements IValidator<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// private final String ZIPCODE_PATTERN = "(NL-)?(\\d{4})\\s*([A-Z]{2})";
	// String zipNL = "(NL-)?(\\d{4})\\s*([A-Z]{2})";
	String zipNL = "^[0-9]{4}\\s*[A-Z]{2}";
	String zipUS = "^\\d{5}";
	String zipDE = "^\\d{5}";
	Session session;

	private final Pattern nl_pattern;
	private final Pattern us_pattern;
	private final Pattern de_pattern;

	public ZIPValidator(Session session) {
		this.session = session;
		nl_pattern = Pattern.compile(zipNL);
		us_pattern = Pattern.compile(zipUS);
		de_pattern = Pattern.compile(zipDE);
	}

	private void error(IValidatable<String> validatable, String errorKey) {
		ValidationError error = new ValidationError();
		error.addKey(getClass().getCanonicalName() + "." + errorKey);
		switch (session.getLocale().getLanguage()) {
		case "nl":
			error.setMessage("Postcode bestaat uit 4 cijfers en 2 hoofdletters of 5 cijfers!");
			break;
		case "de":
			error.setMessage("Das Postleitzahl besteht aus 5 Ziffern oder 4 Ziffern und 2 Großbuchstaben!");
			break;
		default:
			error.setMessage("ZIP Code must contain 5 digits or 4 digits and 2 capital letters!");
			break;
		}
		validatable.error(error);
	}

	@Override
	public void validate(IValidatable<String> validatable) {
		final String zipcode = validatable.getValue();
		if ((nl_pattern.matcher(zipcode).matches() == false) && (us_pattern.matcher(zipcode).matches() == false)
				&& (de_pattern.matcher(zipcode).matches() == false)) {
			error(validatable, "not-valid-zipcode");
		}
	}

	public Validatable<String> validate(String zipcode, String country) {
		Validatable<String> validatable = new Validatable<String>();
		switch (country) {
		case "Nederland":
		case "Netherlands":
		case "Niederlande":
			if (nl_pattern.matcher(zipcode).matches() == false) {
				ValidationError error = new ValidationError();
				switch (session.getLocale().getLanguage()) {
				case "nl":
					error.setMessage("Postcode bestaat uit 4 cijfers en 2 hoofdletters!");
					break;
				case "de":
					error.setMessage("Das Postleitzahl besteht aus 4 Ziffern und 2 Großbuchstaben!");
					break;
				default:
					error.setMessage("ZIP Code must contain 4 digits and 2 capital letters!");
					break;
				}
				validatable.error(error);
			}
			break;
		case "Duitsland":
		case "Germany":
		case "Deutschland":
			if (de_pattern.matcher(zipcode).matches() == false) {
				ValidationError error = new ValidationError();
				switch (session.getLocale().getLanguage()) {
				case "nl":
					error.setMessage("Postcode bestaat uit 5 cijfers!");
					break;
				case "de":
					error.setMessage("Das Postleitzahl besteht aus 5 Ziffern!");
					break;
				default:
					error.setMessage("ZIP Code must contain 5 digits!");
					break;
				}
				validatable.error(error);
			}
			break;
		default:
			if (us_pattern.matcher(zipcode).matches() == false) {
				ValidationError error = new ValidationError();
				switch (session.getLocale().getLanguage()) {
				case "nl":
					error.setMessage("Postcode bestaat uit 5 cijfers!");
					break;
				case "de":
					error.setMessage("Das Postleitzahl besteht aus 5 Ziffern!");
					break;
				default:
					error.setMessage("ZIP Code must contain 5 digits!");
					break;
				}
				validatable.error(error);
			}
			break;
		}
		return validatable;
	}
}
