
.PHONY: all
all: install

install:
	mvn clean install
	
test:
	mvn test
	
deploy:
	