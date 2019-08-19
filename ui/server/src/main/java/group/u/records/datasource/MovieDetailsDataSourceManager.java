package group.u.records.datasource;

import group.u.records.service.dossier.MovieDetailsDataSource;

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
