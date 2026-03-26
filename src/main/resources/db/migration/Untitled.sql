CREATE TABLE "user" (
  "id" uuid PRIMARY KEY,
  "first_name" varchar(100) NOT NULL,
  "last_name" varchar(100) NOT NULL,
  "email" varchar(100) UNIQUE NOT NULL,
  "country_id" integer 
);

CREATE TABLE "country" (
  "id" integer PRIMARY KEY,
  "iso_code" varchar(3),
  "name" varchar(40)
);

CREATE TABLE "document_type" (
  "id" integer PRIMARY KEY,
  "country_id" integer,
  "code" varchar(20),
  "name" varchar(100)
);

CREATE TABLE "user_document" (
  "id" integer PRIMARY KEY,
  "user_id" uuid,
  "document_type_id" integer,
  "document_value" varchar(40)
);

CREATE TABLE "account" (
  "id" uuid PRIMARY KEY,
  "user_id" uuid,
  "balance" decimal(19,4),
  "account_number" varchar(16) UNIQUE,
  "currency" integer,
  "status" integer,
  "version" integer,
  "created_at" timestamptz,
  "updated_at" timestamptz
);

CREATE TABLE "routing_number" (
  "id" integer PRIMARY KEY,
  "account_id" uuid,
  "routing_number_type_id" integer,
  "number" varchar(40)
);

CREATE TABLE "routing_number_type" (
  "id" integer PRIMARY KEY,
  "country_id" integer,
  "code" varchar(20),
  "name" varchar(100)
);

CREATE TABLE "currency" (
  "id" integer PRIMARY KEY,
  "currency_name" varchar(3)
);

CREATE TABLE "account_status" (
  "id" integer PRIMARY KEY,
  "status" varchar(40)
);

CREATE TABLE "transaction" (
  "id" uuid PRIMARY KEY,
  "source" uuid,
  "destiny" uuid,
  "source_amount" decimal(19,4),
  "destiny_amount" decimal(19,4),
  "source_currency" integer,
  "destiny_currency" integer,
  "status" integer,
  "created_at" timestamptz
);

CREATE TABLE "transaction_status" (
  "id" integer PRIMARY KEY,
  "status" varchar(40)
);

ALTER TABLE "account" FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "account_status" ADD CONSTRAINT "account_status" FOREIGN KEY ("id") REFERENCES "account" ("status") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "account" ADD CONSTRAINT "transaction_source" FOREIGN KEY ("id") REFERENCES "transaction" ("source") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "account" ADD CONSTRAINT "transaction_destiny" FOREIGN KEY ("id") REFERENCES "transaction" ("destiny") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "transaction_status" ADD CONSTRAINT "transaction_status" FOREIGN KEY ("id") REFERENCES "transaction" ("status") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "currency" ADD CONSTRAINT "account_currency" FOREIGN KEY ("id") REFERENCES "account" ("currency") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "country" ADD CONSTRAINT "user_country" FOREIGN KEY ("id") REFERENCES "user" ("country_id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "user" ADD CONSTRAINT "user_document" FOREIGN KEY ("id") REFERENCES "user_document" ("user_id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "document_type" ADD CONSTRAINT "document_type" FOREIGN KEY ("id") REFERENCES "user_document" ("document_type_id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "country" ADD CONSTRAINT "document_type_country" FOREIGN KEY ("id") REFERENCES "document_type" ("country_id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "routing_number_type" ADD CONSTRAINT "routing_number_country" FOREIGN KEY ("id") REFERENCES "routing_number" ("routing_number_type_id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "account" ADD CONSTRAINT "account_routing_number" FOREIGN KEY ("id") REFERENCES "routing_number" ("account_id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "country" ADD CONSTRAINT "routing_number_type_country" FOREIGN KEY ("id") REFERENCES "routing_number_type" ("country_id") DEFERRABLE INITIALLY IMMEDIATE;
