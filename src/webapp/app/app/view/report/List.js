Ext.define('PH.view.report.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.reports',

    requires: [
        'PH.store.Reports',
        'PH.view.report.ListController',
        'PH.view.report.Record'
    ],
    controller: 'reportList',
    columns: [
        {header: 'Name', dataIndex: 'name', flex:1},
        {header: 'Email', dataIndex: 'email', flex:1}
    ],
    tbar: [{
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