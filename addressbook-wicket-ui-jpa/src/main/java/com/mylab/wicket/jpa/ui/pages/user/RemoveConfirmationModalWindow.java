package com.mylab.wicket.jpa.ui.pages.user;

import org.apache.wicket.PageReference;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import com.mylab.wicket.jpa.sql.Address;
import com.mylab.wicket.jpa.sql.Contact;
import com.mylab.wicket.jpa.ui.JPAFunctions;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

public class RemoveConfirmationModalWindow extends WebPage {

	private static final long serialVersionUID = 961069910549425350L;

	public RemoveConfirmationModalWindow(final PageReference modalWindowPage, final ModalWindow modal,
			final Object object, String objectName) {

		add(new Label("notification",
				"Are you sure you want to remove the " + object.getClass().getSimpleName() + " '" + objectName + "'?"));

		if (object instanceof Contact) {

			add(confirmRemoveContactLink("confirmRemove", ((Contact) object), modal));

			AjaxLink<Void> denyRemoveContactLink = denyRemoveContactLink("denyRemove", modal);
			add(denyRemoveContactLink);

		} else {

			add(confirmRemoveAddressLink("confirmRemove", (Address) object));

			add(denyRemoveAddressLink("denyRemove", (Address) object));
		}

	}

	public static AjaxLink<Void> confirmRemoveContactLink(final String name, final Contact contact,
			final ModalWindow modal) {

		return new AjaxLink<Void>(name) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * @see org.apache.wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick(AjaxRequestTarget target) {

				JPAFunctions.remove_contact(contact.getId());

				// Pass success message to next page:
				getSession().info("The Contact '" + contact.getFirstName() + "' was removed!");
				modal.close(target);
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
				boolean success = JPAFunctions.remove_address(address.getId());
				if (success) {
					getSession().info("The Address in '" + address.getCity() + "' was removed!");
				}
				else {
					getSession().info("The Address in '" + address.getCity() + "' was not or already removed!");
				}
			}
		};
	}

	public static AjaxLink<Void> denyRemoveContactLink(final String name, final ModalWindow modal) {

		return new AjaxLink<Void>(name) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * @see org.apache.wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick(AjaxRequestTarget target) {
				// Pass message to next page:
				getSession().info("Remove canceled!");
				modal.close(target);
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
				// setResponsePage(new EditContact(address.getContact()));
			}
		};
	}
}