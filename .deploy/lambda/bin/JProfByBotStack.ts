#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import {JProfByBotStack} from '../lib/JProfByBotStack';

if (process.env.TOKEN_TELEGRAM_BOT == null) {
    throw new Error('Undefined TOKEN_TELEGRAM_BOT')
}

if (process.env.TOKEN_YOUTUBE_API == null) {
    throw new Error('Undefined TOKEN_YOUTUBE_API')
}

if (process.env.EMAIL_DAILY_URBAN_DICTIONARY == null) {
    throw new Error('Undefined EMAIL_DAILY_URBAN_DICTIONARY')
}

const app = new cdk.App();
new JProfByBotStack(
    app,
    'JProfByBotStack',
    {
        telegramToken: process.env.TOKEN_TELEGRAM_BOT,
        youtubeToken: process.env.TOKEN_YOUTUBE_API,
        dailyUrbanDictionaryEmail: process.env.EMAIL_DAILY_URBAN_DICTIONARY,
        env: {
            region: 'us-east-1'
        }
    }
);
