![MIT License](/src/main/webapp/assets/img/MIT.png "MIT")

<details>
  <summary>License</summary>

**MIT License ┬Е 2025 Aerosimo**

Permission is hereby granted, free of charge, to any person obtaining a copy  
of this software and associated documentation files (the "Software"), to deal  
in the Software without restriction, including without limitation the rights  
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
copies of the Software, and to permit persons to whom the Software is  
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all  
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  
SOFTWARE.

The characters, names, events, articles, templates, or information provided by  
Aerosimo Ltd are fictional and for reference only. While we strive to keep the  
information up to date and correct, we make no representations or warranties of  
any kind, express or implied, about the completeness, accuracy, reliability,  
suitability, or availability with respect to the information, articles, templates,  
or related graphics contained in this document or any part of the project.  
Any reliance you place on such information is therefore strictly at your own risk.
</details>

---

![Project Cover](/src/main/webapp/assets/img/cover.jpg "PersonaHub")

# PersonaHub
> A central hub or unified service for all personal identity data. It is a secure, aggregated source of truth for an individual's records.

---

## Project Structure

This `README.md` gives an overview of the project structure and instructions on how to build and deploy the application.

## Features

**REST Web Service**: Comprises  
- `/avatar/upload` method to upload user image using form-data.
- `/avatar/transfer` method to upload user image in base64.
- `/avatar/{username}` method to retrieve user image.
- `/avatar/{username}` method to delete user image.
- `/address` method to store user address details.
- `/address/{username}` method to retrieve user address details.
- `/address` method to delete user address details.
- `/contact` method to store user contact details.
- `/contact/{username}` method to retrieve user contact details.
- `/contact` method to delete user contact details.
- `/person` method to user personal details.
- `/person/{username}` method to retrieve user personal details.
- `/person` method to delete user personal details.


## Getting Started

![Project Codes & Tasks](/src/main/webapp/assets/img/code.jpg "Project Codes and Task")

---

## Project Badges & Tech Stack

<p align="center">
  <img src="https://img.shields.io/badge/Java-25%2B-orange?logo=java&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Jakarta%20EE-10-blue?logo=jakartaee&logoColor=white" alt="Jakarta EE 10">
  <img src="https://img.shields.io/badge/Apache%20TomEE-10.1.44-critical?logo=apachetomcat&logoColor=white" alt="Apache TomEE">
  <img src="https://img.shields.io/badge/Oracle%20Database-19c-red?logo=oracle&logoColor=white" alt="Oracle 19c">
  <img src="https://img.shields.io/badge/Maven-3.9%2B-C71A36?logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/REST%20API-JAX--RS-green?logo=rest&logoColor=white" alt="JAX-RS">
  <img src="https://img.shields.io/badge/Frontend-JSP%20%2F%20FetchAPI-purple?logo=html5&logoColor=white" alt="JSP + Fetch API">
  <img src="https://img.shields.io/badge/Logging-Log4j-yellow?logo=apache&logoColor=white" alt="Log4j">
</p>

---

### Prerequisites

>- **Apache Tomcat 11**: is the application server used during development, but any Jakarta EE 10-compliant server should work.
>- **Java 25**: Ensure you have Java 23 installed as TomEE 10 targets Jakarta EE 10.
>- **Maven**: The project uses Maven for dependency management with any IDE of choice.

### Dependencies

The required dependencies are defined in `pom.xml`. Below are the key dependencies:

- **Jakarta EE 10 API**: Provides JAX-WS support.

### Building and Running the Application

## Quickstart

1. **Clone the repository**:

    ```bash
    git clone https://github.com/aerosimo/personahub.git
    cd personahub
    ```

2. **Build the project using Maven**:

    ```bash
    mvn clean install
    ```

3. **Deploy the WAR file**:

   After building the project, deploy the generated `WAR` file from the `target/` directory into the `webapps/` folder of your preferred Jakarta EE 10-compatible server.

4. **Start your preferred Jakarta EE 10-compatible server**:

   Start server and access the application:

    - REST Service: `http://localhost:8080/personalhub/api/profile/*`
    - Web Interface: `http://localhost:8080/personahub/index.jsp`

![Project Cover](/src/main/webapp/assets/img/coding.jpg "Coding")

## Detailed Explanation of Components


### **REST Web Service** (JAX-RS)

The REST web service is implemented in `com.aerosimo.ominet.api.PersonaHubREST.java`.

## Contributing

We welcome feedback and contributions. Please open an issue or submit a pull request.

---

![Aerosimo Logo.!](/src/main/webapp/assets/img/logo.png "Aerosimo")