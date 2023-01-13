import * as cdk from 'aws-cdk-lib';
import {Construct} from 'constructs';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import {Architecture} from 'aws-cdk-lib/aws-lambda';
import * as secrets from 'aws-cdk-lib/aws-secretsmanager';
import * as sfn from 'aws-cdk-lib/aws-stepfunctions';
import * as tasks from 'aws-cdk-lib/aws-stepfunctions-tasks';
import * as ses from 'aws-cdk-lib/aws-ses';
import * as sesActions from 'aws-cdk-lib/aws-ses-actions';
import {JProfByBotStackProps} from "./JProfByBotStackProps";

export class JProfByBotStack extends cdk.Stack {
    constructor(scope: Construct, id: string, props: JProfByBotStackProps) {
        super(scope, id, props);

        const secretPaymentProviderTokens = new secrets.Secret(this, 'jprof-by-bot-secret-payment-provider-tokens', {
            secretName: 'jprof-by-bot-secret-payment-provider-tokens',
            secretObjectValue: {
                test: cdk.SecretValue.unsafePlainText('test'),
                production: cdk.SecretValue.unsafePlainText('production'),
            }
        });

        const votesTable = new dynamodb.Table(this, 'jprof-by-bot-table-votes', {
            tableName: 'jprof-by-bot-table-votes',
            partitionKey: {name: 'id', type: dynamodb.AttributeType.STRING},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: cdk.RemovalPolicy.DESTROY,
        });
        const youtubeChannelsWhitelistTable = new dynamodb.Table(this, 'jprof-by-bot-table-youtube-channels-whitelist', {
            tableName: 'jprof-by-bot-table-youtube-channels-whitelist',
            partitionKey: {name: 'id', type: dynamodb.AttributeType.STRING},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: cdk.RemovalPolicy.DESTROY,
        });
        const kotlinMentionsTable = new dynamodb.Table(this, 'jprof-by-bot-table-kotlin-mentions', {
            tableName: 'jprof-by-bot-table-kotlin-mentions',
            partitionKey: {name: 'chat', type: dynamodb.AttributeType.NUMBER},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: cdk.RemovalPolicy.DESTROY,
        });
        const dialogStatesTable = new dynamodb.Table(this, 'jprof-by-bot-table-dialog-states', {
            tableName: 'jprof-by-bot-table-dialog-states',
            partitionKey: {name: 'userId', type: dynamodb.AttributeType.NUMBER},
            sortKey: {name: 'chatId', type: dynamodb.AttributeType.NUMBER},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: cdk.RemovalPolicy.DESTROY,
        });
        const quizojisTable = new dynamodb.Table(this, 'jprof-by-bot-table-quizojis', {
            tableName: 'jprof-by-bot-table-quizojis',
            partitionKey: {name: 'id', type: dynamodb.AttributeType.STRING},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: cdk.RemovalPolicy.DESTROY,
        });
        const moniesTable = new dynamodb.Table(this, 'jprof-by-bot-table-monies', {
            tableName: 'jprof-by-bot-table-monies',
            partitionKey: {name: 'user', type: dynamodb.AttributeType.NUMBER},
            sortKey: {name: 'chat', type: dynamodb.AttributeType.NUMBER},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: cdk.RemovalPolicy.DESTROY,
        });
        const pinsTable = new dynamodb.Table(this, 'jprof-by-bot-table-pins', {
            tableName: 'jprof-by-bot-table-pins',
            partitionKey: {name: 'messageId', type: dynamodb.AttributeType.NUMBER},
            sortKey: {name: 'chatId', type: dynamodb.AttributeType.NUMBER},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: cdk.RemovalPolicy.DESTROY,
        });
        const timezonesTable = new dynamodb.Table(this, 'jprof-by-bot-table-timezones', {
            tableName: 'jprof-by-bot-table-timezones',
            partitionKey: {name: 'user', type: dynamodb.AttributeType.NUMBER},
            sortKey: {name: 'chat', type: dynamodb.AttributeType.NUMBER},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: cdk.RemovalPolicy.DESTROY,
        });
        const languageRoomsTable = new dynamodb.Table(this, 'jprof-by-bot-table-language-rooms', {
            tableName: 'jprof-by-bot-table-language-rooms',
            partitionKey: {name: 'id', type: dynamodb.AttributeType.STRING},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: cdk.RemovalPolicy.DESTROY,
        });
        const urbanWordsOfTheDayTable = new dynamodb.Table(this, 'jprof-by-bot-table-urban-words-of-the-day', {
            tableName: 'jprof-by-bot-table-urban-words-of-the-day',
            partitionKey: {name: 'date', type: dynamodb.AttributeType.STRING},
            billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
            removalPolicy: cdk.RemovalPolicy.DESTROY,
        });

        pinsTable.addGlobalSecondaryIndex({
            indexName: 'chatId',
            partitionKey: {name: 'chatId', type: dynamodb.AttributeType.NUMBER},
            projectionType: dynamodb.ProjectionType.ALL,
        });
        pinsTable.addGlobalSecondaryIndex({
            indexName: 'userId',
            partitionKey: {name: 'userId', type: dynamodb.AttributeType.NUMBER},
            projectionType: dynamodb.ProjectionType.ALL,
        });
        timezonesTable.addGlobalSecondaryIndex({
            indexName: 'username',
            partitionKey: {name: 'username', type: dynamodb.AttributeType.STRING},
            sortKey: {name: 'chat', type: dynamodb.AttributeType.NUMBER},
            projectionType: dynamodb.ProjectionType.ALL,
        });

        const lambdaUnpin = new lambda.Function(this, 'jprof-by-bot-lambda-unpin', {
            functionName: 'jprof-by-bot-lambda-unpin',
            runtime: lambda.Runtime.JAVA_11,
            timeout: cdk.Duration.seconds(30),
            memorySize: 512,
            code: lambda.Code.fromAsset('../../pins/unpin/build/libs/jprof_by_bot-pins-unpin-all.jar'),
            handler: 'by.jprof.telegram.bot.pins.unpin.Handler',
            environment: {
                'LOG_THRESHOLD': 'INFO',
                'TABLE_PINS': pinsTable.tableName,
                'TOKEN_TELEGRAM_BOT': props.telegramToken,
            },
        });

        const stateMachineUnpin = new sfn.StateMachine(this, 'jprof-by-bot-state-machine-unpin', {
            stateMachineName: 'jprof-by-bot-state-machine-unpin',
            stateMachineType: sfn.StateMachineType.STANDARD,
            definition: new sfn.Wait(this, 'jprof-by-bot-state-machine-unpin-wait', {
                time: sfn.WaitTime.secondsPath('$.ttl'),
            }).next(new tasks.LambdaInvoke(this, 'jprof-by-bot-state-machine-unpin-unpin', {
                lambdaFunction: lambdaUnpin,
                invocationType: tasks.LambdaInvocationType.EVENT,
                retryOnServiceExceptions: false,
            })),
            tracingEnabled: false,
        });

        const layerLibGL = new lambda.LayerVersion(this, 'jprof-by-bot-lambda-layer-libGL', {
            layerVersionName: 'libGL',
            code: lambda.Code.fromAsset('layers/libGL.zip'),
            compatibleArchitectures: [Architecture.X86_64],
        });
        const layerLibfontconfig = new lambda.LayerVersion(this, 'jprof-by-bot-lambda-layer-libfontconfig', {
            layerVersionName: 'libfontconfig',
            code: lambda.Code.fromAsset('layers/libfontconfig.zip'),
            compatibleArchitectures: [Architecture.X86_64],
        });
        const layerParametersAndSecretsLambdaExtension = lambda.LayerVersion.fromLayerVersionArn(
            this,
            'jprof-by-bot-lambda-layer-parametersAndSecretsLambdaExtension',
            'arn:aws:lambda:us-east-1:177933569100:layer:AWS-Parameters-and-Secrets-Lambda-Extension:2'
        )

        const lambdaWebhookTimeout = cdk.Duration.seconds(29);
        const lambdaWebhook = new lambda.Function(this, 'jprof-by-bot-lambda-webhook', {
            functionName: 'jprof-by-bot-lambda-webhook',
            runtime: lambda.Runtime.JAVA_11,
            layers: [
                layerLibGL,
                layerLibfontconfig,
                layerParametersAndSecretsLambdaExtension,
            ],
            timeout: lambdaWebhookTimeout,
            maxEventAge: cdk.Duration.minutes(5),
            retryAttempts: 0,
            memorySize: 512,
            code: lambda.Code.fromAsset('../../launchers/lambda/build/libs/jprof_by_bot-launchers-lambda-all.jar'),
            handler: 'by.jprof.telegram.bot.launchers.lambda.JProf',
            environment: {
                'LOG_THRESHOLD': 'INFO',
                'TABLE_VOTES': votesTable.tableName,
                'TABLE_YOUTUBE_CHANNELS_WHITELIST': youtubeChannelsWhitelistTable.tableName,
                'TABLE_KOTLIN_MENTIONS': kotlinMentionsTable.tableName,
                'TABLE_DIALOG_STATES': dialogStatesTable.tableName,
                'TABLE_QUIZOJIS': quizojisTable.tableName,
                'TABLE_MONIES': moniesTable.tableName,
                'TABLE_PINS': pinsTable.tableName,
                'TABLE_TIMEZONES': timezonesTable.tableName,
                'TABLE_LANGUAGE_ROOMS': languageRoomsTable.tableName,
                'TABLE_URBAN_WORDS_OF_THE_DAY': urbanWordsOfTheDayTable.tableName,
                'STATE_MACHINE_UNPINS': stateMachineUnpin.stateMachineArn,
                'TOKEN_TELEGRAM_BOT': props.telegramToken,
                'TOKEN_YOUTUBE_API': props.youtubeToken,
                'TIMEOUT': lambdaWebhookTimeout.toMilliseconds().toString(),
            },
        });

        (lambdaWebhook.node.defaultChild as lambda.CfnFunction).snapStart = {
            applyOn: 'PublishedVersions'
        };

        const lambdaDailyUrbanDictionary = new lambda.Function(this, 'jprof-by-bot-lambda-daily-urban-dictionary', {
            functionName: 'jprof-by-bot-lambda-daily-urban-dictionary',
            runtime: lambda.Runtime.JAVA_11,
            timeout: cdk.Duration.seconds(30),
            retryAttempts: 0,
            memorySize: 512,
            code: lambda.Code.fromAsset('../../english/urban-dictionary-daily/build/libs/jprof_by_bot-english-urban-dictionary-daily-all.jar'),
            handler: 'by.jprof.telegram.bot.english.urban_dictionary_daily.Handler',
            environment: {
                'LOG_THRESHOLD': 'INFO',
                'TABLE_URBAN_WORDS_OF_THE_DAY': urbanWordsOfTheDayTable.tableName,
                'TABLE_LANGUAGE_ROOMS': languageRoomsTable.tableName,
                'TOKEN_TELEGRAM_BOT': props.telegramToken,
                'STATE_MACHINE_UNPINS': stateMachineUnpin.stateMachineArn,
            }
        });

        new ses.ReceiptRuleSet(this, 'jprof-by-bot-receipt-rule-set-daily-urbandictionary', {
            receiptRuleSetName: 'jprof-by-bot-receipt-rule-set-daily-urbandictionary',
            rules: [
                {
                    receiptRuleName: 'jprof-by-bot-receipt-rule-daily-urbandictionary',
                    recipients: [props.dailyUrbanDictionaryEmail],
                    actions: [
                        new sesActions.Lambda({function: lambdaDailyUrbanDictionary})
                    ]
                }
            ],
        });

        secretPaymentProviderTokens.grantRead(lambdaWebhook);

        votesTable.grantReadWriteData(lambdaWebhook);

        youtubeChannelsWhitelistTable.grantReadData(lambdaWebhook);

        kotlinMentionsTable.grantReadWriteData(lambdaWebhook);

        dialogStatesTable.grantReadWriteData(lambdaWebhook);

        quizojisTable.grantReadWriteData(lambdaWebhook);

        moniesTable.grantReadWriteData(lambdaWebhook);

        pinsTable.grantReadWriteData(lambdaWebhook);
        pinsTable.grantReadWriteData(lambdaUnpin);

        timezonesTable.grantReadWriteData(lambdaWebhook);

        languageRoomsTable.grantReadWriteData(lambdaWebhook);
        languageRoomsTable.grantReadData(lambdaDailyUrbanDictionary);

        urbanWordsOfTheDayTable.grantWriteData(lambdaDailyUrbanDictionary);
        urbanWordsOfTheDayTable.grantReadData(lambdaWebhook);

        stateMachineUnpin.grantStartExecution(lambdaWebhook);
        stateMachineUnpin.grantStartExecution(lambdaDailyUrbanDictionary);

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
            .addMethod('POST', new apigateway.LambdaIntegration(lambdaWebhook.currentVersion));

        new cdk.CfnOutput(this, 'URL', {
            value: api.deploymentStage.urlForPath()
        });
    }
}
