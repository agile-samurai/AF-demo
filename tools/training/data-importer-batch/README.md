# SBIR Connect Service

##Installation & Architecture
See [SBIR Connect README](../README.md) one directory up.

## How to
##### Build
From root of service directory
```bash
mvn clean install
```

##### Build Docker Image
From root of service directory
```bash
mvn clean install -Pdocker,embedded
```

##### Run Tests
From root of service directory
```bash
mvn verify
```

##### Run
From root of service directory
```bash
docker-compose up
```

##### Stop
From root of service directory
```bash
docker-compose down
```