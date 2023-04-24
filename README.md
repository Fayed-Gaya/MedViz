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
      <a href="#concepts-covered">Concepts Covered</a></li>
    <li><a href="#feature-list">Feature List</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

[![Product Name Screen Shot][product-screenshot]](https://example.com)

Insert screenshot of main page....

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Project Proposal
The Medical Visualizer, or MedViz for short, is a data visualization application focused on empowering users to derive meaningful insights from medical data including but not limited to patient records, and administrative payroll information.

### Systems Analysis
MedViz extends the foundational, three-tiered data system by re-implementing it from the ground up with increased polish and refinement. The system will have a user interface layer in which users interact with a simple to understand GUI to read data and visualize the results. Users will be able to create logins with special priviledges for admin accounts with passwords being stored with proper password hashing and salting. Queries are processed and converted into accurate and performant logic in the second client tier of the system. Additionally, The client tier is connected to a NoSQL database with a server front end that can handle multiple client requests and ultimatley serves results to clients using Restful APIs. The system will be concurrency safe to support multiple simultaneous client connections and can even run certain query types in  parallel to maximize server load efficiency.

### Data Sources
Data is drawn from Synthea. A project released by best-in-class Not-for-profit MITRE containing synthetic patient and population health data from the state of Massachusetts. Data will be loaded into and queried from a GCP Firestore NOSQL database hosted on a Firebase instance.
- Synthea (Create link here)


<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- Concepts Covered -->
## Concepts Covered

Advanced Java Concepts:
1. Databases, NOSQL
2. Concurrency, Multi-client support
3. Networking
4. GUI and Data Visualization

<!-- Feature List -->
### Feature List

1. noSQL DBs, GCP Firestore
2. Clean UI for querying Databases
3. Concurrency control

<p align="right">(<a href="#readme-top">back to top</a>)</p>
