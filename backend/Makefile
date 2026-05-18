.PHONY: run build format lint test docs
include .env
PROFILE := local

.PHONY: run build format lint test docs

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
	${GRADLE} test --tests code.ModularityTest
	${GRADLE} javadoc
	${GRADLE} asciidoctor
