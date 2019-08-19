![Web](docs/images/web-image.png)
## Documentation
All documents for this submission can be found in the [docs](docs) folder
## ASSUMPTIONS AND PREREQUISITES

We assume that the developer machines used for this project will be pre-configured with the following tools:

|   | **Clone** | **Build** | **Deploy** |
| --- | --- | --- | --- |
| **Git (for Mac)** | **X** | **X** | **X** |
| **Git-Bash (for Windows)** | **X** | **X** | **X** |
| **JAVA (version 10 or greater)** |   | **X** |   |
| **Maven (v.3.6.1)** |   | **X** |   |
| **Python (v.3.6)** |   | **X** |   |
| **Terraform (v.0.11.13)** |   | **X** |   |
| **Docker (v.18.09.2)** |   | **X** | **X** |
| **NPM (v.6.9.0)** |   | **X** |   |
| **AWS Account** |  | **X** | **X** |


__Note: Instructions for installing these tools and configuring the dev environment can be found in the “Help for Pre-requisites” section of the [Solutions.PDF](docs/Solution.pdf) document located in the main folder of the GitHub repo.__

|   Component                 | Url                               |
|---                          |                            ---    |
|   Build Server              |   http://concourse.mdas2.com      |
|   Sonar                     |   http://sonarqube.mdas2.com      |
|   Ravens Poe - Prod         |   http://www.theravenspoe.io      |
|   Ravens Poe - E2E          |   http://www-test.theravenspoe.io |
|   Ravens Poe - Dev          |   http://www-dev.theravenspoe.io  |
|   Jupyter Notebook - Prod   |   http://notebook-prod.theravenspoe.io     |
|   Jupyter Notebook - E2E    |   http://notebook-test.theravenspoe.io |
|   Jupyter Notebook - Dev    |   http://notebook-dev.theravenspoe.io  |


__INSTRUCTIONS FOR DEPLOYING SOLUTION IN AWS ACCOUNT__

To build the CI/CD pipeline and automatically build the infrastructure and environment, we have provided a one-step script:

* Open a terminal and navigate to the extracted directory `mdas-challenge`
Run the script

`bash tools/configure-env.sh AWS_ACCESS_KEY_ID='<>' AWS_SECRET_ACCESS_KEY='<>' GITHUB_USERNAME='<>' GITHUB_PASSWORD='<>'`

* Replace the <> characters in the above command with the appropriate values. As we do not allow authentication information into version control, please leverage the account information that was provided as part of the code submission for GitHub access.

* The command gets executed for about 15 minutes and the output is a URL that will be presented to the user at the end of execution. This is the Concourse CI URL. Use the URL and the Github credentials provided to log into Concourse CI. When you log in, you will see a build job getting executed. This job will run the build and deploy jobs for the development, end to end testing and production environments The entire process takes around 30 minutes to complete.

_Note after deployment the environments will take a few minutes to fully start, after which the automated build process will continue._

* The development environment will be deployed automatically. In order to ensure the deployment of end-to-end testing and prod environments, follow the below remaining steps:

* Once the deployment phase of stage is complete, click on the next phase in the concourse pipeline to begin that phase (end-to-end testing or prod).
Click on the “+” sign to deploy it into those environments.
Once each stage turns green, click on that stage and then expand the Terraform section to see the URLS.

* Client URL of each stage is where the web end of that stage is deployed.

* Notebook URL of each stage is where the notebook is generated. To log on to notebook, enter the notebook URL into browser and use Github password provided to log into it.

### Troubleshooting
_If the concourse job fails because of “too many concurrent requests or resource not found” errors, restart the job. This is an error caused by race condition in the underlying software (Concourse)._

_If concourse stalls or fails with a “worker stalled” error, it essentially means that the AWS account has limits on the account and was unable to provide workers to Concourse. The best course of action at that point is to restart the process with a fresh AWS account._

__NOTE: This command will also build a docker container that can be used to orchestrate the provisioning of the Continuous Integration environment. This tool will leverage scripts present in our git repository for transparency and audit.__


## SOLUTION DESCRIPTION
1. __High Level Project Overview__

We have included a series of scripts to accelerate delivery and development as follows:

* `build-all.sh && docker-compose up --build` - This will allow a user to run the entire platform locally. This is aimed at providing quick developer feedback and will provision an instance of the entire platform on your local machine. Note on launch, the environment will load all tickers and rate each company so initial start up will take a few minutes.

* `tools/training/data-importer-batch` - Our utility for creating new training sets that will generate a new set of training data. This process is time consuming as it will query the following data sources:
  * Nasdaq - 10 years of Pricing Data and Corporate Analysis
  * SEC – SEC Filings over the last 10 years
  * EODData - Corporate Descriptions and Additional Metadata
  * NYSE & NASDAQ - Corporate press release information

The data-importer-batch is a Java program that leverages Spring-Boot and Spring-Batch to complete work. You can execute this application by going into tools/training/data-importer-batch and executing `mvn spring-boot:run`. This will start the collection of a training data set and place it in tools/training/mdas-data-set.

