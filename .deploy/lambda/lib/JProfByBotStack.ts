import * as cdk from '@aws-cdk/core';
import {Duration, RemovalPolicy} from '@aws-cdk/core';
import {JProfByBotStackProps} from './JProfByBotStackProps';
import * as dynamodb from '@aws-cdk/aws-dynamodb';
import * as lambda from '@aws-cdk/aws-lambda';
import * as apigateway from '@aws-cdk/aws-apigateway';

export class JProfByBotStack extends cdk.Stack {
    constructor(scope: cdk.Construct, id: string, props: JProfByBotStackProps) {
        super(scope, id, props);

        const votesTable = new dynamodb.Table(this, 'jprof-by-bot-table-votes', {
            tableName: 'jprof-by-bot-table-votes',
            partitionKey: {name: 'id', type: dynamodb.AttributeType.STRING},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: RemovalPolicy.DESTROY,
        });
        const youtubeChannelsWhitelistTable = new dynamodb.Table(this, 'jprof-by-bot-table-youtube-channels-whitelist', {
            tableName: 'jprof-by-bot-table-youtube-channels-whitelist',
            partitionKey: {name: 'id', type: dynamodb.AttributeType.STRING},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
        });
        const kotlinMentionsTable = new dynamodb.Table(this, 'jprof-by-bot-table-kotlin-mentions', {
            tableName: 'jprof-by-bot-table-kotlin-mentions',
            partitionKey: {name: 'chat', type: dynamodb.AttributeType.NUMBER},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
        });
        const dialogStatesTable = new dynamodb.Table(this, 'jprof-by-bot-table-dialog-states', {
            tableName: 'jprof-by-bot-table-dialog-states',
            partitionKey: {name: 'userId', type: dynamodb.AttributeType.NUMBER},
            sortKey: {name: 'chatId', type: dynamodb.AttributeType.NUMBER},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
        });
        const quizojisTable = new dynamodb.Table(this, 'jprof-by-bot-table-quizojis', {
            tableName: 'jprof-by-bot-table-quizojis',
            partitionKey: {name: 'id', type: dynamodb.AttributeType.STRING},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
        });
        const moniesTable = new dynamodb.Table(this, 'jprof-by-bot-table-monies', {
            tableName: 'jprof-by-bot-table-monies',
            partitionKey: {name: 'user', type: dynamodb.AttributeType.NUMBER},
            sortKey: {name: 'chat', type: dynamodb.AttributeType.NUMBER},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: RemovalPolicy.DESTROY,
        });

        const layerLibGL = new lambda.LayerVersion(this, 'jprof-by-bot-lambda-layer-libGL', {
            code: lambda.Code.fromAsset('layers/libGL.zip'),
            compatibleRuntimes: [lambda.Runtime.JAVA_11],
        });
        const layerLibfontconfig = new lambda.LayerVersion(this, 'jprof-by-bot-lambda-layer-libfontconfig', {
            code: lambda.Code.fromAsset('layers/libfontconfig.zip'),
            compatibleRuntimes: [lambda.Runtime.JAVA_11],
        });

        const lambdaWebhook = new lambda.Function(this, 'jprof-by-bot-lambda-webhook', {
            functionName: 'jprof-by-bot-lambda-webhook',
            runtime: lambda.Runtime.JAVA_11,
            layers: [
                layerLibGL,
                layerLibfontconfig,
            ],
            timeout: Duration.seconds(30),
            memorySize: 1024,
            code: lambda.Code.fromAsset('../../runners/lambda/build/libs/jprof_by_bot-runners-lambda-all.jar'),
            handler: 'by.jprof.telegram.bot.runners.lambda.JProf',
            environment: {
                'LOG_THRESHOLD': 'DEBUG',
                'TABLE_VOTES': votesTable.tableName,
                'TABLE_YOUTUBE_CHANNELS_WHITELIST': youtubeChannelsWhitelistTable.tableName,
                'TABLE_KOTLIN_MENTIONS': kotlinMentionsTable.tableName,
                'TABLE_DIALOG_STATES': dialogStatesTable.tableName,
                'TABLE_QUIZOJIS': quizojisTable.tableName,
                'TOKEN_TELEGRAM_BOT': props.telegramToken,
                'TOKEN_YOUTUBE_API': props.youtubeToken,
            },
        });

        votesTable.grantReadWriteData(lambdaWebhook);
        youtubeChannelsWhitelistTable.grantReadData(lambdaWebhook);
        kotlinMentionsTable.grantReadWriteData(lambdaWebhook);
        dialogStatesTable.grantReadWriteData(lambdaWebhook);
        quizojisTable.grantReadWriteData(lambdaWebhook);

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

        new cdk.CfnOutput(this, 'URL', {
            value: api.deploymentStage.urlForPath()
        });
    }
}
