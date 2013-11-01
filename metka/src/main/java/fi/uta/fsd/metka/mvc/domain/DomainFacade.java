package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.entity.MaterialEntity;
import fi.uta.fsd.metka.data.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/1/13
 * Time: 10:07 AM
 */
@Component("domainFacade")
public class DomainFacade {

    @Autowired
    MaterialRepository materialRepository;

    public MaterialEntity createMaterial(MaterialEntity material) {
        if(material.getId() == null) {
            material.setId(materialRepository.create(material));
        }
        return material;
    }

    public List<MaterialEntity> findAllMaterials() {
        return materialRepository.findAll();
    }
}
