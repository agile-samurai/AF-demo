# Part 1 - Factor 1: Models

To achieve the objectives of this challenge, our data science team has designed, engineered, and executed models using Jupyter as a platform for collaboration with integration into our delivery process through the uses of Continuous Delivery and Continuous Delivery practices.  notebooks and used CICD pipeline to train, integrate and deploy models into production environment and made them available as micro services for Visualization services.

Our data science process is comprised of 3 primary components:
* __Data Research & Exploration__ - Crafting a rich understanding of the data and opportunities within
* __Data Engineering__ -  Transforming, Ingesting, Enriching and preparing the data ready for automated training and deployment
* __Data Modeling__: To Designing and optimizing models for search, entity resolution, predictive redaction, and similarity.


(WRITE MORE HERE ABOUT HOLLISTIC APPROACH 2 to 3 paragraphs)

__Our AI/ML Execution and Analitical Approach__

Our team used Natural Language Processing (NLP) to classify unstructured text pulled from datasources. We performed standard preprocessing by removing stop words, lemmatizing, lower-casing, removing punctuation and extra whitespace. We used SpaCy to identify key verbs in sentences and check against manually defined verbs that better indicated capabilities, before allowing sentences into our corpus. This resulted in sets of word tokens for each company that we used to define company capabilities.

## Algorithmic selection

### Search Execution & Enhancements

To enable Fuzzy search and find similarity between movie descriptions, we  explored multi dimensional entity comparison algorithms that would best leverage the available data.  To validate our approach and provide feedback early, we explored the __[cosine similarity](https://en.wikipedia.org/wiki/Cosine_similarity)__ as a starting point but realized that its performance within this context was more suited to determining anagrams. We then explored other models (Jaro Winkler etc.) that produced varying results, some of which were aligned with our expectations. However, as we tuned out model hyper parameter and preprocessing selections, we saw issues with differentiation of string values.


Our final choice was to implement __[Levenshtein algorithm](https://en.wikipedia.org/wiki/Levenshtein_distance)__  as it closely matched what we needed to do and the results were within the expectations. Additionally we decided this algorithm is a best match because it is not only fast and effective but also since there is no input from end user to success of search.
Similarity Model:  To find movies which are similar and categorize them into a genre we have developed a Similarity model. This model looks at all movie plot descriptions, takes in words, generates vectors of the words used in plot summaries and then compares those vectors against another document’s vectors. This resulted in finding movies with plot summaries that are most similar to one another.
A key advantage of implementing this model is to enable an analyst to find linkages between dossiers by showing similar dossier types.  

We used Doc2Vec implementation of sentence modelling using Gensim as the library for implementation. Using this implementation we determined the genre listed for each film is the one that was primarily associated with it.  This is a key indication of success factor in implementing Doc2Vec which proved that our vectors have separated movies efficiently. Our approach was to measure vector distances between the document and the central location of the genre and try to minimize the distance by adjusting the hyper parameters resulting in tighter clusters. Each vector that was created has 300 dimensions. We used all 300 dimensions and averaged the distance across all of them within a genre for grouping similar movies.

Auto Predictive Redaction Model:  A core objective of this challenge is to ensure security of dossiers and the information they contain. We utilized Amazon’s CloudHSM to implement a cryptographic delete process. Taking the objective of security and privacy concerns to the next evolution, we implemented an “Auto Predictive Redaction Model” which utilizes Named Entity Recognition principles to auto highlight and redact information in dossiers. The advantage of implementing this is to enable business supervisors share redacted dossier information with other internal or external resources as needed, similar to a FOIA process implemented in various Federal agencies.

We identified person, organization entities and used Spacy, which is trained on English Wikipedia to provide highlighted/redacted summaries in a dossier.
Data Engineering: Our team used Natural Language Processing (NLP) to classify unstructured text pulled from data sources. We performed standard preprocessing by removing stop words, lemmatizing, lower-casing, removing punctuation and extra whitespace.

Data Sets: We collected data from a myriad of sources including the data provided by USCIS. Additionally we pulled data from IMDB, OMDB, Twitter, Wikipedia and Amazon Reviews. (Note: we used the publicly available data set of IMDB which is allowed under their terms of service. LINK TO THE IMDB TOS). We used a combination of Python and Java to parse data sources.

* __Labeling__ We leveraged Doc2Vec analysis, an unsupervised learning process, to explore hidden data patterns within our data set. This method allows to easily draw inferences within data sets to discover the strongest relationships among movies, as well as new entities based on patterns within the data. This method does not require us to label training data sets. We trained our models to minimize distances between movies within clusters based on vectorized text of plot summaries. We created a function to place movies into into clusters based on the discovered dimensions.

* __Optimization__ WRITE SOME THING HERE

__Training__ Answered in Logic & Innovation below under “Multiple ways to train models”.
Integrating, Maintaining & Validating NLP/AI/ML models:
Integration and Maintenance: Addressed in “Model Maintenance & Integration through automated CI/CD”.
Validation:  NEED TO WRITE SOME THING HERE

## Model Maintenance & Integration using CI/CD:

#### Integration
For development processes, we used Jupyter to train models locally and then merge the models into GitHub following our development process. Once merged those models are then associated with a job in CICD. This job prepares and combines data for modelling. The output of that job is a pickled binary file which is passed to another CICD job that uses data to build Doc2Vec models. These models are then passed to another CICD job which deploys them into production and provide an API end point using micro services that the rest of the application can consume.
We created a “Data Scientist Lab” which enabled data scientists to easily train models and test algorithms using Jupyter.

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
