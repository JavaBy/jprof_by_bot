enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "jprof_by_bot"

include(":utils:dynamodb")
include(":utils:aws-junit5")
include(":utils:tgbotapi-serialization")
include(":votes")
include(":votes:dynamodb")
include(":votes:tgbotapi-extensions")
include(":votes:voting-processor")
include(":monies")
include(":monies:dynamodb")
include(":dialogs")
include(":dialogs:dynamodb")
include(":core")
include(":jep")
include(":youtube")
include(":youtube:dynamodb")
include(":kotlin")
include(":kotlin:dynamodb")
include(":quizoji")
include(":quizoji:dynamodb")
include(":eval")
include(":pins")
include(":pins:dto")
include(":pins:unpin")
include(":pins:dynamodb")
include(":pins:sfn")
include(":runners:lambda")
include(":currencies")
