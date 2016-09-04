Ext.define('PH.view.project.PrjRecord', {
    extend: 'Ext.form.Panel',
    alias: 'widget.projectEdit',
    xtype: 'projectEdit',
    requires: [
        'PH.store.Reports',
        'PH.utils.CommonUtils'
    ],
    store: 'reports',
    defaultType: 'textfield',
    items: [{
        xtype: 'fieldset',
        items: [{
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'pharmNet'),
            xtype: 'textfield',
            name: 'pharmNet'
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'pharmacy'),
            xtype: 'textfield',
            name: 'pharmacy'
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'agreements'),
            xtype: 'textfield',
            name: 'agreements'
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'managerName'),
            xtype: 'textfield',
            name: 'managerName'
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'managerPhone'),
            xtype: 'textfield',
            name: 'managerPhone'
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'tradeRoomPhone'),
            xtype: 'textfield',
            name: 'tradeRoomPhone'
        }]
    }]
});

