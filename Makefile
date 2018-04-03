#
# Created by Szabó Gergely (Gerviba)
# https://github.com/Gerviba/webschop
#

.PHONY: all
all: install

install:
	mvn clean install
	
test:
	mvn test
	
int-test:
	
	
deploy:
	
	
run:
	