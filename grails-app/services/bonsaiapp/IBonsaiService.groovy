package bonsaiapp

import grails.gorm.services.Service

@Service(Bonsai)
interface IBonsaiService {

    Bonsai get(Serializable id)

    List<Bonsai> list(Map args)


    Long count()

    void delete(Serializable id)

    Bonsai save(Bonsai bonsai)

}