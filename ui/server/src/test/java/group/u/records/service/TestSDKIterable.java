package group.u.records.service;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;

import java.util.Iterator;
import java.util.List;

public class TestSDKIterable<S3Object> implements SdkIterable<S3Object> {
    private List list;

    public TestSDKIterable(List list) {
        this.list = list;
    }

    @Override
    public Iterator<S3Object> iterator() {
//        S3Object bje = mock(S3Object.class);
        return list.iterator();
    }
}
