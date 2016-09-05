Ext.define('PH.store.Reports', {
    extend: 'Ext.data.Store',
    model: 'PH.model.Report',
    autoLoad: false,
    alias: 'store.reports',
    cycleId: null,

    proxy: {
        type: 'rest',
        api: {
            read: '/report',
            update: '/report',
            delete: '/report',
            create: '/report'
        },
        reader: {
            type: 'json'
        },
        extraParams: {
            cycleId: '0:0'
        }
    }
});