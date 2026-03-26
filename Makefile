# ============================================================
# quarkus-validation-example — Root Makefile
# ============================================================

NETWORK_NAME := payments-network

# ---- Helpers ----
.PHONY: success me-happy me-down infra-up infra-down services-up services-down \
        schema kafka-topics logs-kafka \
        test-payments test-frauds lint-payments lint-frauds

success:
	@if docker network inspect $(NETWORK_NAME) >/dev/null 2>&1; then \
		echo -e "Docker network \033[32m $(NETWORK_NAME) already exists. \xE2\x9C\x94 \033[0m Continuing..."; \
	else \
		echo "Creating docker \033[32m network $(NETWORK_NAME)... \033[0m"; \
		docker network create $(NETWORK_NAME); \
		echo -e "Docker network\033[32m $(NETWORK_NAME) created successfully. \xE2\x9C\x94 \033[0m"; \
	fi

# ---- Full lifecycle ----
me-happy:
	@${MAKE} success
	@${MAKE} infra-up
	@${MAKE} schema
	@${MAKE} kafka-topics
#	@${MAKE} services-up
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m Services started. Waiting 5 seconds..."

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

# ---- Application services ----
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

# ---- Tests ----
test-payments:
	@echo "Running ms-payments tests..."
	cd ms-payments && mvn test
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m ms-payments tests completed."

test-frauds:
	@echo "Running ms-frauds tests..."
	cd ms-frauds && mvn test
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m ms-frauds tests completed."

test-all:
	@${MAKE} test-payments
	@${MAKE} test-frauds
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m All tests completed."

# ---- Linting ----
lint-payments:
	@echo "Running Checkstyle on ms-payments..."
	cd ms-payments && mvn checkstyle:check
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m ms-payments lint passed."

lint-frauds:
	@echo "Running Checkstyle on ms-frauds..."
	cd ms-frauds && mvn checkstyle:check
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m ms-frauds lint passed."

lint-all:
	@${MAKE} lint-payments
	@${MAKE} lint-frauds
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m All lint checks passed."

# ---- Logs ----
logs-kafka:
	docker compose -f docker-compose-infra.yml logs -f kafka

logs-payments:
	docker compose -p services -f docker-compose-services.yml logs -f ms-payments

logs-frauds:
	docker compose -p services -f docker-compose-services.yml logs -f ms-frauds

# ---- Kafka diagnostics ----
kafka-check-offsets:
	@echo "=== Consumer group offsets for ms-frauds ==="
	docker exec kafka kafka-consumer-groups \
	  --bootstrap-server localhost:9092 \
	  --describe --group ms-frauds-consumer-group

kafka-check-messages:
	@echo "=== Messages in transaction-validation-request ==="
	docker exec kafka kafka-run-class kafka.tools.GetOffsetShell \
	  --broker-list localhost:9092 \
	  --topic transaction-validation-request

kafka-reset-offsets:
	@echo "=== Resetting consumer group offsets to earliest ==="
	docker exec kafka kafka-consumer-groups \
	  --bootstrap-server localhost:9092 \
	  --group ms-frauds-consumer-group \
	  --topic transaction-validation-request \
	  --reset-offsets --to-earliest --execute

kafka-purge:
	@echo "=== Deleting and recreating topics (clean slate) ==="
	docker exec kafka kafka-topics --bootstrap-server localhost:9092 --delete --topic transaction-validation-request 2>/dev/null || true
	docker exec kafka kafka-topics --bootstrap-server localhost:9092 --delete --topic transaction-validation-response 2>/dev/null || true
	@sleep 3
	@${MAKE} kafka-topics
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m Topics purged and recreated."

# ---- Container diagnostics ----
diagnose-frauds:
	@echo "=== 1. Config file in container ==="
	docker exec ms-frauds sh -c 'cat //app/config/application.properties' || echo "FILE NOT FOUND"
	@echo ""
	@echo "=== 2. Schema Registry reachable from ms-frauds ==="
	docker exec ms-frauds curl -sf http://schema-registry:8081/subjects || echo "SCHEMA REGISTRY UNREACHABLE"
	@echo ""
	@echo "=== 3. Raw message from topic (hex first bytes) ==="
	docker exec kafka kafka-console-consumer \
	  --bootstrap-server localhost:9092 \
	  --topic transaction-validation-request \
	  --from-beginning --max-messages 1 --timeout-ms 5000 2>/dev/null || echo "NO MESSAGES OR TIMEOUT"

diagnose-payments:
	@echo "=== 1. Config file in ms-payments container ==="
	docker exec ms-payments sh -c 'cat //app/config/application.properties' || echo "FILE NOT FOUND"
	@echo ""
	@echo "=== 2. Schema Registry reachable from ms-payments ==="
	docker exec ms-payments curl -sf http://schema-registry:8081/subjects || echo "SCHEMA REGISTRY UNREACHABLE"
	@echo ""
	@echo "=== 3. KafkaJsonSchemaSerializerConfig from ms-payments logs ==="
	docker logs ms-payments 2>&1 | grep -A5 "schema.registry.url" | head -20

services-rebuild:
	@echo "Force rebuilding all services (no cache)..."
	docker rm -f ms-payments ms-frauds 2>/dev/null || true
	docker compose -p services -f docker-compose-services.yml build --no-cache ms-payments ms-frauds
	docker compose -p services -f docker-compose-services.yml up -d ms-payments ms-frauds
	@echo -e "\033[32m \xE2\x9C\x94 \033[0m Services rebuilt and started."
