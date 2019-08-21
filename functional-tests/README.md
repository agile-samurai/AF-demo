Getting Started
---------------

To run the tests, follow these steps:

 1. Go to the `functional-test` directory
 2. Run `npm install`
 3. Run `npm run webdriver:update`
 4. Run `npm run protractor:local` to run the tests locally. This will run against the application locally at `http://localhost:9091/`
 5. Run `npm run protractor:baseUrl <targetUrl>` where the targetUrl is the URL you want to run tests against. 
 The syntax looks like this `npm run protractor:baseUrl www.example.com`.
 
 
 Framework Architecture
 ----------------------
 - This framework uses Protractor and Jasmine. The tests are written in Typescript and follow the Page Object Model.
 
 - Functional Tests live under the `tests` directory.
 
 