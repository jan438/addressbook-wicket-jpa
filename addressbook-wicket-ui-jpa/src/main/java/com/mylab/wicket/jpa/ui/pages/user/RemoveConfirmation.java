package com.mylab.wicket.jpa.ui.pages.user;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import com.mylab.wicket.jpa.sql.Address;
import com.mylab.wicket.jpa.sql.Contact;
import com.mylab.wicket.jpa.ui.JPAFunctions;
import com.mylab.wicket.jpa.ui.pages.HomePage;
import com.mylab.wicket.jpa.ui.pages.contact.EditContact;

import org.apache.wicket.Session;

public class RemoveConfirmation extends WebPage {

	private static final long serialVersionUID = 961069910549425350L;
	Session session;

	public RemoveConfirmation(final Object object, String objectName) {

		session = getSession();
		String language = session.getLocale().getLanguage();
		switch (language) {
		case "nl":
			add(new Label("notification", "Weet U zeker dat U het " + object.getClass().getSimpleName() + " '"
					+ objectName + "' wilt verwijderen?"));
			break;
		case "de":
			add(new Label("notification", "Sind Sie sicher das Sie der " + object.getClass().getSimpleName() + " '"
					+ objectName + "' wollen entfernen?"));
			break;
		default:
			add(new Label("notification", "Are you sure you want to remove the " + object.getClass().getSimpleName()
					+ " '" + objectName + "'?"));
			break;
		}

		if (object instanceof Contact) {

			add(confirmRemoveContactLink("confirmRemove", (Contact) object));

			Link<Void> denyRemoveContactLink = denyRemoveContactLink("denyRemove");
			add(denyRemoveContactLink);

		} else {

			add(confirmRemoveAddressLink("confirmRemove", (Address) object));

			add(denyRemoveAddressLink("denyRemove", (Address) object));
		}

	}

	public static Link<Void> confirmRemoveContactLink(final String name, final Contact contact) {

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
				JPAFunctions.remove_contact(contact.getId());

				// Pass success message to next page:
				String language = getSession().getLocale().getLanguage();
				switch (language) {
				case "nl":
					getSession().info("Het Contact '" + contact.getFirstName() + "' was verwijderd!");
					break;
				case "de":
					getSession().info("Das Kontact '" + contact.getFirstName() + "' wurde entfernt!");
					break;
				default:
					getSession().info("The Contact '" + contact.getFirstName() + "' was removed!");
					break;
				}
				setResponsePage(new HomePage());
			}
		};
	}

	public static Link<Void> confirmRemoveAddressLink(final String name, final Address address) {

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
				Contact contact = address.getContact();
				boolean success = JPAFunctions.remove_address(address.getId());

				String language = getSession().getLocale().getLanguage();
				if (success) {
					// Pass success message to next page:
					switch (language) {
					case "nl":
						getSession().info("Het Adres in '" + address.getCity() + "' was verwijderd!");
						break;
					case "de":
						getSession().info("Der Adress in '" + address.getCity() + "' war entferned!");
						break;
					default:
						getSession().info("The Address in '" + address.getCity() + "' was removed!");
						break;
					}
				} else {
					switch (language) {
					case "nl":
						getSession().info("Het Adres in '" + address.getCity() + "' was niet of reeds verwijderd!");
						break;
					case "de":
						getSession()
								.info("Der Adress in '" + address.getCity() + "' war nicht oder bereits entferned!");
						break;
					default:
						getSession().info("The Address in '" + address.getCity() + "' was or already removed!");
						break;
					}
				}
				setResponsePage(new EditContact(contact));
			}
		};
	}

	public static Link<Void> denyRemoveContactLink(final String name) {

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
				// Pass message to next page:
				getSession().info("Remove canceled!");

				setResponsePage(new HomePage());
			}
		};
	}

	public static Link<Void> denyRemoveAddressLink(final String name, final Address address) {

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
				// Pass message to next page:
				getSession().info("Remove canceled!");

				setResponsePage(new EditContact(address.getContact()));
			}
		};
	}
}