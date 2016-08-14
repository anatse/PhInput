Ext.define('PH.model.Report', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        'login',
        'city',
        'street',
        'building',
        'pharmNet',
        'pharmacy',
        'agreements',
        'managerName',
        {
            name: 'managerPhone'
            // validators: {
            //     type: 'format',
            //     matcher: /(\d{3})\d{3}\-\d{4}/
            // }
        },
        'tradeRoomPhone',
        {
            name: 'id',
            identifier: true
        }]
});

