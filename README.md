# Serverless AWS Websocket Demo for Java

As clients connect and disconnect to a WebSocket API in AWS API Gateway, and messages get sent back and forth between 
server and client, associated routes which describe how API Gateway should handle client request are called and these 
routes can be configured to invoke an AWS Lambda. These are these Lambdas, written in Java... FINALLY! 

  
### Usage

#### Prerequisites
- [AWS Account](https://docs.aws.amazon.com/AmazonSimpleDB/latest/DeveloperGuide/AboutAWSAccounts.html)
- [Docker for Mac](https://docs.docker.com/v17.12/docker-for-mac/install/)
- [Java 11 OpenJDK](https://jdk.java.net/11/)
- [IntelliJ Ultimate IDE](https://www.jetbrains.com/idea/download/previous.html)
    - Version 2018.3 does not support the `lombok` plugin. Please use 2018.2.7  

You will need to have a deployed WebSocket API. More information can be found at: https://aws.amazon.com/blogs/compute/announcing-websocket-apis-in-amazon-api-gateway/

#### Getting Started
After deploying, grab the WebSocket Connection URL *(minus the @connections)* and set it as the ``WEBSOCKET_CONNECTION_URL`` environment variable in the [template.yml](../template.yml). 

Build
- `./gradlew clean build`
- `./gradlew buildZip`

Deploy the Lambdas, using [AWS SAM](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-reference.html), [Terraform](https://seanmcgary.com/posts/how-to-deploy-an-aws-lambda-with-terraform/), or manually.
 
Go back to API Gateway and configure the corresponding routes to point to the deployed Lambdas.

![Alt text](websocket_api_screenshot.png?raw=true)
    
Connect to the WebSocket API with WebSocket URL using [wscat](https://github.com/websockets/wscat) or a browser library.

Connect and disconnect and send messages back and forth and verify the Lambdas are being called by checking the logs.


### Testing Lambda Locally

#### Install Docker
 - `brew cask install docker`
 - Start the docker daemon by opening `Docker` from your Applications folder
 - `brew install docker-compose`
  
#### Setup Database
Database connection configurations are defined by environment variables in [template.yml](../template.yml). 
The database is seeded by liquibase files in the `database` module.

Start postgres running locally on port `5432`.
```
./gradlew startDB
```
Load demo schema and sample data into running Postgres database
```
./gradlew loadSchema
```
Shutdown database after testing
```
./gradlew stopDB
```

#### Install [AWS SAM CLI](https://github.com/awslabs/aws-sam-cli)  
- `brew tap aws/tap`
- `brew install aws-sam-cli`
- More information can be found [here](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install-mac.html)

##### AWS SAM Configuration
AWS SAM configuration is defined in [template.yml](../template.yml)
  
#### Build and invoke specific Lambda functions
Run the following commands to build entire project:
- `./gradlew clean build`
- `./gradlew buildZip`

Invoke each specific Lambda function locally::

###### Connect Lambda
- `sam local invoke "WebsocketConnect" -e websocket-connect/src/test/resources/sample-wss-connect-request.json --skip-pull-image --docker-network host`

Check the `Connections` database table for the persisted connection id. Change the value in the sample request file to test different connection ids.


###### Disconnect Lambda
- `sam local invoke "WebsocketDisconnect" -e websocket-disconnect/src/test/resources/sample-wss-disconnect-request.json --skip-pull-image --docker-network host`

Check the `Connections` database table to see the previously saved connection id has been deleted. 


###### Error Lambda
- `sam local invoke "WebsocketError" -e websocket-error/src/test/resources/sample-wss-error-request.json --skip-pull-image --docker-network host`

Should see `error` log message indicating message not sent because of invalid route key.

NOTE: May see `fatal` log message because function attempts to push message to connected client using sample connection id. 
Connect a client to the WebSocket API, obtain connection id, update the sample request, and run command again to see message pushed to client.

###### Receive Message Lambda
- `sam local invoke "WebsocketReceiveMessage" -e websocket-receivemessage/src/test/resources/sample-wss-message-request.json --skip-pull-image --docker-network host`

Should see `info` log message indicating message was successfully received.

NOTE: May also see `fatal` log message because function attempts to push message to connected client using sample connection id. 
Connect a client to the WebSocket API, obtain connection id, update the sample request, and run command again to see message pushed to client.

###### Backend Send Message Lambda
- `echo '""' | sam local invoke "WebsocketBackendSendMessage" --skip-pull-image --docker-network host`
- The input can be anything since it is ignored in this sample. Just needs to be valid JSON. 

NOTE: May see `fatal` log message because function attempts to push message to connected client using sample connection id. 
Connect a client to the WebSocket API, obtain connection id, update the `Connections` database table, and run command again to see message pushed to client.
