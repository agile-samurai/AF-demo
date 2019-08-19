package group.u.records.web;

import group.u.records.security.MasterDossierService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@Controller
public class FileController {
    private Logger logger = LoggerFactory.getLogger(FileController.class);
    private MasterDossierService dossierService;

    @Autowired
    public FileController(MasterDossierService dossierService ) {
        this.dossierService = dossierService;
    }



    @PostMapping("/files/{dossierId}")
    public ResponseEntity upload(@RequestParam("file") MultipartFile file,
                                 @PathVariable UUID dossierId) throws IOException {

        dossierService.saveFile(dossierId, file);
        logger.debug("File Uploaded." );
        return ok().build();
    }

    @GetMapping("/files/{dossierId}/{fileId}")
    public void downloadFile(@PathVariable UUID dossierId,
                             @PathVariable UUID fileId,
                             HttpServletResponse response) throws IOException {

        byte[] file = dossierService.getFile(dossierId, fileId);
        IOUtils.write(file, response.getOutputStream());
        response.getOutputStream().flush();
    }
}
