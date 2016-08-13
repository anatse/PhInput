Ext.define('PH.model.Menu', {
    extend: 'Ext.data.Model',
    fields: [{
        name: 'name',
        convert: function (newValue) {
            return GLocale.menu[newValue] ? GLocale.menu[newValue] : newValue;
        }
    }, "leaf"]
});

