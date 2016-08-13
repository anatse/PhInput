
Ext.define("PH.view.report.ListController", {
    extend: 'Ext.app.ViewController',
    alias: 'controller.reportList',
    
    control: {
        '#': {
            itemdblclick: 'editReport'
        },
        'button[action=add]': {
            click: 'createReport'
        },
        'button[action=refresh]': {
            click: 'refreshReport'
        },
        'button[action=delete]': {
            click: 'deleteReport'
        },
        'button[action=sync]': {
            click: 'sync'
        }
    },
    editReport: function(grid, record) {
        PH.utils.CommonUtils.showEditDialog ('editReport', record, function(win) {
            var values = win.down('form').getValues();
            record.set(values);
            grid.getStore().sync();
            win.close();
        }, this);
    },
    createReport: function(button) {
        var self = this;
        PH.utils.CommonUtils.showEditDialog ('reportEdit', null, function(win) {
            var values = win.down('form').getValues();
            var record = self.getView().getStore().add(values);
            // special fix for ExtJS without this flag store noy send new added record to server
            record[0].phantom = true;

            self.getView().getStore().sync({
                success: function(batch, opts) {
                    win.close();
                },
                failure: function (batch, opts) {
                }
            })
        }, this, true);
    },
    deleteReport: function (button) {

    },
    sync: function(button) {
        this.getView().getStore().sync();
    }
});

