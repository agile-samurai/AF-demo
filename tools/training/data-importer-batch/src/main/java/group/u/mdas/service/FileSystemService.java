package group.u.mdas.service;

import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.model.DataSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static java.util.UUID.randomUUID;

@Component
public class FileSystemService {

    private String dataPath;
    private Logger logger = LoggerFactory.getLogger(FileSystemService.class);

    public FileSystemService(@Value("${platform.training.data.path}") String dataPath) {
        this.dataPath = dataPath;
    }

    public void save(DataSourceCategory dataSourceCategory,
                     String content,
                     CompanyIdentifier companyIdentifier) {


        try{
            String directoryName = dataPath + "/" + dataSourceCategory.getLabel() + "/"
                    + companyIdentifier.getSymbol();
            File directory = new File(directoryName);

            if (!directory.exists()) {
                directory.mkdirs();
            }


            File file = new File(directoryName + "/"
                    + randomUUID().toString() + ".txt");
            file.createNewFile();
            logger.debug(file.getAbsoluteFile().getAbsolutePath());
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))){
                writer.println(content);

            }


        } catch (IOException e) {
            logger.error("File exception", e);
        }
    }
}
