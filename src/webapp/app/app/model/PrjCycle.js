Ext.define('PH.model.PrjCycle', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [{
        name: 'id',
        identifier: true
    },
        'name', {
            name: 'startDate',
            type: 'date',
            convert: function (dt) {return new Date(dt);}
        }, {
            name: 'endDate',
            type: 'date',
            convert: function (dt) {return new Date(dt);}
        }, {
            name: 'visitPharm',
            convert: function (v, record) {
                for (visit in record.visits) {
                    if (visit.typeName === 'P') {
                        return visit.num;
                    }
                }
            }
        }, {
            name: 'visitDoctor',
            convert: function (v, record) {
                for (visit in record.visits) {
                    if (visit.typeName === 'D') {
                        return visit.num;
                    }
                }
            }
        }
    ]
});

