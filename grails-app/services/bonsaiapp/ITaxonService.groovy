package bonsaiapp

import grails.gorm.services.Service

@Service(Taxon)
interface ITaxonService {

    Taxon get(Serializable id)

    List<Taxon> list(Map args)

    List<Taxon> listAll(Map args) //TODO until we have a proper search workflow...

    Long count()

    void delete(Serializable id)

    Taxon save(Taxon taxon)

}