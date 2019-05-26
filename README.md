# Serverless AWS Websocket Demo for Java

This project demonstrates how to write and integrate AWS Lambda functions in Java with the WebSocket API in AWS API Gateway. 
The demos serve as a starting point for users looking to build serverless applications with AWS WebSocket API, AWS Lambda and Java. 

### Usage

#### Prerequisites
- [AWS Account](https://docs.aws.amazon.com/AmazonSimpleDB/latest/DeveloperGuide/AboutAWSAccounts.html)
- [Docker for Mac](https://docs.docker.com/v17.12/docker-for-mac/install/)
- [Java 11 OpenJDK](https://jdk.java.net/11/)
- [IntelliJ Ultimate IDE](https://www.jetbrains.com/idea/download/previous.html)
    - Version 2018.3 does not support the `lombok` plugin. Please use 2018.2.7  

You will need to have a deployed WebSocket API. More information can be found at: https://aws.amazon.com/blogs/compute/announcing-websocket-apis-in-amazon-api-gateway/

#### Getting Started
This code expects that you have AWS credentials set up per: 
http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html

Build
```
$ ./gradlew clean build 
$ ./gradlew buildZip
```

Deploy the AWS resources using [Terraform](https://seanmcgary.com/posts/how-to-deploy-an-aws-lambda-with-terraform/)
```
$ cd terraform
$ terraform init
$ terraform apply
```
*You will have to enter the region in which you want the resources deployed and the Connection URL 
provided with your deployed Websocket API in API Gateway (minus the @connections).*

**NOTE:**
Charges will incur deploying and using AWS services. Be sure to execute `terraform destroy` to tear down the AWS resources created by Terraform when no longer using.

Go back to API Gateway and configure the corresponding routes to point to the deployed Lambdas.

![Alt text](websocket_api_screenshot.png?raw=true)
    
Connect to the WebSocket API with WebSocket URL using [wscat](https://github.com/websockets/wscat) or a browser library.

Connect, disconnect and send messages back and forth as you normally would with Websockets and verify the Lambdas are being called by checking the Cloudwatch logs.
You can test the backend send message Lambda in the console by configuring a test event using any valid JSON as input. 


### Testing Lambda Locally

#### Install Docker
 ```
$ brew cask install docker
 ```
Start the docker daemon by opening `Docker` from your Applications folder
```
$ brew install docker-compose
```
  
#### Setup Database
Database connection configurations are defined by environment variables in [template.yml](template.yml). 
The database is seeded by liquibase files in the `database` module.

The following commands will start postgres running locally on port `5432` and create the demo database, the ws_demo 
schema, and the connections table with one sample record.
```
$ ./gradlew startDB
```
Load demo schema and sample data into running Postgres database
```
$ ./gradlew loadSchema
```
Shutdown database after testing
```
$ ./gradlew stopDB
```

#### Install [AWS SAM CLI](https://github.com/awslabs/aws-sam-cli)  
```
$ brew tap aws/tap
$ brew install aws-sam-cli
```
More information on how to use SAM to package, deploy, and describe your application can be found [here](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install-mac.html).

##### AWS SAM Configuration
AWS SAM configuration is defined in [template.yml](template.yml). Take the Connection URL from the deployed Websocket API 
in API Gateway and set the value *(minus the @connections)* as the ``WEBSOCKET_CONNECTION_URL`` environment variable in the [template.yml](../template.yml). 
  
#### Build and invoke specific Lambda functions
Run the following commands to build entire project:
```
$ ./gradlew clean build
$ ./gradlew buildZip`
```

Invoke each specific Lambda function locally::

##### Connect Lambda
```
$ sam local invoke "WebsocketConnect" \ 
      -e websocket-connect/src/test/resources/sample-wss-connect-request.json \
      --skip-pull-image \
      --docker-network host
```     

Check the `connections` database table for the persisted connection id. 
Change the value in the sample request file to test with different connection ids.


##### Disconnect Lambda
```
$ sam local invoke "WebsocketDisconnect" \
      -e websocket-disconnect/src/test/resources/sample-wss-disconnect-request.json \
      --skip-pull-image \
      --docker-network host
```

Check the `connections` database table to see the previously saved connection id has been deleted. 


##### Error Lambda
```
$ sam local invoke "WebsocketError" \
      -e websocket-error/src/test/resources/sample-wss-error-request.json \
      --skip-pull-image \
      --docker-network host
```

Should see `info` log message indicating message not sent because of invalid route key.

**NOTE:** May also see `warn` log message because function attempts to push message to connected client using sample connection details. 
Connect a client to the WebSocket API, update the sample request with the stage, domain name and connection id from the logs, 
and run the command again to see message pushed to client.

##### Receive Message Lambda
```
$ sam local invoke "WebsocketReceiveMessage" \
    -e websocket-receivemessage/src/test/resources/sample-wss-message-request.json \
    --skip-pull-image \
    --docker-network host
```    

Should see `info` log message indicating message was successfully received.

**NOTE:** May also see `warn` log message because function attempts to push message to connected client using sample connection details. 
Connect a client to the WebSocket API, update the sample request with the stage, domain name and connection id from the logs,
and run the command again to see message pushed to client.

##### Backend Send Message Lambda
```
$ echo '""' | sam local invoke "WebsocketBackendSendMessage" \
  --skip-pull-image \
  --docker-network host
```
The input can be anything since it is ignored in this sample. Just needs to be valid JSON. 

**NOTE:** May see `error` log message and failure response because function attempts to push message to connected client using sample connection id. 
Connect a client to the WebSocket API, update the `Connections` database table with connection id from the logs, and run command again to see message pushed to client.
