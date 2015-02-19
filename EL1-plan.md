# Elaboration Phase 1 

## Milestones

- Project layout of server and client side is created
- UC1 Make a Vote is partially implemented
	- Project list can be queried from the server
	- Project list is displayed on the client side
	- Partial information about the project are stored
	  - Project name, description, Team name
	- Project information can be queried from the server
	- Project information is displayed on the client side
	- "Best of" vote can be placed
- UC2 Edit a vote and UC5 View voted group is implemented
	- When "best of" vote is placed the second time, a dialog asking for voting change confirmation is required.
- UC8 Authentication by KU account is implemented
	- KU account are verified by the server
	- KU account are asked in the client
- The server side is deployed on gg.whs.in.th
- The client side is runnable in web browser and Android phone

## Risks

- KU server does not allow authentication

## Technical specifications

- The server is written in Play Framework
- The database .............. (TBD by Varis)
- The communication protocol between the server and client is Protocol Buffers
- The client side can be compiled with Cordova
- The client side use Ionic Framework as its interface