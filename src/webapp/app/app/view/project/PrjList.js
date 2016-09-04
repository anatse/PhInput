Ext.define('PH.view.project.PrjList', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.projects',
    reference: 'reportGrid',
    requires: [
        'Ext.grid.Panel',
        'Ext.layout.container.Border',
        'PH.store.Project',
        'PH.view.project.PrjController',
        'PH.view.project.PrjRecord',
        'PH.view.project.CycleRecord'
    ],
    layout: 'border',
    items: [{
        region: 'center',
        xtype: 'grid',
        store: Ext.create('PH.store.Project'),
        reference: 'projectGrid',
        tbar: [{
            xtype: 'button',
            text: PH.utils.CommonUtils.getLocaleString('button', 'add')
        }],
        listeners: {
            selectionchange: 'onSelectProject'
        }
    }, {
        region: 'south',
        xtype: 'grid',
        height: 300,
        store: Ext.create('PH.store.PrjCycle'),
        reference: 'cycleGrid',
        columns: [{
            header: PH.utils.CommonUtils.getLocaleString('fields', 'name'),
            dataIndex: 'name',
            flex:1
        }, {
            header: PH.utils.CommonUtils.getLocaleString('fields', 'startDate'),
            dataIndex: 'startDate',
            flex:1
        }],
        tbar: [{
            xtype: 'button',
            text: PH.utils.CommonUtils.getLocaleString('button', 'add')
        }]
    }]
});