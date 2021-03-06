package group.u.records.service;

import org.junit.Before;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class DataImportServiceTest {
    private DataScienceAPIService dataScienceAPIService;

    @Before
    public void setUp() {
        dataScienceAPIService = mock(DataScienceAPIService.class);
        doNothing().when(dataScienceAPIService).waitForAPIReady();
    }
}
