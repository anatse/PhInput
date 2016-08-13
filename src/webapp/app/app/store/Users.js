Ext.define('PH.store.Users', {
    extend: 'Ext.data.Store',
    model: 'PH.model.User',
    autoLoad: true,
    alias: 'store.users',

    proxy: {
        type: 'rest',
        api: {
            read: '/user',
            update: '/user',
            delete: '/user',
            create: '/user/register'
        },
        reader: {
            type: 'json'
        }
    }
});