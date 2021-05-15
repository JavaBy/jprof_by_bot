package by.jprof.telegram.bot.utils.aws_junit5

import me.madhead.aws_junit5.common.AWSEndpoint

class Endpoint : AWSEndpoint {
    override fun url(): String {
        return System.getenv("DYNAMODB_URL")
    }

    override fun region(): String {
        return System.getenv("AWS_DEFAULT_REGION")
    }

    override fun accessKey(): String {
        return System.getenv("AWS_ACCESS_KEY_ID")
    }

    override fun secretKey(): String {
        return System.getenv("AWS_SECRET_ACCESS_KEY")
    }
}
