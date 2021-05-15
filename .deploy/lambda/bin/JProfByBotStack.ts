#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import { JProfByBotStack } from '../lib/JProfByBotStack';

if (process.env.TOKEN_TELEGRAM_BOT == null) {
  throw new Error('Undefined TOKEN_TELEGRAM_BOT')
}

if (process.env.TOKEN_YOUTUBE_API == null) {
  throw new Error('Undefined TOKEN_YOUTUBE_API')
}

const app = new cdk.App();
new JProfByBotStack(
  app,
  'JProfByBotStack',
  {
    telegramToken: process.env.TOKEN_TELEGRAM_BOT,
    youtubeToken: process.env.TOKEN_YOUTUBE_API,
    env: {
      region: 'us-east-1'
    }
  }
);
