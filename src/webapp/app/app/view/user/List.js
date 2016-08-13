
Ext.define('PH.view.user.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.users',

    requires: [
        'PH.store.Users',
        'PH.view.user.ListController',
        'PH.view.user.Record'
    ],
    controller: 'list',
    store: 'users',
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

        this.buttons = [{
            text:'Sync',
            action:'sync'
        }];

        this.callParent(arguments);
    }
});