
drop schema sqlj;

create schema sqlj;



create table sqlj.plj_classes(
	jar		varchar(255) default null,
	fqn		varchar(255) not null primary key,
	classcode	bytea not null,
	created		timestamp not null default now(),
	lmodified	timestamp,
	cuser		varchar not null default session_user,
	lmod		varchar
);

COMMENT ON TABLE sqlj.plj_classes is 
'
Used by the default JDBC classloader to store classes.
Please never manipulate this table directly! Use standard based methods!

';


create index plj_classes_jar_idx on sqlj.plj_classes(jar);
create index plj_classes_jar_fqn on sqlj.plj_classes(fqn);


create or replace function sqlj.install_jar(varchar, varchar, int4) returns void as '
	class=#privileged-class
	method=install_jar
' language 'plj' security definer;

create or replace function sqlj.replace_jar(varchar, varchar) returns void as
'
	class=#privileged-class
	method=replace_jar
' language 'plj' security definer;

create or replace function sqlj.remove_jar(varchar, int4) returns void as
'
	class=#privileged-class
	method=remove_jar
' language 'plj' security definer;

create or replace function sqlj.alter_java_path(varchar, varchar) returns void as
'
	class=#privileged-class
	method=alter_java_path
' language 'plj' security definer;



