#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import { JProfByBotStack } from '../lib/JProfByBotStack';

if (process.env.TELEGRAM_BOT_TOKEN == null) {
  throw new Error('Undefined TELEGRAM_BOT_TOKEN')
}

const app = new cdk.App();
new JProfByBotStack(
  app,
  'JProfByBotStack',
  {
    telegramToken: process.env.TELEGRAM_BOT_TOKEN,
    env: {
      region: 'us-east-1'
    }
  }
);
