
create or replace function plpgj_call_handler()
	returns language_handler
	as '/home/kocka/source/my/pl-j/csrc//plpgj.so', 'plpgj_call_handler'
	language 'C';

create trusted language 'plj' handler plpgj_call_handler;


