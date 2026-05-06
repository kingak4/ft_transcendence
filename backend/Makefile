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