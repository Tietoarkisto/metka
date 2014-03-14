package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.repository.FileRepository;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileService {
    @Autowired
    private FileRepository repository;

    public Integer addFile(TransferObject to) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public String saveFile(MultipartFile file, String fileName, Integer id) throws IOException {
        File dir = new File("/usr/share/metka/files/study/"+id);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File multipartFile = new File("/usr/share/metka/files/study/"+id+"/"+fileName);
        file.transferTo(multipartFile);
        return multipartFile.getCanonicalPath();
    }

    /**
     * Request adding a file with given path to database.
     * Calls for adding a new file to database which will create a new Revisionable entry and a revision for that entry.
     * After this method returns there should be a new File-revisionable with a revision pointing to given path with state DRAFT.
     * Returns a representation of a row ready to be inserted into a CONTAINER json on client.
     * @param path Path to a newly added file
     * @param targetId Id of the revisionable that should refer this file.
     * @return
     */
    public String initNewFile(String path, Integer targetId) throws IOException {
        RevisionData revision = repository.newFileRevisionable(path);

        // TODO: Form file row

        return null;
    }
}
