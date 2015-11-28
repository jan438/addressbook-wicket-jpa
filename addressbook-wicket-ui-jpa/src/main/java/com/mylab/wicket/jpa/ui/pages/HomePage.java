package com.mylab.wicket.jpa.ui.pages;

import java.util.List;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebSession;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.sql.Contact;
import com.mylab.wicket.jpa.sql.JPAFunctions;
import com.mylab.wicket.jpa.ui.application.SignIn;
import com.mylab.wicket.jpa.ui.application.SignInSession;
import com.mylab.wicket.jpa.ui.pages.contact.CreateNewContact;
import com.mylab.wicket.jpa.ui.pages.contact.EditContact;
import com.mylab.wicket.jpa.ui.pages.contact.MailContact;
import com.mylab.wicket.jpa.ui.pages.contact.ShowContact;
import com.mylab.wicket.jpa.ui.pages.contact.ShowContactModalWindow;
import com.mylab.wicket.jpa.ui.pages.user.EditUsers;
import com.mylab.wicket.jpa.ui.pages.user.RemoveConfirmation;
/**
 * The applications homepage. Shows a list of contacts
 */
// @AuthorizeAction(action = "RENDER", roles = { "SIGNED_IN" })
public class HomePage extends WebPage {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Page constructor
	 */
	public HomePage() {

		final SignInSession session = (SignInSession) getSession();

		// Show user's name and role:
		add(new Label("userInfo", getUserInfo(session)));

		// Add a FeedbackPanel for displaying our messages
		FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		add(feedbackPanel);

		// Add sign out button:
		add(signOutLink("signOut"));

		// Add Edit Users button:
		add(new EditUsersLink("editUsers", EditUsers.class));

		// List all Contacts
		final Contact contact = new Contact();
		final List<Contact> contacts = JPAFunctions.getContacts();
		final PageableListView<Contact> listView;

		add(listView = new PageableListView<Contact>("contactList", contacts, 10) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Contact> contactItem) {

				final Contact contact = contactItem.getModelObject();

				contactItem.add(new Label("firstName", contact.getFirstName()));
				contactItem.add(new Label("lastName", contact.getLastName()));
				contactItem.add(
						new Label("dateOfBirth", new PropertyModel<Contact>(contactItem.getModel(), "dateOfBirth")));
				contactItem.add(new Label("mailAddress", contact.getMailAddress()));

				// The old "normal" link:
				// contactItem.add(showContactLink("showContact", contact));

				// Modal window for ShowContact:
				// -----------------------------------------------------------------------------
				final ModalWindow modal;
				add(modal = new ModalWindow("modalWindow"));
				modal.setCookieName("modal-1");
				modal.setResizable(true);
				modal.setInitialWidth(880);
				modal.setInitialHeight(650);
				modal.setWidthUnit("px");
				modal.setHeightUnit("px");

				modal.setPageCreator(new ModalWindow.PageCreator() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					public Page createPage() {
						// Use this constructor to pass a reference of this
						// page.
						return new ShowContactModalWindow(HomePage.this.getPageReference(), modal, contact);
					}
				});
				modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					public void onClose(AjaxRequestTarget target) {
						// target.add(border);
						modal.close(target);
					}
				});
				modal.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					public boolean onCloseButtonClicked(AjaxRequestTarget target) {
						modal.close(target);
						return true;
					}
				});

				// Add the link that opens the modal window.
				contactItem.add(new AjaxLink<Void>("showContact") {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						modal.show(target);
					}
				});
				contactItem.add(modal);
				// Modal window end
				// -----------------------------------------------------------------------------------------

				contactItem.add(editContactLink("editContact", contact));

				// Link for remove confirmation:
				Link<Void> removeContactLink = removeContactLink("removeContact", contact);
				contactItem.add(removeContactLink);
				
				Link<Void> mailContactLink = mailContactLink("mailContact", contact);
				contactItem.add(mailContactLink);
			}

		});

		add(new PagingNavigator("pageNavigator", listView));

		// Create new Contact link
		add(new Link<Void>("create_new_contact_link") {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				setResponsePage(new CreateNewContact());
			}
		});

	}

	protected String getUserInfo(final SignInSession session) {
		final AddressBookUser user = session.getUser();
		if (null != user) {
			return "User: " + user.getUsername() + " || Role: " + user.getRole();
		} else {
			return "No AddressBookUser data available.";
		}
	}

	@AuthorizeAction(action = "ENABLE", roles = { "ADMIN" })
	public class EditUsersLink extends BookmarkablePageLink<Void> {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public EditUsersLink(String id, Class<EditUsers> pageClass) {
			super(id, pageClass);
		}
	}

	public static Link<Void> showContactLink(final String name, final Contact contact) {

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
				setResponsePage(new ShowContact(contact));
			}
		};
	}

	public static Link<Void> removeContactLink(final String name, final Contact contact) {

		return new Link<Void>(name) {
			private static final long serialVersionUID = 7487367604406288782L;

			/**
			 * @see org.apache.wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				setResponsePage(new RemoveConfirmation(contact, contact.getLastName()));
			}

			// Check the user's role:
			@Override
			public boolean isEnabled() {
				return isAdminOrUser(getWebSession());
			}
		};
	}
	
	public static Link<Void> mailContactLink(final String name, final Contact contact) {

		return new Link<Void>(name) {
			private static final long serialVersionUID = 7487367604406288782L;

			/**
			 * @see org.apache.wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				setResponsePage(new MailContact(contact));
			}

			// Check the user's role:
			@Override
			public boolean isEnabled() {
				return isAdminOrUser(getWebSession());
			}
		};
	}
	public static Link<Void> editContactLink(final String name, final Contact contact) {

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
				setResponsePage(new EditContact(contact));
			}

			// Check the user's role:
			@Override
			public boolean isEnabled() {
				return isAdminOrUser(getWebSession());
			}
		};
	}

	public static Link<Void> signOutLink(final String name) {

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

				// Log out:
				getSession().invalidate();
				setResponsePage(SignIn.class);
			}
		};
	}

	public static Boolean isAdminOrUser(WebSession session) {
		if (session instanceof SignInSession) {
			final SignInSession signInSession = (SignInSession) session;
			return signInSession.getRoles().hasRole("ADMIN") || signInSession.getRoles().hasRole("USER");
		}
		return true;
	}
}