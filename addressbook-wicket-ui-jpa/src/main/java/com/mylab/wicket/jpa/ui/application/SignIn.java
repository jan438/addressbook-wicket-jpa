package com.mylab.wicket.jpa.ui.application;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import com.googlecode.wicket.jquery.ui.form.button.Button;
import com.googlecode.wicket.jquery.ui.markup.html.link.AjaxLink;
import com.googlecode.wicket.jquery.ui.markup.html.link.Link;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.captcha.kittens.KittenCaptchaPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.WebRequest;
import com.mylab.wicket.jpa.ui.pages.HomePage;
import com.mylab.wicket.jpa.ui.pages.user.RegisterUser;

public final class SignIn extends WebPage {

	private static final long serialVersionUID = 1L;

	/** Relevant locales wrapped in a list. */
	public static final List<Locale> LOCALES = Arrays.asList(new Locale("nl", "NL"), Locale.ENGLISH, Locale.GERMAN);

	private KittenCaptchaPanel captcha;
	private int errors;
	SignInSession session = getMySession();

	public void setLocale(Locale locale) {
		if (locale != null) {
			getSession().setLocale(locale);
		}
	}

	/**
	 * Choice for a locale.
	 */
	private final class LocaleChoiceRenderer extends ChoiceRenderer<Locale> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor.
		 */
		public LocaleChoiceRenderer() {
		}

		/**
		 * @see org.apache.wicket.markup.html.form.IChoiceRenderer#getDisplayValue(Object)
		 */
		@Override
		public Object getDisplayValue(Locale locale) {
			return locale.getDisplayName(getLocale());
		}
	}

	/**
	 * Dropdown with Locales.
	 */
	private final class LocaleDropDownChoice extends DropDownChoice<Locale> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 * 
		 * @param id
		 *            component id
		 */
		public LocaleDropDownChoice(String id) {
			super(id, LOCALES, new LocaleChoiceRenderer());

			// set the model that gets the current locale, and that is used for
			// updating the current locale to property 'locale' of FormInput
			setModel(new PropertyModel<Locale>(SignIn.this, "locale"));
		}

		/**
		 * @see org.apache.wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
		 */
		@Override
		public void onSelectionChanged(Locale newSelection) {
			setResponsePage(SignIn.class);
		}

		/**
		 * @see org.apache.wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
		 */
		@Override
		protected boolean wantOnSelectionChangedNotifications() {
			// we want roundtrips when a the AddressBookUser selects another
			// item
			return true;
		}
	}

	/**
	 * @return Session
	 */
	private SignInSession getMySession() {
		return (SignInSession) getSession();
	}

	/**
	 * Page Constructor
	 */
	public SignIn() {
		// Create feedback panel and add to page
		add(new JQueryFeedbackPanel("feedback"));

		// Add register button:
		add(registerUserLink("registerUser"));

		// Check if there already is a session:
		if (session.isSignedIn()) {
			// set feedback message and go to HomePage:
			getSession().info("Welcome back '" + session.getUser().getUsername() + "'!");
			setResponsePage(new HomePage());
		} else {
			// Add sign-in form to the page:
			add(new SignInForm("signInForm"));
		}
	}

	public static Link<Void> registerUserLink(final String name) {

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

				setResponsePage(new RegisterUser());
			}
		};
	}

	boolean isSpamBot() {
		return errors > 3;
	}

	/**
	 * Sign in form
	 */
	public final class SignInForm extends Form<Void> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		TextField<String> usernameField;
		PasswordTextField passwordField;

		/**
		 * Constructor
		 */
		public SignInForm(final String id) {

			super(id);

			// Dropdown for selecting locale
			add(new LocaleDropDownChoice("localeSelect"));

			// Link to return to default locale
			add(new Link<Void>("defaultLocaleLink") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					WebRequest request = (WebRequest) getRequest();
					setLocale(request.getLocale());
				}
			});
			usernameField = new TextField<String>("username", Model.of(" "));
			usernameField.setRequired(true);
			add(usernameField);

			passwordField = new PasswordTextField("password", Model.of(" "));
			passwordField.setRequired(true);
			add(passwordField);

			add(captcha = new KittenCaptchaPanel("captcha", new Dimension(400, 200)));

			// In a real application, you'd check the kittens in a form
			add(new AjaxLink<Void>("checkKittens") {
				private static final long serialVersionUID = 642245961797905032L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					String language = session.getLocale().getLanguage();
					if (!isSpamBot() && captcha.allKittensSelected()) {
						getSession().setAttribute("kittens_checked", "true");
						switch (language) {
						case "nl":
							getSession().info("Succes, U kunt nu inloggen!");
							break;
						case "de":
							getSession().info("Erfolg, Sie können jetzt login!");
							break;
						default:
							getSession().info("Success, You can login now!");
							break;
						}
					} else {
						errors++;
						if (isSpamBot()) {
							getSession().error("Spammer alert");
						} else {
							switch (language) {
							case "nl":
								getSession().error("Wilt U a.u.b. opnieuw proberen");
								break;
							case "de":
								getSession().error("Bitte, Versuchen Sie nochmals");
								break;
							default:
								getSession().error("Please try again");
								break;
							}
						}
						target.add(captcha);
					}
					captcha.reset();
					setResponsePage(new SignIn());
				}
			});
			add(new Button("signinbutton", Model.of("Submit")));
		}

		/**
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		public final void onSubmit() {
			String kittens_checked = (String) getSession().getAttribute("kittens_checked");
			if ((kittens_checked != null) && kittens_checked.equals("true")) {
				// Sign the AddressBookUser in
				if (session.signIn(usernameField.getModelObject(), passwordField.getModelObject())) {
					String language = session.getLocale().getLanguage();
					switch (language) {
					case "nl":
						getSession().info("Welkom '" + usernameField.getModelObject() + "'!");
						break;
					case "de":
						getSession().info("Willkommen '" + usernameField.getModelObject() + "'!");
						break;
					default:
						getSession().info("Welcome '" + usernameField.getModelObject() + "'!");
						break;
					}
					setResponsePage(new HomePage());
				} else {
					String errmsg = getString("loginError", null, "Login failed!");
					error(errmsg);
				}
			} else {
				String errmsg = getString("loginError", null, "Please check the kittens first!");
				error(errmsg);
			}
		}
	}
}