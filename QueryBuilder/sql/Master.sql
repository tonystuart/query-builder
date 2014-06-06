
connect 'jdbc:derby://localhost:1527/Master;create=true';

create schema data;
create schema configuration;

drop table configuration.tables;
drop table configuration.schemas;
drop table configuration.connections;

create table configuration.connections
(
    id integer generated always as identity (start with 1, increment by 1) not null primary key,
	connection_name varchar(256) unique,
	url varchar(256) not null,
	user_name varchar(256),
	password varchar(256),
	description varchar(1024),
	last_modified timestamp not null default current timestamp
);

create table configuration.schemas
(
    id integer generated always as identity (start with 1, increment by 1) not null primary key,
    connection_id integer not null references configuration.connections(id) on delete cascade,
	schema_name varchar(256) not null,
	last_modified timestamp not null default current timestamp,
	unique (connection_id, schema_name)
);

create table configuration.tables
(
    id integer generated always as identity (start with 1, increment by 1) not null primary key,
    schema_id integer not null references configuration.schemas(id) on delete cascade,
	generation integer not null,
	source_table_name varchar(256) not null,
	row_count integer,
	column_count integer,
	import_time timestamp,
	last_modified timestamp not null default current timestamp,
	unique (schema_id, source_table_name, generation)
);


