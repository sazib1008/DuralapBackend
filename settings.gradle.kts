rootProject.name = "duralap-backend"

// Common library modules
include("common:common-dto")
include("common:common-utils")
include("common:common-events")
include("common:common-security")
include("common:common-mongo")
include("common:common-redis")
include("common:common-kafka")
include("common:common-websocket")

// Microservice modules
include("gateway-service")
include("auth-service")
include("user-service")
include("chat-service")
include("message-service")
include("media-service")
include("presence-service")
include("notification-service")
include("analytics-service")
include("search-service")
