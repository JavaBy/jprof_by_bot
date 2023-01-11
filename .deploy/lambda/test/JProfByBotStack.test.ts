import * as cdk from 'aws-cdk-lib';
import {Template} from 'aws-cdk-lib/assertions';
import {JProfByBotStack} from '../lib/JProfByBotStack';

describe('JProfByBotStack', () => {
    const app = new cdk.App();
    const stack = new JProfByBotStack(app, 'JProfByBotStack', {
        telegramToken: 'TOKEN_TELEGRAM_BOT',
        youtubeToken: 'TOKEN_YOUTUBE_API',
        dailyUrbanDictionaryEmail: 'EMAIL_DAILY_URBAN_DICTIONARY',
        env: {region: 'us-east-1'}
    });
    const template = Template.fromStack(stack);

    test('All DynamoDB tables use PAY_PER_REQUEST billing mode', () => {
        let tables = template.findResources('AWS::DynamoDB::Table')

        for (const [_, table] of Object.entries(tables)) {
            expect(table.Properties.BillingMode).toBe('PAY_PER_REQUEST')
        }
    })
})
