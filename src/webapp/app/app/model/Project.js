Ext.define('PH.model.Project', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [{
            name: 'id',
            identifier: true
        },
        'name',
        {
            name: 'startDate',
            type: 'date',
            convert: function (dt) {
                return new Date(dt);
            }
        }
    ]
});

