Ext.define('PH.store.Reports', {
    extend: 'Ext.data.Store',
    model: 'PH.model.Report',
    autoLoad: true,
    alias: 'store.reports',

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
        }
    }
});