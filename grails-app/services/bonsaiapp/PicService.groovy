package bonsaiapp

import grails.gorm.services.Service

@Service(Pic)
interface PicService {

    Pic get(Serializable id)

    List<Pic> list(Map args)

    Long count()

    void delete(Serializable id)

    Pic save(Pic pic)

}