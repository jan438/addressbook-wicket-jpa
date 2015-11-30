    Address Book Web Application with Apache Wicket, Java, Eclipse Persistence JPA and PostgreSQL

This is a small web application build using with Apache Wicket Framework, JPA and PostgreSQL. It contains two sub-projects located in the following folders:

    - addressbook-wicket-ui-jpa: Renders the user interface in the web browser
    - addressbook-wicket-sql-jpa: Contains the Eclipse Persistence data models and interfaces for 
      data manipulation

Before running the Wicket Address Book, the following requirements must be met:

    - Maven 3 must be installed
    - GIT must be installed
    - Tomcat Server must be installed (I use version 7.0.65) or 
    - Wildfly Server must be installed (I use version wildfly-9.0.2.Final) 
    

To run the Wicket Address Book on Tomcat, follow these steps:

    1. Start your Tomcat Server and deploy the addressbook-wicket-ui-jpa web application
    2. Open the browser at http://192.168.1.31/addressbook-wicket-ui-jpa-1.1

To run the Wicket Address Book on Wildfly, follow these steps:

    1. Start your Tomcat Server and deploy the addressbook-wicket-ui web application
    2. Open the browser at http://localhost:8080/addressbook-wicket-ui-jpa-1.1
    
Once the application started you can register a new user and then login.

For the kitten captcha create the following property files in the directory: apache-wicket-7.1.0/wicket-extensions/src/main/java/org/apache/wicket/extensions/captcha/kittens

KittenCaptchaPanel_de.properties
pleaseWait=Bitte warten...
animalsSelected=von der 3 Kätzchen ausgewählt
instructions=Bitte warten...

KittenCaptchaPanel_nl.properties
pleaseWait=Wilt U a.u.b wachten....
animalsSelected=van de 3 katjes geselecteerd
instructions=Selecteer alle drie katjes hier beneden
