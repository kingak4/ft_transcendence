GRADLE := gradlew
PROFILE := local

run: build
	$(GRADLE) bootRun --args='--spring.profiles.active=$(PROFILE)'

build:
	$(GRADLE) build

format:
	$(GRADLE) checkstyleApply

lint:
	$(GRADLE) pmdMain pmdTest