
Ext.define('PH.view.user.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.users',

    requires: [
        'PH.store.Users',
        'PH.view.user.ListController',
        'PH.view.user.Record'
    ],
    controller: 'userList',
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
    }, {
        xtype: 'button',
        action: 'sync',
        text: PH.utils.CommonUtils.getLocaleString('button', 'sync')
    }],
    columns: [
        {header: 'Name', dataIndex: 'name', flex:1},
        {header: 'Email', dataIndex: 'email', flex:1}
    ],
    initComponent: function () {
        this.store = Ext.create('PH.store.Users', {
            storeId: 'users'
        });
        this.columns = [
            {header: 'Name', dataIndex: 'name', flex:1},
            {header: 'Email', dataIndex: 'email', flex:1}
        ];

        this.callParent(arguments);
    }
});