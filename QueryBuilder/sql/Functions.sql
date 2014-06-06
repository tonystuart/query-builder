
connect 'jdbc:derby://localhost:1527/Master';

drop function app.dayName;

create function app.dayName
    ( dateValue date )
    returns varchar( 10 )
    parameter style java
    no sql
    language java
    external name 'com.example.querybuilder.derby.DbDateUtilities.getDayName'
;
