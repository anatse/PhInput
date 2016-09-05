
Ext.define("PH.view.project.PrjController", {
    extend: 'Ext.app.ViewController',
    alias: 'controller.projectList',

    onSelectProject: function (grid, selected, eOpts) {
        var cycleGrid = this.lookupReference('cycleGrid');
        Ext.apply(store.getProxy().extraParams, {
            prjId : selected[0].get('id'),
        });
        store.load();
    },
    onSelectCycle: function (grid, selected, eOpts) {
        
//        this.lookupReference('prjCycle')
        
    }
    
});

