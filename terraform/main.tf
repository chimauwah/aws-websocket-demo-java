# Configure the AWS Provider
provider "aws" {
  region = "${var.region}"
}

locals {
  dynamodb_table_name = "${var.dynamodb_table_name}"

  ws_connect_lambda_name = "demo_ws_connect"
  ws_disconnect_lambda_name = "demo_ws_disconnect"
  ws_error_lambda_name = "demo_ws_error"
  ws_recieve_message_lambda_name = "demo_ws_receive_message"
  ws_backend_send_message_lambda_name = "demo_ws_backend_send_message"

  aws_iam_role_name = "demo_exec_role"
}

# Create dynamodb table resoure
resource "aws_dynamodb_table" "connections_dynamodb_table" {
  name = "${local.dynamodb_table_name}"
  hash_key = "connectionId"
  read_capacity = 1
  write_capacity = 1
  stream_enabled = true
  stream_view_type = "NEW_IMAGE"

  attribute {
    name = "connectionId"
    type = "S"
  }

  tags = "${merge(var.common_tags, map(
       "Name", local.dynamodb_table_name,
       "Role", "DynamoDB"
  ))}"
}

# Create Lambda functions
resource "aws_lambda_function" "websocket_connect_lambda" {
  function_name = "${local.ws_connect_lambda_name}"
  description = "Demo Websocket Connect Lambda Service"
  filename = "../websocket-connect/build/distributions/websocket-connect.zip"
  handler = "com.chimauwah.aws.websocket.WebsocketConnectHandler::handleRequest"
  source_code_hash = "${filebase64sha256("../websocket-connect/build/distributions/websocket-connect.zip")}"
  runtime = "java8"
  timeout = "${var.lambda_timeout_seconds}"
  memory_size = "${var.lambda_memory_size}"
  role = "${aws_iam_role.demo_exec_role.arn}"

  tags = "${merge(var.common_tags, map(
       "Name", local.ws_connect_lambda_name,
       "Role", "Lambda"
  ))}"
}

resource "aws_lambda_function" "websocket_disconnect_lambda" {
  function_name = "${local.ws_disconnect_lambda_name}"
  description = "Demo Websocket Disconnect Lambda Service"
  filename = "../websocket-disconnect/build/distributions/websocket-disconnect.zip"
  handler = "com.chimauwah.aws.websocket.WebsocketDisconnectHandler::handleRequest"
  source_code_hash = "${filebase64sha256("../websocket-disconnect/build/distributions/websocket-disconnect.zip")}"
  runtime = "java8"
  timeout = "${var.lambda_timeout_seconds}"
  memory_size = "${var.lambda_memory_size}"
  role = "${aws_iam_role.demo_exec_role.arn}"

  tags = "${merge(var.common_tags, map(
       "Name", local.ws_disconnect_lambda_name,
       "Role", "Lambda"
  ))}"
}

resource "aws_lambda_function" "websocket_error_lambda" {
  function_name = "${local.ws_error_lambda_name}"
  description = "Demo Websocket Error Lambda Service"
  filename = "../websocket-error/build/distributions/websocket-error.zip"
  handler = "com.chimauwah.aws.websocket.WebsocketErrorHandler::handleRequest"
  source_code_hash = "${filebase64sha256("../websocket-error/build/distributions/websocket-error.zip")}"
  runtime = "java8"
  timeout = "${var.lambda_timeout_seconds}"
  memory_size = "${var.lambda_memory_size}"
  role = "${aws_iam_role.demo_exec_role.arn}"

  environment {
    variables {
      "WEBSOCKET_CONNECTION_URL" = "${var.websocket_connection_url}"
    }
  }

  tags = "${merge(var.common_tags, map(
       "Name", local.ws_error_lambda_name,
       "Role", "Lambda"
  ))}"
}

resource "aws_lambda_function" "websocket_receive_message_lambda" {
  function_name = "${local.ws_recieve_message_lambda_name}"
  description = "Demo Websocket Receive Message Lambda Service"
  filename = "../websocket-receivemessage/build/distributions/websocket-receivemessage.zip"
  handler = "com.chimauwah.aws.websocket.WebsocketReceiveMessageHandler::handleRequest"
  source_code_hash = "${filebase64sha256("../websocket-receivemessage/build/distributions/websocket-receivemessage.zip")}"
  runtime = "java8"
  timeout = "${var.lambda_timeout_seconds}"
  memory_size = "${var.lambda_memory_size}"
  role = "${aws_iam_role.demo_exec_role.arn}"

  environment {
    variables {
      "WEBSOCKET_CONNECTION_URL" = "${var.websocket_connection_url}"
    }
  }

  tags = "${merge(var.common_tags, map(
       "Name", local.ws_recieve_message_lambda_name,
       "Role", "Lambda"
  ))}"
}

resource "aws_lambda_function" "websocket_backend_send_message_lambda" {
  function_name = "${local.ws_backend_send_message_lambda_name}"
  description = "Demo Websocket Backend Send Message Lambda Service"
  filename = "../websocket-backend-sendmessage/build/distributions/websocket-backend-sendmessage.zip"
  handler = "com.chimauwah.aws.websocket.WebSocketBackendSendMessageHandler::handleRequest"
  source_code_hash = "${filebase64sha256("../websocket-backend-sendmessage/build/distributions/websocket-backend-sendmessage.zip")}"
  runtime = "java8"
  timeout = "${var.lambda_timeout_seconds}"
  memory_size = "${var.lambda_memory_size}"
  role = "${aws_iam_role.demo_exec_role.arn}"

  environment {
    variables {
      "WEBSOCKET_CONNECTION_URL" = "${var.websocket_connection_url}"
    }
  }

  tags = "${merge(var.common_tags, map(
       "Name", local.ws_backend_send_message_lambda_name,
       "Role", "Lambda"
  ))}"
}

# Create role for demo Lambda functions
resource "aws_iam_role" "demo_exec_role" {
  name = "${local.aws_iam_role_name}"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF

  tags = "${merge(var.common_tags, map(
       "Name", local.aws_iam_role_name,
       "Role", "IAM Role"
  ))}"
}

# Create policy for created role
resource "aws_iam_role_policy" "demo_policy" {
  name = "demo_policy"
  role = "${aws_iam_role.demo_exec_role.id}"
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "cloudwatch:*",
                "dynamodb:*",
                "lambda:*",
                "logs:*"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

# Create policy for invoking manage connections
resource "aws_iam_role_policy" "demo_api_gateway_invoke_policy" {
  name = "demo_api_gateway_invoke_policy"
  role = "${aws_iam_role.demo_exec_role.id}"
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "execute-api:Invoke",
                "execute-api:ManageConnections"
            ],
            "Resource": "arn:aws:execute-api:*:*:*"
        }
    ]
}
EOF
}
