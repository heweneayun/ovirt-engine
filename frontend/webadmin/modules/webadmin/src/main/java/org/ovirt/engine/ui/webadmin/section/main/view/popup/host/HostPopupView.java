package org.ovirt.engine.ui.webadmin.section.main.view.popup.host;

import org.ovirt.engine.core.common.businessentities.VDSGroup;
import org.ovirt.engine.core.common.businessentities.storage_pool;
import org.ovirt.engine.core.common.mode.ApplicationMode;
import org.ovirt.engine.core.compat.Event;
import org.ovirt.engine.core.compat.EventArgs;
import org.ovirt.engine.core.compat.IEventListener;
import org.ovirt.engine.core.compat.PropertyChangedEventArgs;
import org.ovirt.engine.ui.common.idhandler.ElementIdHandler;
import org.ovirt.engine.ui.common.idhandler.WithElementId;
import org.ovirt.engine.ui.common.view.popup.AbstractModelBoundPopupView;
import org.ovirt.engine.ui.common.widget.Align;
import org.ovirt.engine.ui.common.widget.HasUiCommandClickHandlers;
import org.ovirt.engine.ui.common.widget.UiCommandButton;
import org.ovirt.engine.ui.common.widget.dialog.SimpleDialogPanel;
import org.ovirt.engine.ui.common.widget.dialog.tab.DialogTab;
import org.ovirt.engine.ui.common.widget.dialog.tab.DialogTabPanel;
import org.ovirt.engine.ui.common.widget.editor.EntityModelCheckBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.EntityModelPasswordBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.EntityModelTextBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.ListModelListBoxEditor;
import org.ovirt.engine.ui.common.widget.renderer.NullSafeRenderer;
import org.ovirt.engine.ui.uicommonweb.models.ApplicationModeHelper;
import org.ovirt.engine.ui.uicommonweb.models.EntityModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostModel;
import org.ovirt.engine.ui.webadmin.ApplicationConstants;
import org.ovirt.engine.ui.webadmin.ApplicationResources;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.HostPopupPresenterWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

public class HostPopupView extends AbstractModelBoundPopupView<HostModel> implements HostPopupPresenterWidget.ViewDef {

    interface Driver extends SimpleBeanEditorDriver<HostModel, HostPopupView> {

        Driver driver = GWT.create(Driver.class);
    }

