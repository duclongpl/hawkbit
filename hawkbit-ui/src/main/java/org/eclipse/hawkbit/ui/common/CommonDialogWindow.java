/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.hawkbit.ui.components.SPUIComponentProvider;
import org.eclipse.hawkbit.ui.decorators.SPUIButtonStyleBorderWithIcon;
import org.eclipse.hawkbit.ui.utils.I18N;
import org.eclipse.hawkbit.ui.utils.SPUIComponentIdProvider;
import org.eclipse.hawkbit.ui.utils.SPUIStyleDefinitions;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Superclass for pop-up-windows including a minimize and close icon in the
 * upper right corner and a save and cancel button at the bottom.
 */
@SpringComponent
@ViewScope
public class CommonDialogWindow extends Window {

    private static final long serialVersionUID = -1321949234316858703L;

    private final VerticalLayout mainLayout = new VerticalLayout();

    private final String caption;

    private final Component content;

    private final String helpLink;

    private Button saveButton;

    private Button cancelButton;

    private HorizontalLayout buttonsLayout;

    protected ValueChangeListener buttonEnableListener;

    private final ClickListener saveButtonClickListener;

    private final ClickListener cancelButtonClickListener;

    private Map<String, Boolean> requiredFields;

    @Autowired
    private I18N i18n;

    /**
     * Constructor.
     * 
     * @param caption
     *            the caption
     * @param content
     *            the content
     * @param helpLink
     *            the helpLinks
     * @param saveButtonClickListener
     *            the saveButtonClickListener
     * @param cancelButtonClickListener
     *            the cancelButtonClickListener
     */
    public CommonDialogWindow(final String caption, final Component content, final String helpLink,
            final ClickListener saveButtonClickListener, final ClickListener cancelButtonClickListener,
            final Map<String, Boolean> requiredFields) {
        checkNotNull(saveButtonClickListener);
        checkNotNull(cancelButtonClickListener);
        this.caption = caption;
        this.content = content;
        this.helpLink = helpLink;
        this.saveButtonClickListener = saveButtonClickListener;
        this.cancelButtonClickListener = cancelButtonClickListener;
        this.requiredFields = requiredFields;

        init();
    }

    /**
     * Checks the mandatory fields in the pop-up-window content. If all
     * mandatory fields are filled the save button is enabled. Otherwise the
     * save button is disabled.
     */
    public void checkMandatoryFields() {

        for (final Map.Entry<String, Boolean> entry : requiredFields.entrySet()) {
            if (entry.getValue() == null || entry.getValue().equals(Boolean.FALSE)) {
                saveButton.setEnabled(false);
                return;
            }
        }
        saveButton.setEnabled(true);
    }

    private final void init() {

        if (content instanceof AbstractOrderedLayout) {
            ((AbstractOrderedLayout) content).setSpacing(true);
            ((AbstractOrderedLayout) content).setMargin(true);
        }

        if (null != content) {
            mainLayout.addComponent(content);
        }

        createMandatoryLabel();

        final HorizontalLayout buttonLayout = createActionButtonsLayout();
        mainLayout.addComponent(buttonLayout);
        mainLayout.setComponentAlignment(buttonLayout, Alignment.TOP_CENTER);

        setCaption(caption);
        setContent(mainLayout);
        setResizable(true);
        center();
        setModal(true);
        addStyleName("fontsize");
    }

    private HorizontalLayout createActionButtonsLayout() {

        buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSizeFull();
        buttonsLayout.setSpacing(true);

        createSaveButton();

        createCancelButton();
        buttonsLayout.addStyleName("actionButtonsMargin");

        addHelpLink();

        return buttonsLayout;
    }

    private void createMandatoryLabel() {

        if (existsMandatoryFieldsInWindowContent()) {
            // final Label madatoryLabel = new
            // Label(i18n.get("label.mandatory.field"));
            final Label madatoryLabel = new Label("* Mandatory Field");
            madatoryLabel.addStyleName(SPUIStyleDefinitions.SP_TEXTFIELD_ERROR + " " + ValoTheme.LABEL_TINY);
            mainLayout.addComponent(madatoryLabel);
        }
    }

    private void createCancelButton() {
        cancelButton = SPUIComponentProvider.getButton(SPUIComponentIdProvider.CANCEL_BUTTON, "Cancel", "", "", true,
                FontAwesome.TIMES, SPUIButtonStyleBorderWithIcon.class);
        cancelButton.setSizeUndefined();
        cancelButton.addStyleName("default-color");
        cancelButton.addClickListener(cancelButtonClickListener);

        buttonsLayout.addComponent(cancelButton);
        buttonsLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_LEFT);
        buttonsLayout.setExpandRatio(cancelButton, 1.0F);
    }

    private void createSaveButton() {
        saveButton = SPUIComponentProvider.getButton(SPUIComponentIdProvider.SAVE_BUTTON, "Save", "", "", true,
                FontAwesome.SAVE, SPUIButtonStyleBorderWithIcon.class);
        saveButton.setSizeUndefined();
        saveButton.addStyleName("default-color");
        saveButton.addClickListener(saveButtonClickListener);
        saveButton.setEnabled(!existsMandatoryFieldsInWindowContent());
        buttonsLayout.addComponent(saveButton);
        buttonsLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);
        buttonsLayout.setExpandRatio(saveButton, 1.0F);
    }

    private boolean existsMandatoryFieldsInWindowContent() {

        if (requiredFields != null && requiredFields.size() > 0) {
            return true;
        }
        return false;
    }

    private void addHelpLink() {

        if (StringUtils.isEmpty(helpLink)) {
            return;
        }
        final Link helpLinkComponent = SPUIComponentProvider.getHelpLink(helpLink);
        buttonsLayout.addComponent(helpLinkComponent);
        buttonsLayout.setComponentAlignment(helpLinkComponent, Alignment.MIDDLE_RIGHT);
    }

    public void setSaveButtonEnabled(final boolean enabled) {
        saveButton.setEnabled(enabled);
    }

    public void setCancelButtonEnabled(final boolean enabled) {
        cancelButton.setEnabled(enabled);
    }

    public HorizontalLayout getButtonsLayout() {
        return buttonsLayout;
    }

    public Map<String, Boolean> getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(final Map<String, Boolean> requiredFields) {
        this.requiredFields = requiredFields;
    }

}
