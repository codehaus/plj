
create or replace function plpgj_test_lametest1()
returns varchar
as '
	class=org.deadcat_enterprises.Businnes
	method=test
	oneway=false
	instantiation=session
' language 'pl/pgj';

create or replace function plpgj_test_lametest2(in int)
returns varchar
as '
	class=org.deadcat_enterprises.Businnes
	method=test
	oneway=false
	instantiation=session
' language 'pl/pgj';


create or replace function plpgj_test_jdbc1()
returns varchar
as '
         class=org.deadcat_enterprises.Businnes
         method=jdbcTest
         oneway=false
         instantiation=session
' language 'pl/pgj';

