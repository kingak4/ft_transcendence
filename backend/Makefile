<<<<<<< HEAD
run: build
	gradle bootRun

build:
	gradle build

format:
	gradle checkstyleApply
=======
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
	${GRADLE} test --tests code.ModularityTest
	${GRADLE} javadoc
	${GRADLE} asciidoctor
>>>>>>> main
