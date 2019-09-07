	docker run -d --name=webschop --publish=8080:8080 \
		--volume=$(mkfile_dir)docker/application-docker.properties:/opt/webschop/application.properties \
		--volume=webschop-permanent-storage:/permanent/external/ \
		--volume=webschop-lucene-cache:/tmp/webschop/search/ \
		--network="host" \
		-e SPRING_DATASOURCE_USERNAME=webschop \
		-e SPRING_DATASOURCE_PASSWORD=password \
        -e AUTHSCH_CLIENT-IDENTIFIER=68098300940381627560 \
        -e AUTHSCH_CLIENT-KEY=dX4rtYVhNMA9xIYV4iWcmI3eE8I447mXTfEC5akHnkFatewnCIFpQ9JV7SOzO1MhWRkQrKXVvdve8iH0 \
		webschop:latest