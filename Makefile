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
	cp target/webschop-*.jar docker/webschop-latest.jar
	docker build --file=docker/Dockerfile --tag=webschop:latest --rm=true docker/

docker-run:
	docker run -d --name=webschop --publish=8080:8080 \
		--volume=$(mkfile_dir)docker/application-docker.properties:/opt/webschop/application.properties \
		--volume=webschop-permanent-storage:/permanent/external/ \
		--volume=webschop-lucene-cache:/tmp/webschop/search/ \
		--network="host" \
		webschop:latest

docker-run-test:
	docker run --name=webschop --publish=8080:8080 \
		--volume=$(mkfile_dir)docker/application-docker.properties:/opt/webschop/application.properties \
		--volume=$(mkfile_dir)test/external:/permanent/external/ \
		--volume=$(mkfile_dir)docker/temp/webschop/search:/tmp/webschop/search/ \
		--network="host" \
		webschop:latest

docker-stop:
	docker stop webschop

docker-remove:
	docker rm webschop || true

docker-volume-create:
	docker volume create --name webschop-permanent-storage
	docker volume create --name webschop-lucene-cache

	