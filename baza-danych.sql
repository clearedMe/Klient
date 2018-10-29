CREATE TABLE "CUSTOMERS" (
  "ID" serial NOT NULL,
  "NAME" character varying(32) NOT NULL,
  "SURNAME" character varying(32) NOT NULL,
  "AGE" integer NULL
);

CREATE TABLE "CONTACTS" (
  "ID" serial NOT NULL,
  "ID_CUSTOMER" integer NOT NULL,
  "TYPE" integer NOT NULL,
  "CONTACT" character varying(128) NOT NULL
);