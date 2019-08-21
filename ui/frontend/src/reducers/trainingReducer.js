import axios from "axios";

export const GET_TRAINING_DATA_REQUESTED = 'GET_TRAINING_DATA_REQUESTED';
export const GET_TRAINING_DATA_SUCCESSFUL = 'GET_TRAINING_DATA_SUCCESSFUL';
export const GET_TRAINING_DATA_FAILED = 'GET_TRAINING_DATA_FAILED';
const fetchTrainingDataRequested = () => ({
  type: GET_TRAINING_DATA_REQUESTED
});
const fetchTrainingDataSucceeded = (data) => ({
  type: GET_TRAINING_DATA_SUCCESSFUL,
  data
});
const fetchTrainingDataFailed = (error) => ({
  type: GET_TRAINING_DATA_FAILED,
  error
});

const initialState = {
  trainingData: [],
  loading: false,
  error: {},
  pageInfo: {
    number: 0,
    totalPages: 1
  }
};


export default (state = initialState, action) => {
  switch (action.type) {
    case GET_TRAINING_DATA_REQUESTED:
      return {
        ...state,
        loading: true
      };
    case GET_TRAINING_DATA_SUCCESSFUL:
      return {
        ...state,
        trainingData: [...state.trainingData, ...action.data.content],
        pageInfo: action.data,
        loading: false
      }
    case GET_TRAINING_DATA_FAILED:
      return {
        ...state,

      }
      break;
    default:
      return state;
  }
}

export const loadData = (page) => {
  console.log('Redux page: ', page);
  console.log("Wat about here");
  return (dispatch) => {
    console.log("WE in there");
    dispatch(fetchTrainingDataRequested());
    axios.get('/api/training', {
      params: {cursor: page},
      auth: {  // TODO remove
        username: 'business-user',
        password: 'password'
      }
    }).then(response => {
      dispatch(fetchTrainingDataSucceeded(response.data))
    })
  }
};