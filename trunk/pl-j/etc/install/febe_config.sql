
-- Description: This sql script is for the SPI configuration api and teh febe
-- channel module. The params should work in most environment: the java process
-- resides on the localhost and listens on port 1984, the protocol trace
-- goes to /tmp/febe-trace. Just delete the entry if you want to disable 
-- protocol traces.
-- Author: Laszlo Hornyak
--
-- The schame sqlj must exist.
--

CREATE TABLE sqlj.plj_config (
    config_key character varying NOT NULL PRIMARY KEY,
    config_value character varying
);
 
 
INSERT INTO plj_config VALUES ('febe-mini.host', 'localhost');
INSERT INTO plj_config VALUES ('febe-mini.port', '1984');
INSERT INTO plj_config VALUES ('febe-mini.unix-socket', '/dev/null');
INSERT INTO plj_config VALUES ('febe-mini.connect-timeout', '500');
INSERT INTO plj_config VALUES ('febe-mini.proto-trace', '/tmp/febe-trace');
 

