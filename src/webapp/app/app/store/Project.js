Ext.define('PH.store.Project', {
    extend: 'Ext.data.Store',
    model: 'PH.model.Project',
    autoLoad: false,
    alias: 'store.projects',
    cycleId: null,

    proxy: {
        type: 'rest',
        api: {
            read: '/project',
            update: '/project',
            delete: '/project',
            create: '/project'
        },
        reader: {
            type: 'json'
        }
    }
});