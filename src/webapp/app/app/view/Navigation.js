
Ext.define('PH.view.Navigation', {
    extend: 'Ext.tree.Panel',
    xtype: 'navigation',
    title: PH.utils.CommonUtils.getLocaleString('titles', 'navi'),
    rootVisible: false,
    store: 'Menu',
    lines: true,
    useArrows: true,
    idField: 'id',
    stateId : 'navi-tree',
    stateful : true,
    stateEvents: ['selectionchange', 'expand', 'itemexpand', 'collapse', 'itemcollapse'],
    displayField: 'name',

    getState : function () {
        var expandedNodes = [];
        this.getRootNode().eachChild (function (child) {
            if (child.isExpanded() === true) {
                expandedNodes.push(child.getPath());
            }
        });
        
        return {
            expanded: expandedNodes
        };
    },
    applyState : function (state) {
        var that = this;
        this.on ('load', function () {
            Ext.Array.forEach (state.expanded, function (path) {
                that.expandPath (path, null, null, function (success, node) {
                });
            }, that);
        });
    }
});

