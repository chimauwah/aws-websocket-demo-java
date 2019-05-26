variable "region" {
  description = "AWS region"
}

variable "websocket_connection_url" {
  description = "Callback URL for sending message to a connected client"
}

variable "dynamodb_table_name" {
  default = "connections"
}

variable "lambda_timeout_seconds" {
  default = 30
}
variable "lambda_memory_size" {
  default = 256
}

variable "common_tags" {
  type = "map"
  default = {
    "Project" = "Serverless AWS Websocket Demo for Java"
    "OriginalAuthor" = "Chima Uwah"
    "Provisioner" = "Terraform"
  }
}
