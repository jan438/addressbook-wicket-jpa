package com.mylab.wicket.jpa.ui.pages.contact;

import java.util.Date;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public class BirthDayValidator implements IValidator<Date> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date today;

	public BirthDayValidator() {
		today = new Date();
	}

	private void error(IValidatable<Date> validatable, String errorKey) {
		ValidationError error = new ValidationError();
		error.addKey(getClass().getCanonicalName() + "." + errorKey);
		validatable.error(error);
	}

	@Override
	public void validate(IValidatable<Date> validatable) {
		final Date birthday = validatable.getValue();
			// validate birthday
			if (birthday.compareTo(today) > 0) {
				error(validatable, "not-valid-birthday");
			}
	}
}
