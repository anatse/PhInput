Ext.define('PH.model.User', {
    extend: 'Ext.data.Model',

    requires: [
        'Ext.data.reader.Json'
    ],

    idProperty: 'login',
    fields: ['login', 'firstName', 'secondName', 'email', 
        {
            name: 'activated',
            type: 'bool'
        }, {
            name: 'manager',
            type: 'bool'
        }]
});