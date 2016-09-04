
Ext.define("PH.view.user.ListController", {
    extend: 'Ext.app.ViewController',
    alias: 'controller.userList',

    control: {
        '#': {
            itemdblclick: 'editUser'
        },
        'button[action=add]': {
            click: 'createUser'
        },
        'button[action=refresh]': {
            click: 'refreshUsers'
        },
        'button[action=delete]': {
            click: 'deleteUser'
        },
        'button[action=sync]': {
            click: 'sync'
        }
    },
    editUser: function(grid, record) {
        PH.utils.CommonUtils.showEditDialog ('userEdit', record, function(win) {
            var values = win.down('form').getValues();
            record.set(values);
            grid.getStore().sync({
                success: function(batch, opts) {
                    win.hide();
                },
                failure: function (batch, opts) {
                    grid.getStore().load();

                    if (batch.hasException) {
                        for (var i in batch.exceptions) {
                            Ext.Msg.alert('Failure', batch.exceptions[i].getError());
                        }
                    }
                }
            });
            win.hide();
        }, this);
    },
    createUser: function() {
        var self = this;
        PH.utils.CommonUtils.showEditDialog ('userEdit', null, function(win) {
            var values = win.down('form').getValues();
            var record = self.getView().getStore().add(values);
            // special fix for ExtJS without this flag store noy send new added record to server
            record[0].phantom = true;
            // self.getView().getStore().commitChanges();

            self.getView().getStore().sync({
                success: function(batch, opts) {
                    win.hide();
                },
                failure: function (batch, opts) {
                    grid.getStore().load();

                    if (batch.hasException) {
                        for (var i in batch.exceptions) {
                            Ext.Msg.alert('Failure', batch.exceptions[i].getError());
                        }
                    }
                }
            })
        }, this, true);
    },

    deleteUser: function(button) {
        // var win = button.up('window'),
        //         form = win.down('form'),
        //         record = form.getRecord(),
        //         values = form.getValues();
        //
        // record.set(values);
        // win.close();
    },
    refreshUsers: function(button) {
        this.getView().getStore().load();
    },
    sync: function(button) {
        this.getView().getStore().sync();
    }
});

