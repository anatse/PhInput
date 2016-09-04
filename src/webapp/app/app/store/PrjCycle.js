Ext.define('PH.store.PrjCycle', {
    extend: 'Ext.data.Store',
    model: 'PH.model.PrjCycle',
    autoLoad: false,
    alias: 'store.prjCycles',
    cycleId: null,

    proxy: {
        type: 'rest',
        api: {
            read: '/cycle',
            update: '/cycle',
            delete: '/cycle',
            create: '/cycle'
        },
        reader: {
            type: 'json'
        }
    }
});