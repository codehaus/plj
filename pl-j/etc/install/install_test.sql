
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
	oneway=false
	instantiation=static
' language 'plj';

create trigger plpgj_test_lametrigger1 before insert on plj_testtable
	for each row
	execute procedure plpgj_test_lametrigger1fn();

-- JDBC tests

create or replace function plpgj_test_jdbctest1()
returns varchar
as '
        class=org.deadcat.jdbctests.JDBCTests
        method=doTest1
        oneway=false
        instantiation=session
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


