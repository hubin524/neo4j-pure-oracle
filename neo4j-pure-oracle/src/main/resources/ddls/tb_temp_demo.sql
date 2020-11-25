create table TB_TEMP_DEMO
(
  demo_id        NUMBER not null,
  demo_name      VARCHAR2(16) not null,
  age       NUMBER,
  birthdate DATE
);
-- Create/Recreate primary, unique and foreign key constraints
alter table TB_TEMP_DEMO
  add constraint DEMO_PK_ID primary key (demo_id)
  ;
alter table TB_TEMP_DEMO
  add constraint DEMO_UN_NAME unique (demo_name)
  ;




  create sequence SEQ_TEMP_DEMO;