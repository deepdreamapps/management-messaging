CREATE SCHEMA IF NOT EXISTS management ;

CREATE TABLE IF NOT EXISTS management.reminder_email 
(
	id bigint generated by default as identity,
	event_type varchar(100) ,
	event_id bigint ,
	instant  timestamp with time zone ,
	subject text,
	sender varchar(100) ,
	recipient varchar(100) ,
	content text ,
	sent boolean ,
	primary key (id)
) ;

ALTER SEQUENCE management.reminder_email_id_seq RESTART WITH 1000000000 ;

CREATE INDEX reminder_email__sent ON management.reminder_email (sent) ;
CREATE INDEX reminder_email__event_type_event_id ON management.reminder_email (event_type, event_id) ;

SET TIMEZONE TO 'Africa/Douala' ;


