# Kafka messages sending DEMO
There are 2 services:
- Order-service
- Order-status-service

Steps:
1. User send POST-request with product (see [Endpoints](#endpoints)) in Order-service. 
2. Order-service send message with POST-request's data in Kafka Brocker.
3. Order-status-service get this message from Kafka and send new message in Kafka.
4. Order-service get message from Order-status-service.

## Quick start
1. Download project
```
git clone Kofa-Yoh/6.5-spring-boot-kafka-order-service
cd 6.5-spring-boot-kafka-order-service
```
2. Start Zookeeper, Kafka, Order-status-app services via Docker - in terminal
```
cd docker
.\docker-start.cmd
# or
.\docker-start.sh
``` 
3. Start main order-service project
```
./gradlew bootRun
```
4. After work - to stop services
```
# Stop Order-status-app service
Ctrl + C

# Stop and delete containers, images, volumes
 .\docker-stop.cmd
# or
 .\docker-stop.sh
```

## Endpoints
```
POST http://localhost:8080/api/v1/order/kafka/send
{
    "product":"phone",
    "quantity":"100"
}
```
