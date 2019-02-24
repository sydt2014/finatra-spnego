# GNU Make 4.1

SHELL = /usr/bin/env bash

SCRIPT = ./script

.PHONY: usage
usage:
	@echo "targets include: usage build run clean all"

.PHONY: wrapper
wrapper:
	@gradle wrapper --gradle-version $(shell cat .gradle-version)

.PHONY: build
build: wrapper
	@./gradlew build

.PHONY: run
run:
	@$(SCRIPT)/run.sh ${ARGS}

.PHONY: clean
clean: wrapper
	@./gradlew clean

.PHONY: all
all: clean build run
