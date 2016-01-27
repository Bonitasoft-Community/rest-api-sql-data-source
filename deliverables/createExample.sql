DROP TABLE  if exists client;

CREATE TABLE client
(
  client_id uuid NOT NULL,
  first_name character varying(50),
  last_name character varying(50),
  city character varying(30),
  country character varying(30),
  CONSTRAINT client_pk PRIMARY KEY (client_id)
);

-- tip: use https://www.uuidgenerator.net/

insert into client ( client_id,first_name,last_name,  city, country) values ('894184d6-0930-11e5-a6c0-1697f925ec7b','William','Jobs','Grenoble','France');

insert into client ( client_id,first_name,last_name,  city, country) values ('89418c24-0930-11e5-a6c0-1697f925ec7b','Walter','Bates','Paris','France');

insert into client ( client_id,first_name,last_name,  city, country) values ('89418d64-0930-11e5-a6c0-1697f925ec7b','Helen','Kelly','San Francisco','USA');

insert into client ( client_id,first_name,last_name,  city, country) values ('89418f26-0930-11e5-a6c0-1697f925ec7b','Daniela','Angelo','Rio de Janeiro', 'Brazil');





