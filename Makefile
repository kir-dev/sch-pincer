#
# Created by Szabó Gergely (Gerviba)
# https://github.com/Gerviba/webschop
#

mkfile_path := $(abspath $(lastword $(MAKEFILE_LIST)))
mkfile_dir := $(dir $(mkfile_path))
version := $(shell cat $(mkfile_dir)pom.xml | grep '<version>' | head -2 | tail -1 | cut -d \> -f 2 | cut -d \< -f 1)

.PHONY: all
all: install

install:
	./mvnw clean install

test:
	./mvnw test

version:
	@echo $(version)

run:

package:
	./mvnw clean package spring-boot:repackage

docker-build: package
	cp target/schpincer.jar docker/schpincer-latest.jar
	docker build --file=docker/Dockerfile --tag=schpincer:latest --rm=true .

deploy: docker-build
	docker tag schpincer registry.k8s.sch.bme.hu/schpincer/schpincer:$(version)
	docker tag schpincer registry.k8s.sch.bme.hu/schpincer/schpincer:latest
	docker push registry.k8s.sch.bme.hu/schpincer/schpincer:$(version)
	docker push registry.k8s.sch.bme.hu/schpincer/schpincer:latest
	@echo
	@echo VERSION: $(version) | STATUS: DONE
	@echo


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

new-publish:
	docker build -t schpincer/schpincer -f docker/standalone-Dockerfile .
	docker push schpincer/schpincer
