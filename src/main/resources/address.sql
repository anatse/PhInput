create class CodeWithName;
create property CodeWithName.code string;
create property CodeWithName.name string;
alter property CodeWithName.code mandatory true;
alter property CodeWithName.name mandatory true;

create class Address;
create property Address.city embedded CodeWithName;
create property Address.street embedded CodeWithName;
create property Address.building embedded CodeWithName;
alter property Address.city mandatory true;
alter property Address.street mandatory true;
alter property Address.building mandatory true;

CREATE INDEX UxPharmacyAddress ON Pharmacy(address) UNIQUE;
insert into PrjCycle(name, visits, startDate, endDate) values ('Цикл-1', [{"@type" : "d", "@class": "VisitReq", "type":"P","num":20}], '2016-07-31', '2016-09-03')
insert into Pharmacy(name, cityName, streetName, buildingName) values ("Аптека 36.6", 'Москва', 'Новоданиловская', '10')
drugs- {"@type":"d","@class":"Drug","name":"Analgin","existence":true,"price":99.99}