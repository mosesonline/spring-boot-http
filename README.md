# http-service

[![Java CI with Maven](https://github.com/mosesonline/spring-boot-http/actions/workflows/main.yml/badge.svg)](https://github.com/mosesonline/spring-boot-http/actions/workflows/main.yml)

An example service to add multiple modular backends with spring-boot.

The incoming "x-backend" header controls the backend.

## first backend use case

Simple backend with different model as incoming requires an extra mapper and encoding.

## second backend use case

Simple backend with same model as incoming.

## third backend use case

By default disabled backend

## fourth backend use case

Retry backend


TODO

TODO:
- sanitize logging

Targets:
- No Lombok
- SoC separation of concerns
- well seperated backend
- Documented and tested.
