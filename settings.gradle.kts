rootProject.name = "duralap-backend"

// Application entry point
include("app")

// Shared modules
include("shared:shared-kernel")
include("shared:shared-security")
include("shared:shared-mongo")
include("shared:shared-redis")
include("shared:shared-websocket")

// Business modules
include("modules:auth")
include("modules:user")
include("modules:chat")
include("modules:message")
include("modules:presence")
include("modules:notification")
include("modules:media")
include("modules:search")
include("modules:analytics")
