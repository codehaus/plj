
create or replace function plpgj_test_lametest1()
returns varchar
as '
	class=org.deadcat_enterprises.Businnes
	method=test
	oneway=false
	instantiation=session
' language 'plj';

create or replace function plpgj_test_lametest2(in int)
returns varchar
as '
	class=org.deadcat_enterprises.Businnes
	method=test
	oneway=false
	instantiation=session
' language 'plj';


create or replace function plpgj_test_jdbc1()
returns varchar
as '
         class=org.deadcat_enterprises.Businnes
         method=jdbcTest
         oneway=false
         instantiation=session
' language 'plj';

create or replace function plpgj_test_lametest3()
returns int
as '
	class=org.deadcat_enterprises.Businnes
	method=testInt0
	oneway=false
	instantiation=session
' language 'plj';

create or replace function plpgj_test_lametest4(in int, in int)
returns int
as '
	class=org.deadcat_enterprises.Businnes
	method=testInt1
	oneway=false
	instantiation=session
' language 'plj';

create or replace function plpgj_test_lametest5(in int)
returns int
as '
	class=org.deadcat_enterprises.Businnes
	method=testInt2
	oneway=false
	instantiation=session
' language 'plj';

create or replace function plpgj_test_lametest6(in varchar)
returns varchar
as '
	class=org.deadcat_enterprises.Businnes
	method=testString0
	oneway=false
	instantiation=session
' language 'plj';

create or replace function plpgj_test_lametest7(in varchar)
returns varchar
as '
        class=org.deadcat_enterprises.Businnes
        method=logTest
        oneway=false
        instantiation=session
' language 'plj';

create or replace function plpgj_test_lametest8()
returns varchar
as '
        class=org.deadcat_enterprises.Businnes
        method=threadedLogTest
        oneway=false
        instantiation=session
' language 'plj';


-- Boolean type tests

create or replace function plpgj_test_boolean1(bool)
returns void
as '
        class=org.deadcat.typetests.BooleanTests
        method=setBoolean
' language 'plj';

create or replace function plpgj_test_boolean2()
returns bool
as '
        class=org.deadcat.typetests.BooleanTests
        method=getBoolean
' language 'plj';

create or replace function plpgj_test_boolean_AND(bool, bool)
returns bool
as '
        class=org.deadcat.typetests.BooleanTests
        method=doAND
' language 'plj';

create or replace function plpgj_test_boolean_OR(bool, bool)
returns bool
as '
        class=org.deadcat.typetests.BooleanTests
        method=doOR
' language 'plj';

create or replace function plpgj_test_boolean_NOT(bool)
returns bool
as '
        class=org.deadcat.typetests.BooleanTests
        method=doNOT
' language 'plj';


-- trigger tests

create table plj_testtable(
	id	int primary key,
	name	varchar
);

create or replace function plpgj_test_lametrigger1fn() returns trigger
as '
	class=org.deadcat_enterprises.PLJTriggers
	method=testTableInsertTrigger
' language 'plj';

create trigger plpgj_test_lametrigger1 before insert on plj_testtable
	for each row
	execute procedure plpgj_test_lametrigger1fn();

create table plj_testable2(
	id	int primary key,
	name	varchar
);

create or replace function plpgj_test_t2_beforeinsert() returns trigger
as
'
	class=org.deadcat_enterprises.PLJTriggers
	method=beforeInsertRowTrigger
' language 'plj';

create trigger plj_testable2_beforeinsert_row before insert on plj_testable2
	for each row
	execute procedure plpgj_test_t2_beforeinsert();

create or replace function plpgj_test_t2_afterinsert() returns trigger
as
'
	class=org.deadcat_enterprises.PLJTriggers
	method=afterInsertRowTrigger
' language 'plj';

create trigger plj_testable2_afterinsert_row after insert on plj_testable2
	for each row
	execute procedure plpgj_test_t2_afterinsert();



-- JDBC tests

create or replace function plpgj_test_jdbctest1()
returns varchar
as '
        class=org.deadcat.jdbctests.JDBCTests
        method=doTest1
' language 'plj';

create sequence plj_prepsta_xmpl_seq;
create table plj_prepsta_xmpl(
	id integer not null default nextval('plj_prepsta_xmpl_seq'),
	t timestamp not null default now(),
	str varchar not null,
	str_maynull varchar
);

create or replace function plpgj_test_jdbc_prep1()
returns void
as '
	class=org.deadcat.jdbctests.JDBCTests
	method=doPreparedTest1
' language 'plj';

create or replace function plpgj_test_jdbc_prep2(int4)
returns void
as '
	class=org.deadcat.jdbctests.JDBCTests
	method=doPreparedQueryTest1
' language 'plj';


-- int4 tests

create function plpgj_test_int4_add(int4, int4)
returns int4 as
'
	class=org.deadcat.typetests.SmallIntTests
	method=add
' language 'plj';

create function plpgj_test_int4_sub(int4, int4)
returns int4 as
'
	class=org.deadcat.typetests.SmallIntTests
	method=sub
' language 'plj';

create function plpgj_test_int4_mul(int4, int4)
returns int4 as
'
	class=org.deadcat.typetests.SmallIntTests
	method=mul
' language 'plj';

-- int8 function

create function plpgj_test_int8_add(int8, int8)
returns int8 as
'
	class=org.deadcat.typetests.BigIntTests
	method=add
' language 'plj';

create function plpgj_test_int8_sub(int8, int8)
returns int8 as
'
	class=org.deadcat.typetests.BigIntTests
	method=sub
' language 'plj';

create function plpgj_test_int8_mul(int8, int8)
returns int8 as
'
	class=org.deadcat.typetests.BigIntTests
	method=mul
' language 'plj';

-- bytea tests

create function plpgj_test_bytea_log(bytea)
returns void as
'
        class=org.deadcat.typetests.ByteaTest
        method=printBytea
' language 'plj';

create function plpgj_test_bytea_returnthesame(bytea)
returns bytea as
'
        class=org.deadcat.typetests.ByteaTest
        method=returnTheSameBytea
' language 'plj';


