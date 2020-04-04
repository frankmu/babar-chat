# babar-chat
Lightweight messaging system

## how to run it
1. Build the project from babar-parent using maven `mvn clearn install`
2. Run the application in following order:
- CoreStarter
- GateStarter
- ClientStarter
3. Go to `localhost`

## feature highlight
- Layered services including Core, Gateway and Client. Each service can be scaled out separately if needed
- 2 ways to communicate between services: gRPC and Rest built by Spring Boot
- Use netty as Websocket server to communicate with client for better performance
- Use Redis as MQ to push messages between Core and Gateway
- Other tech specs: Spring Data, JPA, Lombok, 
