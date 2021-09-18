package bonsaiapp

import grails.gorm.services.Service

@Service(EntityPics)
interface EntityPicsService {

    EntityPics get(Serializable id)

    List<EntityPics> list(Map args)

    Long count()

    void delete(Serializable id)

    EntityPics save(EntityPics entityPics)

}