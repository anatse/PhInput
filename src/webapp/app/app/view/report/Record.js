
Ext.define('PH.view.report.Record', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportEdit',
    xtype: 'reportEdit',
    requires: [
        'PH.store.Reports',
        'PH.utils.CommonUtils'
    ],
    store: 'reports',
    defaultType: 'textfield',
    items: [{
        xtype: 'fieldset',
        items: [{
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'city'),
            xtype: 'combobox',
            triggerAction:'all',
            typeAhead:true,
            mode:'remote',
            minChars:4,
            hideTrigger:true,
            store: null,
            displayField: 'name',
            valueField: 'name',
            required: true,
            name: 'city',
            listeners: {
                select: function (combo) {
                    var store = combo.up('form').getForm().findField("street").getStore();
                    Ext.apply(store.getProxy().extraParams, {
                        cityId : combo.getSelectedRecord().get('id'),
                    });
                }
            }
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'street'),
            xtype: 'combobox',
            triggerAction:'all',
            typeAhead:true,
            mode:'remote',
            minChars:4,
            hideTrigger:true,
            store: null,
            displayField: 'name',
            valueField: 'name',
            required: true,
            name: 'street',
            listeners: {
                select: function (combo) {
                    var store = combo.up('form').getForm().findField("building").getStore();
                    Ext.apply(store.getProxy().extraParams, {
                        streetId : combo.getSelectedRecord().get('id'),
                    });
                }
            }
        }, {
            fieldLabel: PH.utils.CommonUtils.getLocaleString ('fields', 'building'),
            xtype: 'combobox',
            triggerAction:'all',
            typeAhead:true,
            mode:'remote',
            minChars: 1,
            store: null,
            hideTrigger:true,
            displayField: 'name',
            valueField: 'name',
            required: true,
            name: 'building'
        }, {
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
    }],

    initComponent: function () {
        this.callParent(arguments);

        var cityStore = Ext.create ('Ext.data.JsonPStore', {
            fields: [
                'id', 'name', 'name', 'type', 'zip',
                'contentType', 'parents'
            ],
            proxy: {
                type: 'jsonp',
                url: 'https://kladr-api.ru/api.php?contentType=city&limit=10',
                reader: {
                    type: 'json',
                    rootProperty: 'result',
                    callbackKey: 'theCallbackFunction'
                }
            }
        });

        this.getForm().findField("city").setStore (cityStore)

        var streetStore = Ext.create ('Ext.data.JsonPStore', {
            fields: [
                'id', 'name', 'name', 'type', 'zip',
                'contentType', 'parents'
            ],
            proxy: {
                type: 'jsonp',
                url: 'https://kladr-api.ru/api.php?contentType=street&limit=10',
                extraParams: {
                    cityId: 'none'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'result',
                    callbackKey: 'theCallbackFunction'
                }
            }
        });

        this.getForm().findField("street").setStore (streetStore)

        var buildingStore = Ext.create ('Ext.data.JsonPStore', {
            fields: [
                'id', 'name', 'name', 'type', 'zip',
                'contentType', 'parents'
            ],
            proxy: {
                type: 'jsonp',
                url: 'https://kladr-api.ru/api.php?contentType=building&limit=10',
                extraParams: {
                    streetId: 'none'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'result',
                    callbackKey: 'theCallbackFunction'
                }
            }
        });

        this.getForm().findField("building").setStore (buildingStore)
    }
});

