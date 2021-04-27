enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "jprof_by_bot"

include(":utils:dynamodb")
include(":votes")
include(":votes:dynamodb")
include(":votes:tgbotapi-extensions")
include(":votes:voting-processor")
include(":core")
include(":jep")
include(":runners:lambda")
