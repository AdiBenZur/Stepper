# Stepper
Stepper is a pipeline system designed for non-technical users to define, execute, and manage automated process (called flows).   
The system focuses on reuse of the various operational components as well as applying the same solution several times for many cases. 

The architecture design aims for ease of extensibility with additional capabilities that will enrich the system flows.

The project offers diverse UI options (CLI, GUI) and client-server architecture, supporting multiple clients at the same time, and management users permissions.   
It employs multithreading and asynchronous flows executions, with clients receiving status updates and result using pull’ing.

### Skills:
-	Java 8
-	Tomcat- open-source web server
-	JSON handling- Gson
-	Desktop application- JavaFX
-	Multithreading
- XML parser- Jaxb

### Modules:
1.	Engine. 
The stepper-engine module is the heart of the system, responsible for driving its core functionality. It serves as the central component that coordinates the entire system and includes tasks like parsing XML, executing flow, managing users and permissions.

2.	Web App.
The WebApp module serves as the interface for users interacting with the system- regular users, and administrator. It includes graphical user interface (GUI) for each type. In addition, it holds all the servlets (endpoints) of the system, that integrate with the engine.

3.	DTO – data transfer objects.
The DTO module contains objects designed for efficient data transfer, particularly suitable for use with Gson serialization. These objects are simple POJOs (Plain Old Java Object) holding data of the domain objects in the system.

4.	UI.
The ui module provides two distinct interfaces interacting with the system: command-line interface (CLI) and a graphical user interface (GUI), without relying on a client-server setup. This module is not under active maintenance.


### User Types and Responsibilities:
Administrator:
-	Load data (flows): Upload XML files to the system, from which the system dynamically generates and manages the available flows.
-	Role management: Add new roles to the system and assign them to specific flows.
-	Manage users: View all users in the system (online or offline) and assign roles to control which flows are accessible to each user.
-	Execution history: View all past executions performed by users.
-	Statistics: View execution statistics, including the duration of flows and steps.

  ![image](https://github.com/user-attachments/assets/e594f7c7-977c-4f87-8156-7ca4f24e4a8e)        ![image](https://github.com/user-attachments/assets/4c3072d8-8a99-4981-86cd-9099e2572608)

  

