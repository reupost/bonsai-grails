package bonsaiapp

import grails.gorm.services.Service

@Service(Pic)
interface IPicService {

    Pic get(Serializable id)

    List<Pic> list(Map args)

    List<Pic> listAll(Map args)

    Long count()

    void delete(Serializable id)

    Pic save(Pic pic)

}