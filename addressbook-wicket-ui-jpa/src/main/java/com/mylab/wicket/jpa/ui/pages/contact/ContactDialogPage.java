package com.mylab.wicket.jpa.ui.pages.contact;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.util.ListModel;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.markup.html.link.Link;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.mylab.wicket.jpa.sql.Address;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.sql.Contact;
import com.mylab.wicket.jpa.sql.JPAFunctions;
import com.mylab.wicket.jpa.ui.application.SignInSession;
import com.mylab.wicket.jpa.ui.pages.HomePage;

public class ContactDialogPage extends WebPage {
	private static final long serialVersionUID = 1L;

	public List<Address> addresses;

	public ContactDialogPage(final Contact contact) {
		
		addresses = new ArrayList<Address>();
		addresses.addAll(JPAFunctions.getAddresses(contact.getId()));

		final Form<List<Address>> form = new Form<List<Address>>("form", new ListModel<>(this.addresses));
		this.add(form);

		add(new Label("userInfo", getUserInfo(getSession())));

		// FeedbackPanel //
		form.add(new JQueryFeedbackPanel("feedback"));

		// Dialog //
		final AddressAddDialog adddialog = new AddressAddDialog("adddialog", "Address details") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				Address address = this.getModelObject();

				if (!addresses.contains(address)) {
					addresses.add(address);
					JPAFunctions.persist_newaddress(address);
					this.info(String.format("Address '%s' created", address.getStreet()));
				} else {
					JPAFunctions.persist_existingaddress(address);
					this.info(String.format("Address '%s' updated", address.getStreet()));
				}
			}

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				handler.add(form);
			}
		};

		this.add(adddialog);
		
		// Dialog //
		final AddressRemoveDialog removedialog = new AddressRemoveDialog("removedialog", "Address details") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				Address address = this.getModelObject();

				if (addresses.contains(address)) {
					this.info(String.format("Address '%s' removed", address.getStreet()));
					addresses.remove(address);
					JPAFunctions.remove_address(address.getId());
				}
			}

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				handler.add(form);
			}
		};

		this.add(removedialog);

		// ListView //
		form.add(new PropertyListView<Address>("address", form.getModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Address> addressItem) {
				addressItem.add(new Label("street"));
				addressItem.add(new Label("zipcode"));
				addressItem.add(new Label("city"));
				addressItem.add(new Label("country"));
				addressItem.add(new Label("isWorkAddress"));
				
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
	
	protected String getUserInfo(final Session session) {
		final AddressBookUser user = ((SignInSession) session).getUser();
		if (null != user) {
			return "User: " + user.getUsername() + " || Role: "+ user.getRole();
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