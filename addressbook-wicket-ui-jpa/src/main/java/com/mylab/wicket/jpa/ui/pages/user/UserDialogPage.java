package com.mylab.wicket.jpa.ui.pages.user;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.util.ListModel;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.markup.html.link.Link;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.mylab.wicket.jpa.sql.AddressBookUser;
import com.mylab.wicket.jpa.sql.JPAFunctions;
import com.mylab.wicket.jpa.ui.pages.HomePage;

//@AuthorizeInstantiation("ADMIN")
//@AuthorizeAction(action = "RENDER", roles = {"ADMIN"})
public class UserDialogPage extends TemplatePage {
	private static final long serialVersionUID = 1L;

	public List<AddressBookUser> users;

	public UserDialogPage() {
		
		users = new ArrayList<AddressBookUser>();
		users.addAll(JPAFunctions.getAllUsers());

		final Form<List<AddressBookUser>> form = new Form<List<AddressBookUser>>("form", new ListModel<>(this.users));
		this.add(form);

		// FeedbackPanel //
		form.add(new JQueryFeedbackPanel("feedback"));

		// Dialog //
		final UserAddDialog adddialog = new UserAddDialog("adddialog", "User details") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				AddressBookUser user = this.getModelObject();

				if (!users.contains(user)) {
					users.add(user);
					JPAFunctions.persist_newuser(user);
					this.info(String.format("User '%s' created", user.getUsername()));
				} else {
					this.info(String.format("User '%s' updated", user.getUsername()));
				}
			}

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				handler.add(form);
			}
		};

		this.add(adddialog);
		
		// Dialog //
		final UserRemoveDialog removedialog = new UserRemoveDialog("removedialog", "User details") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				AddressBookUser user = this.getModelObject();

				if (users.contains(user)) {
					this.info(String.format("User '%s' removed", user.getUsername()));
					users.remove(user);
				}
			}

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				handler.add(form);
			}
		};

		this.add(removedialog);

		// ListView //
		form.add(new PropertyListView<AddressBookUser>("user", form.getModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<AddressBookUser> userItem) {
				userItem.add(new Label("username"));
				userItem.add(new Label("password"));
				userItem.add(new Label("role"));
				userItem.add(new AjaxButton("edit") {

					private static final long serialVersionUID = 5412191533847334364L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						AddressBookUser user = userItem.getModelObject();

						adddialog.setTitle(target, "Update user " + user.getUsername());
						adddialog.setModelObject(user);
						adddialog.open(target);
					}
				});
				userItem.add(new AjaxButton("remove") {

					private static final long serialVersionUID = 8957408668699107899L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						AddressBookUser user = userItem.getModelObject();

						removedialog.setTitle(target, "Remove user " + user.getUsername());
						removedialog.setModelObject(user);
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
				adddialog.setTitle(target, "Create new user");
				adddialog.setModelObject(new AddressBookUser()); 
				adddialog.open(target); 
			}
		});
		add(backLink("backLink"));
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