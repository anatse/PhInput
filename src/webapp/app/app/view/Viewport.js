
Ext.define('PH.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: [
        'Ext.layout.container.Border',
        'PH.utils.CommonUtils',
        'PH.view.Navigation',
        'PH.view.ContentPanel',
        'PH.view.user.List',
        'Ext.tree.*'
    ],
    layout: 'border',
    items: [{
        region: 'north',
        xtype: 'toolbar',
        items: [{
            xtype: 'button',
            text: 'Menu Button'
        }, {
            xtype: 'combo',
            text: 'Right choise'
        }, '->', {
            xtype: 'splitbutton',
            text : 'Split Button'
        }]
    }, {
        region: 'west',
        collapsible: true,
        xtype: 'navigation',
        title: 'Navigation',
        width: 250,
        split: true,
        margin: '0 5 5 5'
    }, {
        region: 'center',
        xtype: 'contentPanel'
    }]
});