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