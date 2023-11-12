# Banking System

## Description

A restful banking system built in hexagonal architecture with CQRS light (with one DB).
the primary use cases are internal money transfer, external money transfer, opening and closing an account, adding and
deleting a beneficiary, retrieving various info on an account (its balance, iban, bic, beneficiaries and transactions).
There is also and identity and access modules who is in charge of authorizations,login and signup features.

## Basic set up

- You need your own openexchangerates api key to replace in the application properties file.
- You need to set up the database there is some sql scripts in the resources packages to help you with that.

## How to launch the app ?

mvn spring-boot:run

## How to run the test ?

Some integration test use test container, so you need to have docker installed on your machine.

mvn test

## Strategic Domain driven design

if you are not familiar with strategic domain driven design let me redirect you toward this article:
https://blog-clement-gombauld.vercel.app/article/6458fa505f21517ea8ef0e81

### Sub Domains and Bounded contexts

The domain is of course a Banking domain who is composed of 4 subdomains.

- Account who is the core domain.
- Identity and access who is a generic subdomain.
- Country who is an external generic subdomain.
- Currency who is also an external generic subdomain.


