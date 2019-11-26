# Flat Viewing Scheduler
  
API for creating reservation for flat view

## Prerequisites

- Java 8
- Maven
- Docker

## Testing

run ```mvn verify``` to start integration tests

## Usage

run
```mvn package```
to create docker image called ```flat-viewing-scheduler```

start it with mapping to port you want
```docker run -d -p{port}:8080 flat-viewing-scheduler```

## API

### Flats

#### Create Flat
Request:
```POST /flat```

Body:
```{"address": "Flat address", "currentTenantId": 123}```

Response:
```{"id": 1}```

#### Get Flat by id
Request:
```GET /flat/:id```

Response:
```{"id": 1, "address": "Flat address", "currentTenantId": 123}```

### Tenants

#### Create Tenant
Request:
```POST /tenant```

Body:
```{"name": "Tenant name"}```

Response:
```{"id": 1}```

#### Get Tenant by id
Request:
```GET /tenant/:id```

Response:
```{"id": 1, "name": "Tenant name"}```

### View Reservations

#### Create View Reservation
Request:
```POST /reservation```

Body:
```{"flatId": 1, "tenantId": 1, "startTime": "2020-01-03T12:00"}```

start time format:
```yyyy-MM-dd'T'HH:mm```

Response:
```{"id": 1, "status": "OK"}```

Possible statuses:
```
    FLAT_NOT_EXISTS
    TENANT_NOT_EXISTS
    NOT_VALID_START_TIME
    TIME_ALREADY_RESERVED
```

#### Get View Reservation by id
Request:
```GET /reservation```

Response:
```{"id": 1, "flatId": 1, "tenantId": 1, "startTime": "2020-01-03T12:00"}```

#### Approve View Reservation
Request:
```POST /reservation/:id/approve```

#### Reject View Reservation
Request:
```POST /reservation/:id/reject```

#### Cancel View Reservation
Request:
```POST /reservation/:id/cancel```
