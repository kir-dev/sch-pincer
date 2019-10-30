#
# Created by Szabó Gergely (Gerviba)
# https://github.com/Gerviba/webschop
#

mkfile_path := $(abspath $(lastword $(MAKEFILE_LIST)))
mkfile_dir := $(dir $(mkfile_path))

.PHONY: all
all: install

install:
	./mvnw clean install

test:
	./mvnw test
	
deploy: docker-volume-create docker-remove docker-build
	@echo
	@echo Done
	@echo

run:

package:
	./mvnw package spring-boot:repackage

docker-build: package
	cp target/schpincer-*.jar docker/schpincer-latest.jar
	docker build --file=docker/Dockerfile --tag=schpincer:latest --rm=true docker/

docker-run:
	docker run -d --name=schpincer --publish=80:80 \
		--volume=$(mkfile_dir)docker/application-docker.properties:/opt/schpincer/application.properties \
		--volume=schpincer-permanent-storage:/permanent/external/ \
		--volume=schpincer-lucene-cache:/tmp/schpincer/search/ \
		--network="host" \
		schpincer:latest

docker-run-test:
	docker run --name=schpincer --publish=8080:8080 \
		--volume=$(mkfile_dir)docker/application-docker.properties:/opt/schpincer/application.properties \
		--volume=$(mkfile_dir)test/external:/permanent/external/ \
		--volume=$(mkfile_dir)docker/temp/schpincer/search:/tmp/schpincer/search/ \
		--network="host" \
		schpincer:latest

docker-stop:
	docker stop schpincer

docker-remove:
	docker rm schpincer || true

docker-volume-create:
	docker volume create --name schpincer-permanent-storage
	docker volume create --name schpincer-lucene-cache

	
