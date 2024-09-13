# Client Card Application

## 1. Setting Up Environment Variables

Make sure to set the following environment variables before running the project:

- `DB_USERNAME=sa`
- `DB_PASSWORD=password`

These variables are required for the database connection. You can set them in your system's environment variables or in your IDE's run configuration.

## 2. Installing and Running Kafka

### Download Kafka

Download Kafka from the official site: [Kafka Downloads](https://kafka.apache.org/downloads).  
Extract the downloaded file and navigate to the directory where Kafka is located.  
For example, if Kafka is extracted to `C:\kafka_2.12-3.8.0`:

### Start Zookeeper

Open a terminal, navigate to the Kafka folder, and run the following command to start Zookeeper:

```bash
cd C:\kafka_2.12-3.8.0
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
```

Zookeeper should now be running in the terminal.

### Start Kafka Server

Open another terminal window and run the following command to start the Kafka broker:

```bash
cd C:\kafka_2.12-3.8.0
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

This will start Kafka on the default port 9092.

### Create a Kafka Topic

To create a Kafka topic for the application (for example, card-status-topic), run the following command in the Kafka terminal:

```bash
.\bin\windows\kafka-topics.bat --create --topic card-status-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### List Kafka Topics

To check if the topic has been created, you can list all Kafka topics with the following command:

```bash
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```

### Read Kafka Topic Messages

To read messages from the card-status-topic, use the following command:

```bash
.\bin\windows\kafka-console-consumer.bat --topic card-status-topic --from-beginning --bootstrap-server localhost:9092
```

## 3. Running the Application

### Build the Project

Use Maven to build the project:

```bash
mvn clean install
```

### Run the Application

After building, you can run the application using the following command:

```bash
mvn spring-boot:run
```

### Swagger UI

Once the application is running, you can access the API documentation and test the endpoints using Swagger UI:

```bash
http://localhost:8080/swagger-ui/index.html#/
```

### Example request to create a client:
```bash
{
"ime": "Ana",
"prezime": "Anić",
"oib": "12345678903",
"statusKartice": "Accepted"
}
```
## 4. Features

- **Client Management:** Create, retrieve, and delete clients.
- **API Communication:** Send client data to an external API.
- **Kafka Integration:** Asynchronous messaging using Kafka topics.


