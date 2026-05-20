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
	rm -rf $(RELEASE_DIR) $(RELEASE_DIR)/../$(PROJECT_NAME)-$(RELEASE_VERSION).tar.gz $(RELEASE_DIR)/../$(PROJECT_NAME)-$(RELEASE_VERSION).zip
	mkdir -p $(RELEASE_DIR)
	for module in $(MODULES); do \
		mkdir -p $(RELEASE_DIR)/$$module; \
		cp $$module/target/$$module-$(RELEASE_VERSION).jar $(RELEASE_DIR)/$$module/; \
	done
	tar -czf $(RELEASE_DIR)/../$(PROJECT_NAME)-$(RELEASE_VERSION).tar.gz -C $(RELEASE_DIR) .
	cd $(RELEASE_DIR) && zip -r ../$(PROJECT_NAME)-$(RELEASE_VERSION).zip .

.PHONY: clean
clean: java_clean