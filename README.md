Real-time Transaction
===============================
## Overview
## Schema
The [included dev.transactionapp.service.yml](service.yml) is the schema. 

## Details
The transaction service accepts two types of transactions:
1) Loads: Add money to a user (credit)

2) Authorizations: Conditionally remove money from a user (debit)

Every load or authorization PUT should return the updated balance following the transaction. Authorization declines should be saved, even if they do not impact balance calculation.

## Bootstrap instructions
### Prerequisites:
- Java 11 or later (https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- Maven (https://maven.apache.org/download.cgi)

### Method 1: Using Maven

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/nileshrathi99/transaction-app.git
2. **Navigate to the Project Directory:**
   ```bash
   cd transaction-app
3. **Build the Project**
    ```bash
   mvn clean install
4. **Run the Server**
    ```bash
    java -jar target/transaction-app-1.0.0.jar

### Method 2: Using an IDE

1. **Import the Project into Your IDE:**
    - Open your preferred IDE (e.g., IntelliJ IDEA, Eclipse).
    - Choose to import a Maven project.
    - Navigate to the cloned directory and select the `pom.xml` file.
    - Complete the import process.

2. **Run the Application:**
    - Find the `Application.java` class, which contains `@SpringBootApplication` annotation.
    - Right-click on the `Application.java` class.
    - Choose the option to run it as a Java application.

### APIs Description
1. **Find All Users**
   - **Endpoint:** `GET /user`
   - **Description:** Handles a GET request to retrieve all users from the database.
   - **Response:** Returns a list of User objects along with HTTP status code OK (200).
   - Two users have been generated for development/testing purpose
   - **Example curl command:**
     ```bash
     curl --location --request GET 'localhost:8080/user'
     ```
     **Response**
     - **Note** You need to use this id as userId to make requests to server.
     ```bash
     [
      {
        "id": "9b79f510-4913-4a2f-8585-bc0cb2f641d2",
        "currency": "USD",
        "balance": 200.0,
        "createdAt": "2024-04-28T16:09:07.334047"
      },
      {
        "id": "2abb3d57-b094-4701-a7d3-edd0b43fd36d",
        "currency": "INR",
        "balance": 500.0,
        "createdAt": "2024-04-28T16:09:07.334068"
      }
     ]
     ```
   
2. **Add User**
   - **Endpoint:** `POST /user`
   - **Description:** Handles a POST request to create a new user with provided details.
   - **Request Body:** JSON object representing user information with attributes `currency` and `balance`.
   - **Response:** Returns the saved User object along with HTTP status code CREATED (201).
   - **Example curl command:**
     ```bash
     curl --location --request POST 'localhost:8080/user' \
     --header 'Content-Type: application/json' \
     --data-raw '{
         "currency":"USD",
         "balance":"500"
     }'
     ```
     **Response**
     - **Note** You need to use this id as userId to make requests to server.
     ```bash
     {
        "id": "5f5a90d7-8b1c-4b33-bf2e-62f2540e0f21",
        "currency": "USD",
        "balance": 500.0,
        "createdAt": "2024-04-28T16:09:13.32465"
     }
     ```
3. **Health Check (Ping)**
   - **Endpoint:** `GET /ping`
   - **Description:** Handles a GET request to perform a basic health check.
   - **Response:** Returns a simple "Ping" object along with HTTP status code OK (200).
   - **Example curl command**:
      ```bash
      curl --location --request GET 'localhost:8080/ping'
      ```
     **Response**
      ```bash
      {
        "serverTime": "2024-04-28T16:18:42.197282"
      }
      ```

4. **Authorize Transaction**
   - **Endpoint:** `PUT /authorization/{messageId}`
   - **Description:** Handles a PUT request to authorize a transaction.
   - **Request Body:** JSON object representing the Authorization Request containing user and transaction details.
   - **Response:** Returns the Authorization Response object along with HTTP status code CREATED (201).
   - **Example curl command (Successful authorization)**: 
      - **Note** "userId" should exist in database (use userId, from 1.Find all Users Api or 2.Add User Api)
      ```bash
      curl --location --request PUT 'localhost:8080/authorization/msg1' \
           --header 'Content-Type: application/json' \
           --data-raw '{
                 "messageId": "msg1",
                 "userId": "9b79f510-4913-4a2f-8585-bc0cb2f641d2",
                 "transactionAmount": {
                    "amount": "180",
                    "currency": "USD",
                    "debitOrCredit": "DEBIT"
                 }
           }'
      ```
      **Response**
      - **Note** The authorization is successful, and the user's balance is updated, which is reflected in the response.
      ```bash
      {
        "messageId": "msg1",
        "userId": "9b79f510-4913-4a2f-8585-bc0cb2f641d2",
        "responseCode": "APPROVED",
        "balance": {
           "amount": "20.0",
           "currency": "USD",
           "debitOrCredit": "DEBIT"
         }
      }
      ```
   - **Example curl command (Unsuccessful authorization)**:
      - **Note** "userId" should exist in database (use userId, from 1.Find all Users Api or 2.Add User Api)
      ```bash
      curl --location --request PUT 'localhost:8080/authorization/msg2' \
           --header 'Content-Type: application/json' \
           --data-raw '{
                 "messageId": "msg2",
                 "userId": "9b79f510-4913-4a2f-8585-bc0cb2f641d2",
                 "transactionAmount": {
                    "amount": "180",
                    "currency": "USD",
                    "debitOrCredit": "DEBIT"
                 }
           }'
      ```
     **Response**
      - **Note** The authorization is unsuccessful. The response includes details of the failed transaction but not the user's balance.
      ```bash
      {
        "messageId": "msg2",
        "userId": "9b79f510-4913-4a2f-8585-bc0cb2f641d2",
        "responseCode": "DECLINED",
        "balance": {
           "amount": "180.0",
           "currency": "USD",
           "debitOrCredit": "DEBIT"
         }
      }
      ```

5. **Load Funds**
   - **Endpoint:** `PUT /load/{messageId}`
   - **Description:** Handles a PUT request to load funds.
   - **Request Body:** JSON object representing the Load Request containing user and transaction details.
   - **Response:** Returns the Load Response object along with HTTP status code CREATED (201).
   - **Example curl command**:
      - **Note** "userId" should exist in database (use userId, from 1.Find all Users Api or 2.Add User Api)
      ```bash
      curl --location --request PUT 'localhost:8080/load/msg3' \
           --header 'Content-Type: application/json' \
           --data-raw '{
                 "messageId": "msg3",
                 "userId": "9b79f510-4913-4a2f-8585-bc0cb2f641d2",
                 "transactionAmount": {
                    "amount": "600",
                    "currency": "USD",
                    "debitOrCredit": "CREDIT"
                 }
           }'
      ```
   
      **Response**
      **Note** The user's balance is loaded and updated, which is reflected in the response.
      ```bash
      {
         "userId": "9b79f510-4913-4a2f-8585-bc0cb2f641d2",
         "messageId": "msg3",
         "balance": {
             "amount": "620.0",
             "currency": "USD",
             "debitOrCredit": "CREDIT"
        }
      }
      ```

6. **Find All Authorization Responses**
   - **Endpoint:** `GET /responses`
   - **Description:** Handles a GET request to retrieve all failed authorization responses from the database.
   - **Response:** Returns a list of AuthorizationResponse objects along with HTTP status code OK (200).
   - **Example curl command**:
     ```bash
     curl --location --request GET 'localhost:8080/responses'
     ```
     **Response**
     ```bash
     [
        {
           "messageId": "msg2",
           "userId": "9b79f510-4913-4a2f-8585-bc0cb2f641d2",
           "responseCode": "DECLINED",
           "balance": {
              "amount": "180.0",
              "currency": "USD",
              "debitOrCredit": "DEBIT"
           }
        }
     ]
     ```

### How to Use

1. **Find All Users:**
   - Send a GET request to `localhost:8080/user` to retrieve a list of all users.

2. **Add User:**
   - Send a POST request to `localhost:8080/user` with a JSON body containing user details like currency and balance.
   
3. **Health Check (Ping):**
   - Send a GET request to `localhost:8080/ping` to perform a basic health check.

4. **Authorize Transaction:**
   - Send a PUT request to `localhost:8080/authorization/{messageId}` with a JSON body containing authorization request details.

5. **Load Funds:**
   - Send a PUT request to `localhost:8080/load/{messageId}` with a JSON body containing load request details.

6. **Find All Authorization Responses:**
   - Send a GET request to `localhost:8080/responses` to retrieve a list of failed authorization responses.

Make sure the server is running locally on port 8080 before making these requests. You can use tools like cURL or Postman to interact with the APIs.

## Design considerations

### Assumptions

* **Pre-existing Users:** The system assumes users are already registered and stored within the database. If a user attempts to interact with the system without prior registration, they'll need to utilize a separate API endpoint (potentially `/user`) for registration. This registration process would establish their initial currency, balance, and provide a unique user ID (UUID) required for subsequent service requests.

* **Single Account & Currency Limitation:** Currently, the system is designed to handle a single account and associated currency per user. Future enhancements may consider expanding functionality to support multiple accounts and currencies.

* **Unique Message IDs are Mandatory:** Every `messageId` included within a request must be demonstrably unique compared to previously saved messages. This design choice prevents the possibility of duplicate processing.

* **Request Validation Enforced:** All incoming requests undergo a validation process to ensure they adhere to established criteria. If validation fails, the user receives a detailed error response explaining the specific issue encountered.

* **Concurrent Access Considerations:** The system anticipates the possibility of concurrent user requests for authorization or updates (e.g., loading funds). This assumption necessitates mechanisms to maintain data consistency and prevent race conditions.

* **Consistency Over Availability:** Due to the focus on maintaining data integrity, the system prioritizes consistency over availability. This means that in the event of potential network issues, the system might prioritize ensuring all data updates are reflected accurately across all replicas (if applicable) even if it results in slightly reduced availability for users.

### User ID

* **Existing User Lookup:** The provided user ID is assumed to exist in the database. This allows retrieving the user's current balance and currency for processing requests. Random user IDs are not allowed.
* **User Registration:** If a user needs to register, a separate API endpoint (e.g., `/user`) would be used. This endpoint would allow creating a user with initial currency and balance. The registration response would provide the user ID needed for future requests.

### Request Handling

* **Response Format:** Users receive informative responses upon request completion, including details about successful processing or error explanations.
* **Message ID Consistency:** The message ID in the request path variable must match the message ID in the request body.

### Amount

* **Validation:** The amount field must be a valid double value between 0 and 1 billion, with up to two decimal places. Invalid values (e.g., "abcdc") trigger error responses with explanations.
* **Currency Matching:** The request body currency must match the user's currency stored in the database. Mismatches result in error responses suggesting supported currencies.

### Currency

* **Enum Representation:** Currency is represented as an enum for clarity and control over supported options.
* **Limited Currency Support:** The system currently supports a limited set of currencies, including USD and INR. Future updated can expand this selection. Requests using unsupported currencies will result in error messages suggesting valid options.

### Debit/Credit

* **Enum Representation:** Debit/Credit options are represented as an enum for clarity and control.
* **Supported Types:** Only "DEBIT" and "CREDIT" transactions are supported. Invalid types trigger error responses suggesting supported options.

### Exception Handling

  This section details the design decisions made for exception handling, prioritizing a user-friendly and robust system. Our primary focus is providing informative error messages that empower users to identify and resolve issues independently.

* **Custom Exception Classes:** Specific exception classes are defined for various error scenarios (e.g., `MessageIdNotMatchException`, `UserNotFoundException`). These exceptions provide clear descriptions of the problem, aiding in debugging and facilitating informative error messages.
* **User-Friendly Error Messages:** Error messages are crafted to be user-friendly, avoiding technical jargon. They clearly explain the issue and, if possible, guide the user towards a solution.
  
These design choices offer several benefits:

* **Improved User Experience:** Clear error messages empower users to troubleshoot issues on their own, reducing reliance on support.
* **Reduced Support Tickets:** By providing informative error messages, users can often resolve problems independently.
* **Efficient Debugging:** Specific error codes and messages simplify debugging efforts for developers.

### Database Choice

* **Relational Database:** A relational database management system (RDBMS) is chosen as the primary data store for this project due to its emphasis on transaction consistency. This aligns with the system's requirement of prioritizing data consistency over potential availability benefits offered by NoSQL databases.

* **CAP Theorem and Consistency:** The CAP theorem, a fundamental principle in distributed data systems, states that a system can only guarantee at most two of the three properties: Consistency, Availability, and Partition Tolerance. In this case, prioritizing consistency implies that all replicas of the data across the system must reflect the same state after an update. This ensures that users always see the most recent and accurate data, regardless of which replica they interact with.

* **H2 In-Memory Database:** For development and testing purposes, this project utilizes H2, an in-memory relational database. H2 offers a lightweight, embedded database option that simplifies development and testing without compromising the core principles of data consistency.


### Asynchronous Calls & Concurrency

* **Pessimistic Locking for Data Consistency:** To handle concurrent user requests and ensure data consistency, pessimistic locking is implemented. This technique ensures that only one thread can access service methods at a time, preventing inconsistencies like the scenario described below.

**Example: Race Condition in Balance Update**

Imagine a scenario where user1 attempts to concurrently load 100 USD to their account using two separate requests (Thread1 and Thread2). The user's current balance is 0 USD.

Without pessimistic locking, the following sequence of events could lead to data inconsistency:

1. **Thread1 and Thread2:** Both threads independently read the user's current balance (0 USD).
2. **Thread1:** Calculates the new balance (0 USD + 100 USD = 100 USD).
3. **Thread2:** Calculates the new balance (0 USD + 100 USD = 100 USD).
4. **Thread1:** Commits the update, setting the user's balance to 100 USD.
5. **Thread2:** Commits the update (based on its outdated balance of 0 USD), also setting the user's balance to 100 USD.

This results in an inconsistency: the user's balance ends up as 100 USD instead of the expected 200 USD (100 USD from each request).

**Pessimistic Locking to the Rescue**

By utilizing pessimistic locking, we can prevent this scenario. Here's how it works:

1. **Thread1 Requests Lock:** When Thread1 initiates the request, it acquires a lock on the user's data in the database. This lock prevents other threads (like Thread2) from accessing the same data until Thread1 completes its operation.
2. **Thread1 Reads Balance:** Thread1 reads the user's current balance (0 USD).
3. **Thread1 Calculates New Balance:** Thread1 calculates the new balance (0 USD + 100 USD = 100 USD).
4. **Thread1 Updates Balance:** Thread1 updates the user's balance in the database to 100 USD.
5. **Thread1 Releases Lock:** Thread1 releases the lock on the user's data.
6. **Thread2 Requests Lock (After Wait):** Now, when Thread2 attempts to acquire a lock (after potentially waiting due to Thread1 holding the lock), it can proceed.
7. **Thread2 Reads Balance (100 USD):** Thread2 reads the updated balance (100 USD) from the database.
8. **Thread2 Calculates New Balance (Redundant):** Since the balance is already 100 USD, any further calculation by Thread2 (100 USD + 100 USD = 100 USD) would be redundant.
9. **Thread2 Commits Update (100 USD):** Thread2 commits the update, maintaining the correct balance of 100 USD (already set by Thread1).

**Benefits of Pessimistic Locking:**

* Protects against data inconsistencies caused by concurrent updates
* Ensures data integrity in multi-threaded environments
* Guarantees that only one request updates the data at a time

**Implementation Example**

The provided code snippet demonstrates how pessimistic locking can be implemented using JPA annotations. The `@Lock(LockModeType.PESSIMISTIC_WRITE)` annotation applied to the `findById` method in the `UserRepository` ensures that any operation fetching a user by ID also acquires a pessimistic write lock on the user's data. This prevents other threads from modifying the same user data until the current operation completes.
### Transaction Management

* **Transactional Service Methods:** The service methods, like `authorizeTransactionAndGetResponse` and `loadFundsAndGetResponse`, are annotated with `@Transactional`. This annotation plays a crucial role in maintaining data consistency within the system.

* **Ensuring Data Integrity:** By applying `@Transactional`, we guarantee that either all operations within a service method execute successfully or none of them do. This all-or-nothing approach prevents the occurrence of partial updates and inconsistencies in case of errors.

* **Example Scenario:**
    Imagine a scenario where the `authorizeTransactionAndGetResponse` method attempts to update a user's balance and send a response. Without `@Transactional`, an error might occur during the response transmission. In such a case, the user's balance could be updated even though the user never receives confirmation. This could lead to inconsistencies in the system's data.

* **Transactional Guarantee:** With `@Transactional`, if the response transmission fails, the entire operation (including the balance update) is rolled back. This ensures that the system's data remains consistent and reflects the actual state of transactions.

### High Level Diagram (Flow Chart)

![image](https://github.com/codescreen/CodeScreen_cr9u116u/assets/32071800/115d6aac-6518-4cbb-a5c3-7f4fb450c368)

### API Performance of service

- **Endpoint:** `/load/{messageId}`

![image](https://github.com/codescreen/CodeScreen_cr9u116u/assets/32071800/e42cf9ce-640c-4dd5-9204-7407a79ad7ec)

- **Endpoint:** `/authorization/{messageId}`

![image](https://github.com/codescreen/CodeScreen_cr9u116u/assets/32071800/a431d45b-fedd-4c89-92f2-ac42d474609a)


## Deployment
- **Hosting Platform**: Opt for a cloud provider with multiple regions and availability zones to deploy the service where the majority of the user base resides. Utilize AWS, Google Cloud Platform (GCP), or Azure with multi-region support.

- **Containerization**: Use Docker to containerize the Spring Boot application, ensuring consistency and portability across different environments.

- **Orchestration**: Employ Kubernetes for container orchestration across multiple regions. Configure Kubernetes clusters in each region to distribute the workload effectively and ensure high availability.

- **Managed Database Services**: Create replicas of the SQL database backend across multiple availability zones or regions to avoid single points of failure. Utilize managed database services such as Amazon RDS, Google Cloud SQL, or Azure Database for PostgreSQL/MySQL with geo-replication support.

- **Monitoring and Logging**: Implement monitoring and logging solutions across all regions to ensure visibility into the performance and health of the application and databases.

- **Security Best Practices**: Apply security best practices consistently across all regions, including deploying the application over HTTPS, and encrypting sensitive data at rest and in transit.

- **Continuous Integration/Continuous Deployment (CI/CD) Pipelines**: Set up CI/CD pipelines to automate the deployment process across multiple regions. Ensure that changes are tested thoroughly and deployed seamlessly to maintain consistency and reliability.


*In summary, deploying the Spring Boot application with a SQL database backend on a cloud platform using containerization, orchestration, managed database services, monitoring/logging, security best practices, CI/CD pipelines would ensure a robust, scalable, and secure infrastructure for handling financial transactions.*
## ASCII art
```

 .-') _   _  .-')     ('-.         .-') _   .-')     ('-.               .-') _                            .-') _       
