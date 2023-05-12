<!-- Back to top Link -->
<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/Fayed-Gaya/MedViz">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">MedViz</h3>

  <p align="center">
    A data visualization application focused on empowering users to derive meaningful insights from medical data
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#project-proposal">Project Proposal</a></li>
        <li><a href="#systems-analysis">Systems Analysis</a></li>
        <li><a href="#data-sources">Data Sources</a></li>
      </ul>
    </li>
    <li>
      <a href="#concepts-covered">Concepts Covered</a>
      <ul>
        <li><a href="#GUI">Graphical User Interface</a></li>
        <li><a href="#networking">Networking</a></li>
        <li><a href="#concurrency">Concurrency</a></li>
        <li><a href="#database-support">Databases</a></li>
      </ul>
    </li>
    <li><a href="#feature-list">Feature List</a>
        <ul>
            <li><a href="#login">Login</a></li>
            <li><a href="#sign-up">Sign-Up</a></li>
            <li><a href="#patient-creation">Patient Creation</a></li>
            <li><a href="#value-lookup-search">Value Lookup Search</a></li>
            <li><a href="#patient-update">Patient Update</a></li>
            <li><a href="#patient-deletion">Patient Deletion</a></li>
            <li><a href="#aggregate-value-search">Aggregate Value Search</a></li>
        </ul>
    </li>
    <li><a href="#feature-list">Project Setup</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

<!-- [![Product Name Screen Shot][product-screenshot]](https://example.com) -->

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Project Proposal
The Medical Visualizer, or MedViz for short, is a data visualization application focused on empowering users to derive meaningful insights from medical data including but not limited to patient records, and administrative payroll information.

### Systems Analysis
MedViz extends the foundational, three-tiered data system by re-implementing it from the ground up with increased polish and refinement. The system will have a user interface layer in which users interact with a simple to understand GUI to read data and visualize the results. Users will be able to create logins with special priviledges for admin accounts with passwords being stored with proper password hashing and salting. Queries are processed and converted into accurate and performant logic in the second client tier of the system. Additionally, The server tier is connected to a NoSQL database and can handle multiple client requests, ultimatley serving results to clients using Restful APIs. The system will be concurrency safe to support multiple simultaneous client connections and can even run certain query types in parallel to maximize server load efficiency.

### Data Sources
Data is drawn from Synthea. A project released by best-in-class Not-for-profit MITRE containing synthetic patient and population health data from the state of Massachusetts. Data will be loaded into and queried from a GCP Firestore NOSQL database hosted on a Firebase instance.
- [Synthea](https://synthea.mitre.org/)


<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- Concepts Covered -->
## Concepts Covered

### GUI
The project's graphical user interface, or GUI was built primarily with Java's Swing library. It uses a single frame which is repainted as necessary.
The different frame paints include:
  
  ![login](images/login.png)

  1. Login Page
  The page where the user logs in. Only verified users are a logged in and Admin accounts are given special priveledge. Additionally, the user is given a message that details whether entered information is missing, a username does not exist, or a password is incorrect. Navigation to the sign up page and a credential reset button is available.
  
  
  ![signup](images/signUp.png)

  2. Sign-Up Page
  The page where a use can sign up. Checks are run on whether credentials are left empty and if a username is taken. The user is correspondingly notified. Once a user is signed up they can navigate to the login page and login using their credentials.
  
  
  ![main](images/mainPage.png)

  3. Main Page
  The main page functions as a way to naviage to the different parts of MedViz including the create, update, and delete patient information pages and the MedViz vizualization page. All of those pages contain a link to navigate back to the main page. Users can also logout from the main page which closes the client application.

  
  ![create](images/patientCreation.png)

  4. Create Patient Page
  The create patient page allows users to enter patient information in order to create a new patient record in the database. Input validation is conducted at the client level. Additionally, if users try to create a patient that already exists, they are notified of this error.

  
  ![medViz](images/medViz.png)

  5. MedViz Page
  The MedViz Page allows for two different types of data visualization. The first is a visuzalization based on looking up a particular value with choices for the types of conditions applied to that value, including equals to, less than, less than or equals to, greater than, greater than or equals to, and not equals to. The second visualization allows users to search up frequency of a given condition, selected from a drop down menu, betweeen a range of years. This is the marquee feature of MedViz.

  
  ![update](images/updatePatient.png)

  6. Update Patient Page
  The Update patient page allow users to update any value within a patient record. Standard input validation and patient existence checking is applied.

  
  ![delete](images/deletePatient.png)

  7. Delete Patient Page
  Admin privledged users can delete patient records from the database.


### Networking
Client
    - Creates a session to send and receive query data to and from server
Server
    - Can handle multiple client connection

### Concurrency
Multi threaded, locked at db and within server for special operations

### Database Support
Database
    - Store user information
    - Store data
    - NoSQL Database:GCP Firestore

<!-- Feature List -->
## Feature List

### Login
login, login validation, messaging

### Sign Up
signup, signup validation, messaging

### Patient Creation
create patient, input validation

### Value Lookup Search
Lookup and visualize data based on field

### Patient Update
Update patient records

### Patient Deletion
Delete patient records

### Aggregate Value Search
Search and visuzalize aggreate patient condition datas

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[product-screenshot]: images/loginTemp.png
[login]: images/login.png
[signup]: images/signUp.png
[create]: images/patientCreation.png
[delete]: images/deletePatient.png
[update]: images/updatePatient.png
[search]: images/medViz.png
[main]: images/main.png