# ============================================================
# quarkus-validation-example — Root Makefile
# ============================================================

NETWORK_NAME := payments-network

# ---- Helpers ----
.PHONY: external-network me-happy me-down infra-up infra-down services-up services-down \
        schema kafka-topics logs-kafka \
        test-payments test-frauds lint-payments lint-frauds

external-network:
	@if docker network inspect $(NETWORK_NAME) >/dev/null 2>&1; then \
		echo -e "Docker network \033[32m $(NETWORK_NAME) already exists. \xE2\x9C\x94 \033[0m Continuing..."; \
	else \
		echo "Creating docker \033[32m network $(NETWORK_NAME)... \033[0m"; \
		docker network create $(NETWORK_NAME); \
		echo -e "Docker network\033[32m $(NETWORK_NAME) created successfully. \xE2\x9C\x94 \033[0m"; \
	fi

# ---- Full lifecycle ----
me-happy:
	@${MAKE} external-network
	@${MAKE} infra-up
	@${MAKE} schema
	@${MAKE} kafka-topics
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m Infra, Schema Registry, and Kafka topics are ready."

me-down:
	@${MAKE} infra-down
	@${MAKE} services-down
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m Services deleted"
	@echo "Deleting docker network -> $(NETWORK_NAME)"
	docker network rm $(NETWORK_NAME) || true
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m Network \033[32m $(NETWORK_NAME) \033[0m deleted."
	docker ps

# ---- Schema Registry ----
schema:
	@echo "Registering transaction-validation-request schema..."
	curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
	  --data @./schema-registry/schema/subjects/transaction-validation-request.schema.json \
	  http://localhost:8081/subjects/transaction-validation-request-schema/versions
	@echo ""
	@echo "Registering transaction-validation-response schema..."
	curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
	  --data @./schema-registry/schema/subjects/transaction-validation-response.schema.json \
	  http://localhost:8081/subjects/transaction-validation-response-schema/versions
	@echo -e "\n\033[32m \xE2\x9C\x94 \033[0m Schemas successfully registered in Schema Registry."

# ---- Kafka topics ----
kafka-topics:
	@echo "Creating topics..."
	docker exec kafka kafka-topics --bootstrap-server localhost:9092 \
	  --create --topic transaction-validation-request --partitions 1 --replication-factor 1 --if-not-exists
	docker exec kafka kafka-topics --bootstrap-server localhost:9092 \
	  --create --topic transaction-validation-response --partitions 1 --replication-factor 1 --if-not-exists
	docker exec kafka kafka-configs --bootstrap-server localhost:9092 \
	  --entity-type topics --entity-name transaction-validation-request \
	  --alter --add-config compression.type=snappy
	docker exec kafka kafka-configs --bootstrap-server localhost:9092 \
	  --entity-type topics --entity-name transaction-validation-response \
	  --alter --add-config compression.type=snappy
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m Topics successfully created."

# ---- Infrastructure ----
infra-up:
	@echo "Starting infrastructure services..."
	docker compose -p infrastructure -f docker-compose-infra.yml up -d
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m Infrastructure started."

infra-down:
	@echo "Stopping infrastructure services..."
	docker compose -p infrastructure -f docker-compose-infra.yml down -v --remove-orphans
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m Infrastructure stopped."

# ---- Application services | They can publish but can't consume in docker network ----
services-up:
	@echo "Building and starting ms-payments..."
	docker compose -p services -f docker-compose-services.yml build ms-payments
	docker compose -p services -f docker-compose-services.yml up -d ms-payments
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m ms-payments started."
	@echo "Building and starting ms-frauds..."
	docker compose -p services -f docker-compose-services.yml build ms-frauds
	docker compose -p services -f docker-compose-services.yml up -d ms-frauds
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m ms-frauds started."

services-down:
	@echo "Stopping services..."
	docker rm -f ms-payments ms-frauds 2>/dev/null || true
	docker compose -p services -f docker-compose-services.yml down -v --remove-orphans
	@echo -e "\033[32m \xE2\x9C\x94 ms-payments \033[0m stopped."
	@echo -e "\033[32m \xE2\x9C\x94 ms-frauds \033[0m stopped."