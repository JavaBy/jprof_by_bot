enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "jprof_by_bot"

include(":utils:dynamodb")
include(":votes")
include(":votes:dynamodb")
include(":core")
include(":runners:lambda")
