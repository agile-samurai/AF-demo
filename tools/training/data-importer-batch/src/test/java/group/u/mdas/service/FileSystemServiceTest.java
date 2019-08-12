package group.u.mdas.service;

import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.model.DataSourceCategory;
import org.junit.Test;

import static java.util.UUID.randomUUID;

public class FileSystemServiceTest {

    @Test
    public void shouldWriteToFileSystem(){
        FileSystemService service = new FileSystemService("test-data");
        String identifier = randomUUID().toString().substring(0, 4);
        service.save(DataSourceCategory.SEC_FILING, "fake", new CompanyIdentifier(identifier, "Microsoft", "cats", "Industry"));
    }
}
