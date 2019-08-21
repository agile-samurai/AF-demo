import React from 'react';
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import Typography from "@material-ui/core/Typography";
import * as _ from 'lodash';
import {makeStyles} from "@material-ui/styles";
import {connect} from "react-redux";
import Fade from "@material-ui/core/Fade";
import {loadData} from "../../reducers/trainingReducer";
import CircularProgress from "@material-ui/core/CircularProgress";
import {bindActionCreators} from "redux";
import InfiniteScroll from 'react-infinite-scroller';

const useStyles = makeStyles(theme => ({
  root: {
    padding: theme.spacing(3, 2),
    margin: "2rem 0"
  },
}));

const TrainingData = (props) => {
  const classes = useStyles();

  const load = (page) => props.loadData(page - 1);

  let trainingData = _.map(props.trainingData, (data, index) => (
    <Paper key={index} className={classes.root}>
      <Typography variant="h5" component="h3">
        Data point {index}
      </Typography>
      <Typography component="p">{data.key}</Typography>
    </Paper>
  ));

  return (
    <Grid container component="div" spacing={5} direction="column" justify="flex-start" alignItems="center"
          className={classes.root}>
      <Typography variant="h3">Training Data</Typography>
      <Fade in timeout={{enter: 50, exit: 50}}>
        <InfiniteScroll
          pageStart={0}
          loadMore={load}
          hasMore={props.currentPage < props.totalPages || false}
          loader={<CircularProgress key={0}/>}>
          {trainingData}
        </InfiniteScroll>
      </Fade>
    </Grid>
  )
};

const mapStateToProps = ({training}) => ({
  trainingData: training.trainingData,
  currentPage: training.pageInfo.number,
  totalPages: training.pageInfo.totalPages,
  loading: training.loading
});

const mapDispatchToProps = dispatch => bindActionCreators({loadData}, dispatch);

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(TrainingData)
