enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "jprof_by_bot"

include(":utils:dynamodb")
include(":utils:aws-junit5")
include(":votes")
include(":votes:dynamodb")
include(":votes:tgbotapi-extensions")
include(":votes:voting-processor")
include(":core")
include(":jep")
include(":runners:lambda")
