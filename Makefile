include ./Makefile.os
include ./Makefile.maven

RELEASE_VERSION ?= latest
PROJECT_NAME ?= strimzi-test-connectors
RELEASE_DIR=$(CURDIR)/target/release
MODULES ?= fault-injection-source-connector

.PHONY: all
all: java_install

.PHONY: release
release: release_maven release_package

.PHONY: release_maven
release_maven:
	echo "Update pom versions to $(RELEASE_VERSION)"
	mvn $(MVN_ARGS) versions:set -DnewVersion=$(shell echo $(RELEASE_VERSION) | tr a-z A-Z)
	mvn $(MVN_ARGS) versions:commit

.PHONY: release_package
release_package: java_package
	echo "Creating release archives ..."
	mkdir -p $(RELEASE_DIR)
	for module in $(MODULES); do \
		cp $$module/target/$$module-$(RELEASE_VERSION).jar $(RELEASE_DIR)/; \
	done
	tar -czf $(RELEASE_DIR)/../$(PROJECT_NAME)-$(RELEASE_VERSION).tar.gz -C $(RELEASE_DIR) .
	cd $(RELEASE_DIR) && zip ../$(PROJECT_NAME)-$(RELEASE_VERSION).zip *.jar

.PHONY: clean
clean: java_clean