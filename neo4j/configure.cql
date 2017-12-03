CREATE INDEX ON :Address(address);
CREATE INDEX ON :Address(icij_id);
CREATE INDEX ON :Address(valid_until);
CREATE INDEX ON :Address(countries);
CREATE INDEX ON :Address(country_code);

CREATE INDEX ON :Officer(name);
CREATE INDEX ON :Officer(address);
CREATE INDEX ON :Officer(icij_id);
CREATE INDEX ON :Officer(valid_until);
CREATE INDEX ON :Officer(countries);
CREATE INDEX ON :Officer(country_code);

CREATE INDEX ON :Entity(name);
CREATE INDEX ON :Entity(original_name);
CREATE INDEX ON :Entity(address);
CREATE INDEX ON :Entity(incorporation_date);
CREATE INDEX ON :Entity(inactivation_date);
CREATE INDEX ON :Entity(struck_off_date);
CREATE INDEX ON :Entity(company_type);
CREATE INDEX ON :Entity(service_provider);
CREATE INDEX ON :Entity(jurisdiction);
CREATE INDEX ON :Entity(status);
CREATE INDEX ON :Entity(sourceID);

CREATE INDEX ON :Intermediary(name);
CREATE INDEX ON :Intermediary(sourceID);
CREATE INDEX ON :Intermediary(internal_id);
CREATE INDEX ON :Intermediary(address);
CREATE INDEX ON :Intermediary(valid_until);
CREATE INDEX ON :Intermediary(country_codes);
CREATE INDEX ON :Intermediary(countries);
CREATE INDEX ON :Intermediary(status);

schema await

CALL apoc.index.addAllNodes('offshore',{
  Officer: ["name","address"],
  Intermediary: ["name","address"],
  Address: ["address","countries"],
  Entity: ["status", "service_provider", "address", "name", "jurisdiction_description", "jurisdiction", "former_name", "company_type"]});
