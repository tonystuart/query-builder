
connect 'jdbc:derby://localhost:1527/BirtTest';

create schema classicviews;

create view classicviews.us_customers as select * from classicmodels.customers where country = 'USA';