    interface ViewUiBinder extends UiBinder<SimpleDialogPanel, HostPopupView> {

        ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);
    }

    interface ViewIdHandler extends ElementIdHandler<HostPopupView> {

        ViewIdHandler idHandler = GWT.create(ViewIdHandler.class);
    }

    @UiField
    Style style;

    @UiField
    DialogTabPanel tabPanel;

    @UiField
    @WithElementId
    DialogTab generalTab;

    @UiField
    @WithElementId
    DialogTab powerManagementTab;

    @UiField(provided = true)
    @Path(value = "dataCenter.selectedItem")
    @WithElementId("dataCenter")
    ListModelListBoxEditor<Object> dataCenterEditor;

    @UiField(provided = true)
    @Path(value = "cluster.selectedItem")
    @WithElementId("cluster")
    ListModelListBoxEditor<Object> clusterEditor;

    @UiField
    @Path(value = "name.entity")
    @WithElementId("name")
    EntityModelTextBoxEditor nameEditor;

    @UiField
    @Path(value = "host.entity")
    @WithElementId("host")
    EntityModelTextBoxEditor hostAddressEditor;

    @UiField
    @Path(value = "rootPassword.entity")
    @WithElementId("rootPassword")
    EntityModelPasswordBoxEditor rootPasswordEditor;

    @UiField
    @Path(value = "OverrideIpTables.entity")
    @WithElementId("overrideIpTables")
    EntityModelCheckBoxEditor overrideIpTablesEditor;

    @UiField(provided = true)
    @Path(value = "isPm.entity")
    @WithElementId("isPm")
    EntityModelCheckBoxEditor pmEnabledEditor;

    @UiField
    @Path(value = "managementIp.entity")
    @WithElementId("managementIp")
    EntityModelTextBoxEditor pmAddressEditor;

    @UiField
    @Path(value = "pmUserName.entity")
    @WithElementId("pmUserName")
    EntityModelTextBoxEditor pmUserNameEditor;

    @UiField
    @Path(value = "pmPassword.entity")
    @WithElementId("pmPassword")
    EntityModelPasswordBoxEditor pmPasswordEditor;

    @UiField(provided = true)
    @Path(value = "pmType.selectedItem")
    @WithElementId("pmType")
    ListModelListBoxEditor<Object> pmTypeEditor;

    @UiField
    @Path(value = "pmPort.entity")
    @WithElementId("pmPort")
    EntityModelTextBoxEditor pmPortEditor;

    @UiField
    @Path(value = "pmSlot.entity")
    @WithElementId("pmSlot")
    EntityModelTextBoxEditor pmSlotEditor;

    @UiField
    @Path(value = "pmOptions.entity")
    @WithElementId("pmOptions")
    EntityModelTextBoxEditor pmOptionsEditor;

    @UiField
    @Ignore
    Label pmOptionsExplanationLabel;

    @UiField
    @Path(value = "pmSecure.entity")
    @WithElementId("pmSecure")
    EntityModelCheckBoxEditor pmSecureEditor;

    @UiField
    UiCommandButton testButton;

    @UiField
    @Ignore
    Label testMessage;

    @UiField
    @Ignore
    DialogTab spmTab;

    @UiField()
    @Ignore
    VerticalPanel spmPanel;

    @Inject
    public HostPopupView(EventBus eventBus, ApplicationResources resources, ApplicationConstants constants) {
        super(eventBus, resources);
        initEditors();
        initWidget(ViewUiBinder.uiBinder.createAndBindUi(this));
        ViewIdHandler.idHandler.generateAndSetIds(this);
        localize(constants);

        Driver.driver.initialize(this);
        applyModeCustomizations();
    }

    private void initEditors() {
        // List boxes
        dataCenterEditor = new ListModelListBoxEditor<Object>(new NullSafeRenderer<Object>() {
            @Override
            public String renderNullSafe(Object object) {
                return ((storage_pool) object).getname();
            }
        });

        clusterEditor = new ListModelListBoxEditor<Object>(new NullSafeRenderer<Object>() {
            @Override
            public String renderNullSafe(Object object) {
                return ((VDSGroup) object).getname();
            }
        });

        pmTypeEditor = new ListModelListBoxEditor<Object>(new NullSafeRenderer<Object>() {
            @Override
            protected String renderNullSafe(Object object) {
                return (String) object;
            }
        });

        // Check boxes
        pmEnabledEditor = new EntityModelCheckBoxEditor(Align.RIGHT);
    }

    void localize(ApplicationConstants constants) {
        // General tab
        generalTab.setLabel(constants.hostPopupGeneralTabLabel());
        dataCenterEditor.setLabel(constants.hostPopupDataCenterLabel());
        clusterEditor.setLabel(constants.hostPopupClusterLabel());
        nameEditor.setLabel(constants.hostPopupNameLabel());
        hostAddressEditor.setLabel(constants.hostPopupHostAddressLabel());
        rootPasswordEditor.setLabel(constants.hostPopupRootPasswordLabel());
        overrideIpTablesEditor.setLabel(constants.hostPopupOverrideIpTablesLabel());

        // Power Management tab
        powerManagementTab.setLabel(constants.hostPopupPowerManagementTabLabel());
        pmEnabledEditor.setLabel(constants.hostPopupPmEnabledLabel());
        pmAddressEditor.setLabel(constants.hostPopupPmAddressLabel());
        pmUserNameEditor.setLabel(constants.hostPopupPmUserNameLabel());
        pmPasswordEditor.setLabel(constants.hostPopupPmPasswordLabel());
        pmTypeEditor.setLabel(constants.hostPopupPmTypeLabel());
        pmPortEditor.setLabel(constants.hostPopupPmPortLabel());
        pmSlotEditor.setLabel(constants.hostPopupPmSlotLabel());
        pmOptionsEditor.setLabel(constants.hostPopupPmOptionsLabel());
        pmOptionsExplanationLabel.setText(constants.hostPopupPmOptionsExplanationLabel());
        pmSecureEditor.setLabel(constants.hostPopupPmSecureLabel());
        testButton.setLabel(constants.hostPopupTestButtonLabel());

        // SPM tab
        spmTab.setLabel(constants.spmTestButtonLabel());
    }

    private void applyModeCustomizations() {
        spmTab.setVisible(ApplicationModeHelper.getUiMode() != ApplicationMode.GlusterOnly);
    }

    @Override
    public void setMessage(String message) {
        testMessage.setText(message);
    }

    @Override
    public void edit(final HostModel object) {
        Driver.driver.edit(object);

        // TODO should be handled in a more generic way
        object.getPropertyChangedEvent().addListener(new IEventListener() {
            @Override
            public void eventRaised(Event ev, Object sender, EventArgs args) {
                String propName = ((PropertyChangedEventArgs) args).PropertyName;

                if ("IsGeneralTabValid".equals(propName)) { //$NON-NLS-1$
                    if (object.getIsGeneralTabValid()) {
                        generalTab.markAsValid();
                    } else {
                        generalTab.markAsInvalid(null);
                    }
                } else if ("IsPowerManagementTabValid".equals(propName)) { //$NON-NLS-1$
                    if (object.getIsPowerManagementTabValid()) {
                        powerManagementTab.markAsValid();
                    } else {
                        powerManagementTab.markAsInvalid(null);
                    }
                }
            }
        });

        testButton.setCommand(object.getTestCommand());


        // Create SPM related controls.
        IEventListener spmListener = new IEventListener() {
            @Override
            public void eventRaised(Event ev, Object sender, EventArgs args) {

                createSpmControls(object);
            }
        };

        object.getSpmPriority().getItemsChangedEvent().addListener(spmListener);
        object.getSpmPriority().getSelectedItemChangedEvent().addListener(spmListener);

        createSpmControls(object);
    }

    private void createSpmControls(final HostModel object) {

        spmPanel.clear();

        Iterable items = object.getSpmPriority().getItems();
        if (items == null) {
            return;
        }

        // Recreate SPM related controls.
        for (Object item : items) {

            final EntityModel model = (EntityModel) item;

            RadioButton rb = new RadioButton("spm"); // $//$NON-NLS-1$
            rb.setText(model.getTitle());
            rb.setValue(object.getSpmPriority().getSelectedItem() == model);
            rb.addStyleName(style.radioButton());

            rb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> e) {
                    object.getSpmPriority().setSelectedItem(model);
                }
            });

            spmPanel.add(rb);
        }
    }

    @Override
    public HostModel flush() {
        return Driver.driver.flush();
    }

    @Override
    public void focusInput() {
        nameEditor.setFocus(true);
    }

    @Override
    public HasUiCommandClickHandlers getTestButton() {
        return testButton;
    }

    @Override
    public void showPowerManagement() {
        tabPanel.switchTab(powerManagementTab);
    }


    interface Style extends CssResource {

        String radioButton();
    }
}
