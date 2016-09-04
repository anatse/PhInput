
Ext.define("PH.controller.App", {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'contentPanel',
        selector: 'contentPanel'
    }],

    init: function() {
        this.control ({
            'navigation': {
                selectionchange: this.onNavSelectionChange
            }
        });
    },
    onNavSelectionChange: function(selModel, records) {
        var record = records[0],
            xtype = record.get('module'),
            alias = 'widget.' + xtype;
    
        if (!this.getContentPanel)
            return;
    
        var contentPanel = this.getContentPanel();
        contentPanel.removeAll(true);
        contentPanel.setTitle (null);

        if (xtype) {
            var className = Ext.ClassManager.getNameByAlias(alias);
            if (!className) {
                if (console)
                    console.error ('Not found module: ' + xtype);

                return;
            }

            // load controller class
            var viewClass = Ext.ClassManager.get(className);
            var cmp = new viewClass();
            
            contentPanel.add(cmp);
            cmp.setTitle (PH.utils.CommonUtils.getLocaleString('titles', xtype));
            if (cmp.isXType('grid')) {
                var columns = PH.utils.CommonUtils.createColumnFromModel(GLocale, cmp.getStore())
                cmp.reconfigure(cmp.store, columns);
                PH.utils.CommonUtils.refreshAnyGrid(cmp)
            }
            else {
                var grid = cmp.down('grid')
                var columns = PH.utils.CommonUtils.createColumnFromModel(GLocale, grid.getStore())
                grid.reconfigure(grid.store, columns);
                PH.utils.CommonUtils.refreshAnyGrid(grid)
            }

            if (cmp.floating) {
                cmp.show();
            } 
            else {
                if (this.centerContent)
                    this.centerContent();
            }
        }
    }
});

