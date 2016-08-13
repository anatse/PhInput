Ext.application({
    name: 'PH',
    // automatically create an instance of PH.view.Viewport
    autoCreateViewport: true,
    controllers: [
        'App'
    ],
    models:[
        'Menu'
    ],
    stores:[
        'Menu'
    ]
});
