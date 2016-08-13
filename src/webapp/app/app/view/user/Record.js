
Ext.define('PH.view.user.Record', {
    extend: 'Ext.form.Panel',
    alias: 'widget.userEdit',
    xtype: 'userEdit',
    requires: [
        'PH.store.Users',
        'PH.utils.CommonUtils'
    ],
    store: 'users',
    defaultType: 'textfield',
    items: [{
        xtype: 'fieldset',
        items: [{
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'login'),
            xtype: 'textfield',
            name: 'login'
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'email'),
            xtype: 'textfield',
            name: 'email'
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'firstName'),
            xtype: 'textfield',
            name: 'firstName'
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'secondName'),
            xtype: 'textfield',
            name: 'secondName'
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'manager'),
            xtype: 'checkbox',
            name: 'manager'
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'activated'),
            xtype: 'checkbox',
            name: 'activated'
        }]
    }]
});

