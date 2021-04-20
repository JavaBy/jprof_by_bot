import * as cdk from '@aws-cdk/core';
import { Duration } from '@aws-cdk/core';
import { JProfByBotStackProps } from './JProfByBotStackProps';
import lambda = require('@aws-cdk/aws-lambda');
import apigateway = require('@aws-cdk/aws-apigateway');

export class JProfByBotStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props: JProfByBotStackProps) {
    super(scope, id, props);

    const lambdaWebhook = new lambda.Function(this, 'jprof-by-bot-lambda-webhook', {
      functionName: 'jprof-by-bot-lambda-webhook',
      runtime: lambda.Runtime.JAVA_11,
      timeout: Duration.seconds(30),
      memorySize: 1024,
      code: lambda.Code.fromAsset('../../runners/lambda/build/libs/jprof_by_bot-runners-lambda-all.jar'),
      handler: 'by.jprof.telegram.bot.runners.lambda.JProf',
      environment: {
        'LOG_THRESHOLD': 'DEBUG',
      },
    });

    const api = new apigateway.RestApi(this, 'jprof-by-bot-api', {
      restApiName: 'jprof-by-bot-api',
      cloudWatchRole: false,
      endpointTypes: [apigateway.EndpointType.REGIONAL],
      deployOptions: {
        loggingLevel: apigateway.MethodLoggingLevel.INFO,
        dataTraceEnabled: true,
        metricsEnabled: true,
        tracingEnabled: true,
      },
    });

    api.root
      .addResource(props.telegramToken.replace(':', '_'))
      .addMethod('POST', new apigateway.LambdaIntegration(lambdaWebhook));
  }
}
