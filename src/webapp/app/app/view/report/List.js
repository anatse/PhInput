Ext.define('PH.view.report.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.reports',
    reference: 'reportGrid',
    requires: [
        'PH.store.Reports',
        'PH.store.Project',
        'PH.view.report.ListController',
        'PH.view.report.Record'
    ],
    controller: 'reportList',
    columns: [
        {header: 'Name', dataIndex: 'name', flex:1},
        {header: 'Email', dataIndex: 'email', flex:1}
    ],
    tbar: [{
        xtype: 'label',
        text: PH.utils.CommonUtils.getLocaleString('select', 'project')
    }, {
        xtype: 'combo',
        name: 'project',
        store: Ext.create ('PH.store.Project'),
        editable: false,
        displayField: 'name',
        valueField: 'id',
        listeners: {
            select: 'selectProject'
        },
        reference: 'project'
    }, {
        xtype: 'label',
        text: PH.utils.CommonUtils.getLocaleString('select', 'cycle')
    }, {
        xtype: 'combo',
        name: 'cycle',
        store: Ext.create ('PH.store.PrjCycle'),
        editable: false,
        displayField: 'name',
        valueField: 'id',
        listeners: {
            select: 'selectPrjCycle'
        },
        reference: 'prjCycle'
    }, {
        xtype: 'button',
        action: 'refresh',
        text: PH.utils.CommonUtils.getLocaleString('button', 'refresh')
    }, {
        xtype: 'button',
        action: 'add',
        text: PH.utils.CommonUtils.getLocaleString('button', 'add')
    }, {
        xtype: 'button',
        action: 'delete',
        text: PH.utils.CommonUtils.getLocaleString('button', 'delete')
    }],
    initComponent: function () {
        this.store = Ext.create('PH.store.Reports', {
            storeId: 'reports'
        });
        this.columns = [
            {header: 'Name', dataIndex: 'name', flex:1},
            {header: 'Email', dataIndex: 'email', flex:1}
        ];

        this.callParent(arguments);
    }
});