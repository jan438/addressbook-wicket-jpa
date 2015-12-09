package com.mylab.wicket.jpa.ui.pages.contact;

import java.util.Arrays;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import com.googlecode.wicket.jquery.ui.JQueryIcon;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.mylab.wicket.jpa.sql.Address;

abstract class AddressAddDialog extends AbstractFormDialog<Address> {
	private static final long serialVersionUID = 1L;
	protected final DialogButton btnSubmit = new DialogButton(SUBMIT, "Save", JQueryIcon.CHECK);
	protected final DialogButton btnCancel = new DialogButton(CANCEL, LBL_CANCEL, JQueryIcon.CANCEL);

	private Form<?> form;
	private FeedbackPanel feedback;

	public AddressAddDialog(String id, String title) {
		super(id, title, true);

		this.form = new Form<Address>("form", new CompoundPropertyModel<Address>(this.getModel()));
		this.add(this.form);

		// Slider //
		this.form.add(new RequiredTextField<String>("street"));
		this.form.add(new RequiredTextField<String>("zipcode"));
		this.form.add(new RequiredTextField<String>("city"));
		this.form.add(new RequiredTextField<String>("country"));
		this.form.add(new RequiredTextField<String>("isWorkAddress"));

		// FeedbackPanel //
		this.feedback = new JQueryFeedbackPanel("feedback");
		this.form.add(this.feedback);
	}

	@Override
	protected IModel<?> initModel() {
		return new Model<Address>();
	}

	// AbstractFormDialog //

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(this.btnSubmit, this.btnCancel);
	}

	@Override
	protected DialogButton getSubmitButton() {
		return this.btnSubmit;
	}

	@Override
	public Form<?> getForm() {
		return this.form;
	}

	// Events //

	@Override
	protected void onOpen(IPartialPageRequestHandler handler) {
		handler.add(this.form);
	}

	@Override
	public void onError(AjaxRequestTarget target) {
		target.add(this.feedback);
	}
}
