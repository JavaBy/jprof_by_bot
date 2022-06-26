# Contributing to this repository

## Getting started

Before you begin:

- This project is powered by [Kotlin](https://kotlinlang.org), [TelegramBotAPI](https://github.com/InsanusMokrassar/TelegramBotAPI), [Koin](https://insert-koin.io), [Skija](https://github.com/JetBrains/skija), and [GraphQL](https://graphql.org) technologies, [AWS](https://aws.amazon.com) services and [CDK](https://aws.amazon.com/cdk) with [TypeScript](https://www.typescriptlang.org), and [GitHub Actions](https://github.com/features/actions).
- Have you read the [code of conduct](CODE_OF_CONDUCT.md)?
- Check out the [existing issues](https://github.com/JavaBy/jprof_by_bot/issues).
- Discuss your plans in [the chat](https://t.me/jprof_by) or in an issue.
[Open one](https://github.com/JavaBy/jprof_by_bot/issues/new) if needed.

## Make your update

Touching the code?
Make sure to add some tests demonstrating the use cases or solved issues.

Try to stay modular.
We split the code in modules based on features.

When adding a new entity / table, don't put any DB-specific code in the same module.
Create an interface for the DAO and define the entity in the main module.
Then create a DB-specific submodule with the DAO implementation and entity mappers.

Try to stay serverless.
Stick to the technologies that can fit into AWS free tier: this is a non-commercial bot and it should not cost a lot to host it.
Examples are Lambda, DynamoDB, S3 - they are cheap.

Don't forget to describe the resources you need in the CDK scripts.
If you don't know how to do that - leave it to the owners.

## Open a pull request

When you're done making changes and you'd like to propose them for review, open your PR (pull request).

You can use the GitHub user interface :pencil2: for small changes, like fixing a typo or updating a README.
You can also fork the repo and then clone it locally, to view changes and run your tests on your machine.

## Submit your PR & get it reviewed

- Once you submit your PR, it will be reviewed.
- After that, there may be questions, check back on your PR to keep up with the conversation.
- Did you have an issue, like a merge conflict?
  Check out this [git tutorial](https://lab.github.com/githubtraining/managing-merge-conflicts) on how to resolve merge conflicts and other issues.

## Your PR is merged

Congratulations!

## Issue labels

Labels can help you find an issue you'd like to help with.

- The [`help wanted` label](https://github.com/JavaBy/jprof_by_bot/labels/help%20wanted) is for problems or updates that anyone in the community can start working on.
- The [`good first issue` label](https://github.com/JavaBy/jprof_by_bot/labels/good%20first%20issue) is for problems or updates we think are ideal for beginners.
- The [`CI/CD` label](https://github.com/JavaBy/jprof_by_bot/labels/CI%2FCD) is for problems or updates in the testing, releasing & publishing process.
These will usually require some knowledge of TypeScript and Github Actions to fix.
