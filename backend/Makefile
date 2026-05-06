GRADLE := ./gradlew
PROFILE := local

run:
	$(GRADLE) bootRun --args='--spring.profiles.active=$(PROFILE)'

build:
	$(GRADLE) build

format:
	$(GRADLE) spotlessApply

lint:
	$(GRADLE) pmdMain pmdTest

test:
	$(GRADLE) jacocoTestReport

docs:
	$(GRADLE) test --tests code.ModularityTest
	$(GRADLE) javadoc
	$(GRADLE) asciidoctor