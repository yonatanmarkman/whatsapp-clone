# whatsapp-clone

This is the final project I've built during the course Web Programming.

This project is a single-page web application.
It is chatting website similar to WhatsApp, where users can register, and chat on a private channel (one-to-one chat), or on a group channel (more than two people at a time).

## Front-End

The single page itself is written in HTML, CSS, and AngularJS.
The page design was built using the Bootstrap framework.
The functionality of the page uses jQuery and AJAX to manipulate the page.

The biggest challege in this part was learning AngularJS from scratch, and ofcourse debugging and testing the communication between the client and the server.

## Back-End

The back-end of the website is written in Java, using the Apache Tomcat framework.
The code functions as a RESTful Api, which uses servlets to communicate with the Database.
Also it uses the WebSocket technology, so that the chats are updated in real-time.
The database itself was stored on an Apache Derby server.
I managed the Database using the Squirrel SQL Client.

<<<<<<< HEAD
The biggest challenge in this part was to plan the DB schema before the implementation. And also, testing the servlets code, ensuring that data from the client-side JS was succesfully parsed to Java models using the JSON objects.
=======
The biggest challenge in this part was to plan the DB schema before the implementation. And also, testing the servlets code, ensuring that data from the client-side JS was succesfully parsed to Java models using the JSON objects.
>>>>>>> 912efe9c26534838196da6d07d2f0dbbd5f59e74
