# Part 1 - Factor 1: Models

To achieve the objectives of this challenge, our data science team has designed, engineered, and executed models using Jupyter as a platform for collaboration, and integration into our delivery process through Continuous Delivery and Continuous Delivery practices. The data science components are integrated into the CICD pipeline, which trains and deploys models into development, testing, and production environments. The data science components are then available as micro-services for visualization and other front-end consumption.

Our data science process is comprised of three primary processes:
* __Data Research & Exploration__ - Crafting a rich understanding of the data and opportunities for insight
* __Data Engineering__ - Transforming, ingesting, enriching and preparing the data to enable automated training and deployment
* __Data Modeling__ - Designing and optimizing models for search, entity resolution, predictive redaction, and similarity


At U.Group we believe strongly in customer centricity while delivering value early and often.  This core value not only informs how we approach a given problem, but also how we organize our teams to ensure successful delivery at scale.  While working through the challenge our Developers, Data Scientist practiced multi-disciplinary paired programming to model the business domains and accelerate the path to production with a shared understanding of the needs and concerns.  

In addition to direct collaboration, we have additionally extended our Continuous Integration and Delivery process to include the combined deployment of our Data Models and Integration code into a distributed platform that is consistently audited and versioned.  This active collaboration helps shape our design as well as provides an opportunity for active prioritization and quick feedback loops while remaining customer centric.

__Our AI/ML Execution and Analytical Approach__

Our team used Natural Language Processing (NLP) to classify unstructured text pulled from multiple data sources. We performed standard preprocessing by removing stop words, lemmatizing, lower-casing, removing punctuation and extra whitespace. We used the `nltk` library to identify word tokens that form the basis of our sentence embedding model.

## Algorithmic selection

