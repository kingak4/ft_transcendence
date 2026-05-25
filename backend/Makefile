.PHONY: run build format lint test docs
include ./../.env
include ./env
PROFILE := local

run:
	${GRADLE} bootRun --args='--spring.profiles.active=$(PROFILE)'

build:
	${GRADLE} build

format:
	${GRADLE} spotlessApply

test:
	${GRADLE} jacocoTestReport

docs:
	${GRADLE} docs