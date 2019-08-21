package group.u.records.web.core;

import group.u.records.ds.training.TrainingData;
import group.u.records.repository.TrainingDataRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TrainingDataControllerTest {

    @Mock
    TrainingDataRepository repository;
    @InjectMocks
    TrainingDataController controller;

    @Test
    public void shouldFetchFirstTrainingDataByPage() {
        final ArgumentCaptor<PageRequest> requestCaptor = ArgumentCaptor.forClass(PageRequest.class);
        final TrainingData expected = new TrainingData("id");
        when(repository.findAll(requestCaptor.capture())).thenReturn(new PageImpl<>(newArrayList(expected)));

        final ResponseEntity<Page<TrainingData>> entity = controller.getAll(0);

        final PageRequest captorValue = requestCaptor.getValue();
        assertEquals(0, captorValue.getPageNumber());
        assertEquals(100, captorValue.getPageSize());

        assertEquals(newArrayList(expected), Objects.requireNonNull(entity.getBody()).getContent());
    }
}