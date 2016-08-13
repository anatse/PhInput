
Ext.define ('PH.utils.CommonUtils', {
    requires: [
        'Ext.*',
        'Ext.LoadMask'
    ],
    
    singleton: true,
    
    getLocaleString: function (group, value) {
        var grp = GLocale[group] ? GLocale[group] : group;
        if (grp) {
            return grp[value] ? grp[value] : grp + '.' + value;
        }

        return group + '.' + value;
    },
    
    /**
     * Function refreshes any grid and after restores current selection based
     * on value of the key field 
     * @param grid - grid to be refreshed
     * @param params - additional parameters which use to reload store
     * @param idFieldName - key field name
     * @param selectedItemObj
     * @param callbackFunc
     * @param scope
     * @param disableSelect
     */
    refreshAnyGrid: function (grid, params, idFieldName, selectedItemObj, callbackFunc, scope, disableSelect) {
        // get current selection
        var selectedItem = selectedItemObj;
        if (!selectedItem)
            selectedItem = grid.getSelectionModel().getLastSelected();

        grid.getSelectionModel().deselectAll();
        grid.getStore().sorters.clear();

        grid.getStore().load({ 
            params: params,
            async: false,
            callback: function (records, operation, success) {
                if (disableSelect !== true) {
                    if (selectedItem && idFieldName) {
                        // restore previously selected item
                        var record = grid.getStore().findRecord (idFieldName, selectedItem.get(idFieldName));
                        if (record)
                            grid.getSelectionModel().select (record);
                        else {
                            if (!isNaN(selectedItem.index) && records && records.length >= selectedItem.index) 
                                grid.getSelectionModel().select (records[selectedItem.index]);
                            else if (records && records.length > 0)
                                    grid.getSelectionModel().select (records[0]);
                        }
                    }
                    else {
                        // select first record
                        if (records && records.length > 0)
                            grid.getSelectionModel().select (records[0]);
                    }
                }

                if (callbackFunc)
                    callbackFunc.call (scope ? scope : this, records, operation, success);
            },
            exception: function (proxy, response, operation) {
                if (operation) {
                    Ext.Msg.alert('Error', operation.error);
                }
                else {
                    Ext.Msg.alert ('Error', 'Proxy error');
                }
            }
        });
    },

    selectFirstElement: function( grid ){
        try{
            grid.getSelectionModel().select (0);
        }
        catch (e){};
    },

    statusRenderer: function (value) {
        return value;
    },

    showEditDialog: function (dialog, record, callback, scope, isNew) {
        var win = Ext.create('Ext.window.Window', {
            title: PH.utils.CommonUtils.getLocaleString ('titles', dialog),
            layout: 'fit',
            modal: true,
            items: {
                xtype: dialog
            },
            buttons: [{
                text: PH.utils.CommonUtils.getLocaleString ('button', 'save'),
                listeners: {
                    click: function () {
                        callback (win);
                    }
                }
            }, {
                text: PH.utils.CommonUtils.getLocaleString ('button', 'cancel'),
                listeners: {
                    click: function () {
                        win.close();
                    }
                }
            }]
        });

        // load record data
        if (!isNew)
            win.down('form').loadRecord(record);
        else {
            var pass = win.down('form').getForm().findField("password");
            if (pass)
                pass.hidden = false;
        }

        win.on ('destroy', callback, scope);
        win.show();
    },

    createColumnFromModel: function (res, model, defaultWidth, maxCols, wordWrap) {
        if (!defaultWidth)
            defaultWidth = 150;

        var columns = [];
        if (model.model)
            model = model.model;

        Ext.each (model.getFields(), function(field) {
            if (maxCols && columns.length === maxCols)
                return true;

            var column = {
                text: res.fields[field.name] ? res.fields[field.name] : field.name,
                width: defaultWidth,
                dataIndex: field.name,
                stateId: field.name
            };

            if (field.type.type === 'date') {
                column.renderer = Ext.util.Format.dateRenderer('d.m.Y');
            }
            else if (wordWrap) {
                column.renderer = function (val) {
                    return '<div style="white-space:normal !important;">'+ val +'</div>';
                };
            }

            columns.push (column);
        });

        return columns;
    }
});