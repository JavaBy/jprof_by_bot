import * as cdk from 'aws-cdk-lib';

export interface JProfByBotStackProps extends cdk.StackProps {
    readonly telegramToken: string;
    readonly youtubeToken: string;
}
