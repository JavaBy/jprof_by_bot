import * as cdk from '@aws-cdk/core';

export interface JProfByBotStackProps extends cdk.StackProps {
  readonly telegramToken: string;
}
