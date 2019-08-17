package group.u.records.config;

import group.u.records.service.MovieDetailsDataSource;

import java.util.List;

public class MovieDetailsDataSourceManager {
    private List<MovieDetailsDataSource> dataSource;

    public List<MovieDetailsDataSource> getDataSources() {
        return dataSource;
    }

    public MovieDetailsDataSourceManager(List<MovieDetailsDataSource> dataSource) {
        this.dataSource = dataSource;
    }
}
