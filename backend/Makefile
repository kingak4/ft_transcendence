.PHONY: run build format lint test docs
include .env
PROFILE := local

run:
	${GRADLE} bootRun --args='--spring.profiles.active=$(PROFILE)'

build:
	${GRADLE} build

format:
	${GRADLE} spotlessApply

lint:
	${GRADLE} pmdMain pmdTest

test:
	${GRADLE} jacocoTestReport

docs:
	${GRADLE} docs