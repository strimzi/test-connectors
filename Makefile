include ./Makefile.os
include ./Makefile.maven

RELEASE_VERSION ?= latest

.PHONY: all
all: java_install

.PHONY: release
release: release_maven java_package

.PHONY: release_maven
release_maven:
	echo "Update pom versions to $(RELEASE_VERSION)"
	mvn $(MVN_ARGS) versions:set -DnewVersion=$(shell echo $(RELEASE_VERSION) | tr a-z A-Z)
	mvn $(MVN_ARGS) versions:commit

.PHONY: clean
clean: java_clean