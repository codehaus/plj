
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


