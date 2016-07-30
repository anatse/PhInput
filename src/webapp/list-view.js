Ext.require([
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.panel.*'
]);
Ext.onReady(function(){
    Ext.define('ImageModel', {
        extend: 'Ext.data.Model',
        fields: ['name', 'url', {name:'size', type: 'float'}, {name:'lastmod', type:'date', dateFormat:'timestamp'}]
    });
    
    var store = Ext.create('Ext.data.JsonStore', {
        model: 'ImageModel',
        proxy: {
            type: 'ajax',
            url: 'get-images.php',
            reader: {
                type: 'json',
                rootProperty: 'images'
            }
        }
    });
    store.load();
    
    var comboStore = Ext.create ('Ext.data.JsonPStore', {
       fields: [
           'id', 'fullName', 'name', 'type', 'zip', 
           'contentType', 'parents'
       ],
       proxy: {
            type: 'jsonp',
            url: 'https://kladr-api.ru/api.php?oneString=true&withParent=true&limit=10',
            reader: {
                type: 'json',
                rootProperty: 'result',
                callbackKey: 'theCallbackFunction'
            }
        }
    });

    var listView = Ext.create('Ext.grid.Panel', {
        width: '100%',
        height: 400,
        collapsible:true,
        title:'Simple 1 ListView <i>(0 items selected)</i>',
        renderTo: Ext.getBody(),

        store: store,
        multiSelect: true,
        selModel: 'cellmodel',
        viewConfig: {
            emptyText: 'No images to display'
        },
        plugins: {
            ptype: 'cellediting',
            clicksToEdit: 1
        },

        columns: [{
            text: 'File',
            flex: (Ext.themeName === 'neptune-touch' || Ext.themeName === 'crisp') ? 47: 50,
            editor: {
                xtype: 'combobox',
                triggerAction:'all',
                typeAhead:true,
                mode:'remote',
                minChars:4,
                hideTrigger:true,
                store: comboStore,
                displayField: 'fullName',
                valueField: 'fullName',
                required: true,
                listeners: {
                    select: function (combo, record) {
                        console.log (record.get('name'));
                        console.log (record.get('parents'));
                    }
                }
            },
            dataIndex: 'name'
        },{
            text: 'Last Modified',
            xtype: 'datecolumn',
            format: 'd.m.Y',
            editor: {
                xtype: 'datefield',
                format: 'd.m.Y',
                altFormats: 'd,m,Y|d/m/Y'
            },
            flex: 35,
            dataIndex: 'lastmod'
        },{
            text: 'Size',
            dataIndex: 'size',
            tpl: '{size:fileSize}',
            align: 'right',
            editor: 'numberfield',
            flex: (Ext.themeName === 'neptune-touch' || Ext.themeName === 'crisp') ? 18: 15,
            cls: 'listview-filesize'
        }]
    });

    // little bit of feedback
    listView.on('selectionchange', function(view, nodes){
        var len = nodes.length,
            suffix = len === 1 ? '' : 's',
            str = 'Simple ListView <i>({0} item{1} selected)</i>';
           
        comboStore.load ({
            params : {
                query : nodes[0].data.name
            }
        });

        listView.setTitle(Ext.String.format(str, len, suffix));
    });
});
