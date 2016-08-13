
Ext.define("PH.view.user.ListController", {
    extend: 'Ext.app.ViewController',
    alias: 'controller.list',
    
    control: {
        '#': {
            itemdblclick: 'editUser'
        },
        button: {
            click: 'syncUser'
        }
    },
    editUser: function(grid, record) {
        PH.utils.CommonUtils.showEditDialog ('userEdit', record, function(win) {
            var values = win.down('form').getValues();
            record.set(values);
            grid.getStore().sync();
            win.close();
        }, this);
    },
    updateUser: function(button) {
        var win = button.up('window'),
                form = win.down('form'),
                record = form.getRecord(),
                values = form.getValues();

        record.set(values);
        win.close();
    },
    syncUser: function(button) {
        this.getView().getStore().sync();
    }
});