(  OO) ) ( \( -O )   ( OO ).-.    ( OO ) ) ( OO ).  ( OO ).-.          (  OO) )                          ( OO ) )      
/     '._ ,------.   / . --. /,--./ ,--,' (_)---\_) / . --. /   .-----./     '._ ,-.-')  .-'),-----. ,--./ ,--,'       
|'--...__)|   /`. '  | \-.  \ |   \ |  |\ /    _ |  | \-.  \   '  .--./|'--...__)|  |OO)( OO'  .-.  '|   \ |  |\       
'--.  .--'|  /  | |.-'-'  |  ||    \|  | )\  :` `..-'-'  |  |  |  |('-.'--.  .--'|  |  \/   |  | |  ||    \|  | )      
   |  |   |  |_.' | \| |_.'  ||  .     |/  '..`''.)\| |_.'  | /_) |OO  )  |  |   |  |(_/\_) |  |\|  ||  .     |/       
   |  |   |  .  '.'  |  .-.  ||  |\    |  .-._)   \ |  .-.  | ||  |`-'|   |  |  ,|  |_.'  \ |  | |  ||  |\    |        
   |  |   |  |\  \   |  | |  ||  | \   |  \       / |  | |  |(_'  '--'\   |  | (_|  |      `'  '-'  '|  | \   |        
   `--'   `--' '--'  `--' `--'`--'  `--'   `-----'  `--' `--'   `-----'   `--'   `--'        `-----' `--'  `--'        
  .-')      ('-.  _  .-')        (`-.                        ('-.                                                      
 ( OO ).  _(  OO)( \( -O )     _(OO  )_                    _(  OO)                                                     
(_)---\_)(,------.,------. ,--(_/   ,. \ ,-.-')   .-----. (,------.                                                    
/    _ |  |  .---'|   /`. '\   \   /(__/ |  |OO) '  .--./  |  .---'                                                    
\  :` `.  |  |    |  /  | | \   \ /   /  |  |  \ |  |('-.  |  |                                                        
 '..`''.)(|  '--. |  |_.' |  \   '   /,  |  |(_//_) |OO  )(|  '--.                                                     
.-._)   \ |  .--' |  .  '.'   \     /__),|  |_.'||  |`-'|  |  .--'                                                     
\       / |  `---.|  |\  \     \   /   (_|  |  (_'  '--'\  |  `---.                                                    
 `-----'  `------'`--' '--'     `-'      `--'     `-----'  `------'                                                    
             
```