### Search Execution & Enhancements
To enable fuzzy search and find similarity between movie descriptions, we  explored multi dimensional entity comparison algorithms that would best leverage the available data.
#### Fuzzy Search
To validate our approach and provide rapid feedback, we explored multiple text distance algorithms. We initially explored __[cosine similarity](https://en.wikipedia.org/wiki/Cosine_similarity)__ as a starting point but realized that its performance within this context was not ideal for what we had discussed as a fuzzy search. We continued explored other distance metrics (Jaro-Winkler, etc.) that produced varying results, some of which were aligned with our expectations. However, as we tuned our model hyperparameters and preprocessing selections, we saw issues with differentiation of string values.

We chose to implement the __[Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance)__  algorithm, as it works well with misspellings and different representations of names. Levenshtein distance quickly and deterministically measures the overall difference of an input query from any entry in our data, so that strings that are mostly the same but with a few characters different are identified as being close. Where cosine similarity requires a normalized dataset, the Levenshtein distance executes a fuzzy search without training or additional user input, such as labeling.

#### Document Similarity

To find movies which are similar and categorize them into a genre we have developed a Similarity model.
This model looks at all movie plot descriptions derived from OMDB, tokenizes those sentences using the NLP preparation steps outlined above, and generates vectors of the words used in plot summaries using the Doc2Vec model. These vectors can then be used to compare movies against one another.
This resulted in finding movies with plot summaries that are most similar to one another.
A key advantage of implementing this model is to enable an analyst to find linkages between dossiers by showing similar dossier types.  

We used the Doc2Vec implementation in the Gensim library. Using this implementation we determined the genre listed for each film is the one that was primarily associated with it.  This is a key indication of success factor in implementing Doc2Vec which proved that our vectors have separated movies efficiently. Our approach was to measure vector distances between the document and the central location of the genre and try to minimize the distance by adjusting the hyperparameters resulting in tighter clusters. Each vector that was created has 300 dimensions. We used all 300 dimensions and averaged the distance across all of them within a genre for grouping similar movies.

#### Auto Predictive Redaction Model
A core objective of this challenge is to ensure security of dossiers and the information they contain.
We utilized Amazon’s CloudHSM to implement a cryptographic delete process.
Taking the objective of security and privacy concerns to the next evolution, we implemented an “Auto Predictive Redaction Model” which utilizes Named Entity Recognition principles to auto highlight and redact information in dossiers.
The advantage of implementing this is to enable business supervisors share redacted dossier information with other internal or external resources as needed, similar to a FOIA process implemented in various Federal agencies.

We identified person, organization entities and used Spacy, which is trained on English Wikipedia to provide highlighted/redacted summaries in a dossier.
Data Engineering: Our team used Natural Language Processing (NLP) to classify unstructured text pulled from data sources. We performed standard preprocessing by removing stop words, lemmatizing, lower-casing, removing punctuation and extra whitespace.

Data Sets: We collected data from a myriad of sources including the data provided by USCIS. Additionally we pulled data from IMDB, OMDB, Twitter, Wikipedia and Amazon Reviews. (Note: we used the publicly available data set of IMDB which is allowed under their terms of service. LINK TO THE IMDB TOS). We used a combination of Python and Java to parse data sources.

* __Labeling__ We leveraged Doc2Vec analysis, an unsupervised learning process, to explore hidden data patterns within our data set. This method allows us to easily draw inferences within data sets to discover the strongest relationships among movies, as well as new entities based on patterns within the data. This method does not require us to label training data sets.

* __Optimization__ To create a more responsive and rich user experience, we leveraged a series of runtime caching techniques to ensure that data was accessible with minimal seek time.  

* __Training__ Answered in Logic & Innovation below under “Multiple ways to train models”.
Integrating, Maintaining & Validating NLP/AI/ML models:
Integration and Maintenance: Addressed in “Model Maintenance & Integration through automated CI/CD”.

* __Validation__ was conducted leveraging various data sources for entity recognition on a known and dataset to ensure consistency of execution and reuslts.  

## Model Maintenance & Integration using CI/CD:

#### Integration
We created a “Data Scientist Lab” which enabled data scientists to easily train models and test algorithms using Jupyter.
For development processes, we used Jupyter notebooks to perform exploratory analysis and develop models locally. This work was then implemented in a set of scripts and a RESTful API using the `hug` Python library, all of which was containerized using Docker. This allows the CICD pipeline to manage all of the stages of the data lifecycle, from ingestion, then handing that data off to the model training job, which creates a versioned model that is handed off to the REST API microservice. All of the artifacts from each stage of this process are managed by the CICD pipeline and stored to S3.

#### Maintenance
Our design’s architectural flexibility offers an organization to swap models in and out as needed. Our analysis pipeline enables Continuous Delivery through well factored code and clear separation of responsibilities.  On startup, a training job will spin up a docker container, load data from S3, clean & preprocess it, retrain models, and save the model in to a repository such as Artifactory or S3 so it can be versioned and documented. Our CI/CD pipeline monitors S3 for version changes and automatically kicks off a build and validation process that automatically deploys the model to ECS for Automated Sign-off and compliance scanning.

To maintain CI/CD principles, our entire eco-system is version controlled and built from source in a reproducible fashion within a CI pipeline.  This process includes the training of models, packaging of their dependencies and finally the containerization of the models all tied to an auditable source control commit.  Subsequent changes to the model are also version controlled to allow for tracability and operational monitoring in each of our deployed environments.

#### Deployment
Before deploying the model to production pipeline we check the validity of the model by comparing metrics between the deployed model and the current version. Existing models are retired from production and new models are deployed if, on comparison of metrics, we see improvements in vector distances between the movie and the central location of the genre.

#### Continuous Integration
There are many features that we built as part of this challenge that illustrate innovation to exceed the requirements of this challenge.
Multiple ways to train models: We implemented 3 different ways to train the models to increase model accuracy and speed to delivery.

## Model evolution
To allow for continuous feedback, we have adopted a process of automated and continuous training, which allows us to retrain models automatically as part of our CI process when changes are detected in the data used for training or the source code that govern the model execution process.  This allows for continuous feedback within our build process and makes it visible as part of our CI process. We integrated training set creation and model training as a part of the CI/CD pipeline. The models are trained each time we commit and build. This enables us to train models in a similar fashion to testing and deploying code. The advantage of this approach is the ability to test micro-services code and the data model output together in real time while building and deploying the final output to servers. We successfully architected our data models to be fully automated through the CI/CD pipeline which provides automated testing and deployment of the actual models.

Train on Jupyter: We created a Jupyter notebook and provisioned capabilities to train models on it, thus creating a “data scientist lab” which lets data scientists train models easily and independently during the analysis phase.

Train locally: As development progressed, we enabled our developers to train models locally in their dev environment which enables local testing of changes, including models.

### Human Centered Data Driven Models
Data Models for Human Centered Design:  While analyzing movies, our data involved substantial number of correlated variables. For e.g. each Vector has over 300 dimensions to analyze and correlate. Visualizing 300 dimensions when checking relationships between dimensions is not optimal when considered human centered design principles. As such we used Principal Component Analysis to reduce dimensions from 300 to 2 but still contain most of the information from the 300 dimensions.  This enabled us to see and explore clusters and data in a more meaningful fashion by non-data scientists.


Auto Predictive Redaction using NER and Multi-Dimensional Analytical Models: A significant innovation in our data science models is the ability to predict entities and train the models to enable auto predictive redaction of dossiers.
