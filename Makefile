# GNU Make 4.1

SHELL = /usr/bin/env bash

SCRIPT = ./script

.PHONY: usage
usage:
	@echo "targets include: usage build run clean all"

.PHONY: build
build:
	@gradle build

.PHONY: run
run:
	@$(SCRIPT)/run.sh ${ARGS}

.PHONY: clean
clean:
	@gradle clean

.PHONY: all
all: build run