### Repository Hierarchy List
| PACKAGE | PURPOSE |
| --- | --- |
| /config | Contains all scripts required for configuration management. This is used for provisioning the environment on AWS. All scripts leverage terraform |
| /docs | Contains technical documentation for the project. For functional documentation we have included a series of Unit, Integration, Functional, and Contract Tests. This folder also includes the Solution.PDF, commits.PDF, UserStories.PDF documents. |
| /models | Contains the models.md file. |
| /notebooks | Contains the Jupyter notebooks for the challenge. These notebooks can be accessed using docker-compose up from the root directory and navigating to http://localhost:8084 |
| /services/scoring-service | Contains micro services required for entity scoring and resolution. |
| /tools | Contains the tools and utilities used for quick and easy configuration of the dev environment. |
| /ui | Contains the front-end configuration for the UI that is deployed for a Product Owner and access of the data services. |
| /ui/sector-ui | This is the ReactJS based frontend application that is used for exploring the various Company profiles as they relate to the Nasdaq clustering. |
| /ui/server | A Spring Boot Microservice that is implemented as a BFF (Backend for Frontend) Layer to mediate services calls between Elastic Search (our optimized persistence tier) and the Data Science Microservice. |
| .gitignore | A file that tells Git which files to not track. |
| ReadME.md | The ReadME.md file. |
| build-all.sh | Builds the docker containers locally – used for dev only. |
| docker-compose.yml | Local dev file to orchestrate local dev environment.  Also used in contract tests. |
| generate-project.sh | Initial microservice and configuration scaffolding. |
| pipeline.yml | A descriptor used to determine the entire pipeline. |
| pom.xml | A descriptor of the project as a maven module. |
| Scans.md | The Scans.md file. |
| test-project | A concourse version file. |
| wait-for-status-code-at-url.sh | A shell script for waiting for a status code via a cURL commands and in a loop. |


### Pipeline

Concourse was used to build our end to end, fully automated pipeline and can be accessed (including build history) at http://concourse.mdas2.com.

Within the CI/CD pipeline, code flows from Left to right with the left most step being used to aggregate all the required changes and kick off various build stages.

Each stage is represented as a series of dependencies and outputs which then build our promotion process. Concourse CI provides rapid delivery and enables automation to be accomplished with compliance during the delivery process. Our pipeline includes several features that provide zero-downtime deployments in a high availability configuration:

__Data Science Model Training and Engineering within the Pipeline:__
`Build Training Set -> Train Model -> Promote Model Binary -> Containerize Model Service`

__Data Integration:__
`Build Data Transformations -> Contract Specification & Documentation -> Micro Service Containerization`

__User Experience__
`Build Components -> Build UX Containers`

### Continuous Testing

As we are deploying multiple components (Batch Processes for training, Micro Services for integration and Micro Services for Sector Scoring) we also provide Consumer Driven Contract Tests. This strategy allows our developers and data scientists to communicate their intent via code in a testable and documented fashion within the pipeline. These consumer driven tests become a statement of expectations that will reflect how integration can be done in production. Each of the pipeline features are automatically tested through the pipeline, including:

* Unit
* Integration
* Functional
* Security (OWASP - In the Pipeline )
* Contract (Necessary to ensure model integration)
* Model Performance - Ensures model performance only improves as part of our CI process i.e. Models that degrade will fail the build process
* Experience Responsiveness
* Static Code Analysis (SonarQube)

### URLs and Work Products
__Note: the URLs are available within the deployment steps of their respective environments (dev, test, prod).__

## SOLUTION DESCRIPTION

Our solution is comprised of the following components, which are explained in detail in the solutions.pdf document.

* Data Collection & Data Processing Laboratory
* Model Training and Deployment
* Data Engineering and Model Optimizations
* Data Streaming & Persistence
* User Experience and Data Visualizations

Our solution makes use of AWS through a fully automated CI/CD pipeline orchestrated with Terraform as a configuration management tool.  Our selection of AWS and Terraform was driven by the ability to create environments seamlessly without being coupled to a given deployment target as well as the familiarity of these tools within USCIS.  As we are provisioning Docker Containers, we also have the ability seamlessly provision workloads within the cloud and have AWS’ Platform as a Service implementation (Elastic Container Service) handle our process scheduling.

__Stability:__ To ensure Stability:
Our platform leverages cloud optimized platform as a service techniques and built in telemetry support of the container platform.

__Security:__  Our application leverages AWS provided security implementations, along with Role-Based Access Controls.

__Maintainability:__ We utilized 3 core principles for maintainability:
* Simplicity- Dividing the architecture into distinct components that new developers and data scientists can get acclimated to the system easily.

* Reliability – All services, including models provide a series of telemetry to CloudWatch which can be used to get insight into transactions that are inflight and additionally, to measure model performance over time ensuring better maintenance of various components.

* Extensibility – Modular design easy enough to adjust to evolving needs and technology.

__Scalability:__  To ensure scalability, we have containerized all aspects of our solution and deploy them as independently scalable components with high availability requirements in two different availability zones. This along with Elastic Load Balancers, provides an unparalleled level of scalability and redundancy within the platform

## HIERARCHY LIST OF CORE TECHNOLOGIES

![Hierarchy](docs/images/hierarchy.png)

## High Level architecture

![Architecture](docs/images/highlevel-arch.png)
