import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import {BrowserRouter} from "react-router-dom";

import {applyMiddleware, combineReducers, compose, createStore} from 'redux'
import {Provider} from 'react-redux'
import loginReducer from './reducers/loginReducer'
import trainingReducer from './reducers/trainingReducer'
import {createLogger} from 'redux-logger';
import {composeWithDevTools} from "redux-devtools-extension";
import thunk from 'redux-thunk';

const logger = createLogger();
let middleware = [thunk];

if (process.env.NODE_ENV === 'development') {
  middleware.push(logger);
}

const composeEnhancers = composeWithDevTools();

const store = createStore(
  combineReducers({loginReducer: loginReducer, training: trainingReducer}), /* preloadedState, */
  composeWithDevTools(applyMiddleware(...middleware))
);

ReactDOM.render(
  <Provider store={store}>
    <BrowserRouter>
      <App/>
    </BrowserRouter>
  </Provider>, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
