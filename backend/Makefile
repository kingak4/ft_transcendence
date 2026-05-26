.PHONY: run build format test docs
include ./.env
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