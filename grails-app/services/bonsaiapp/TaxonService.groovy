package bonsaiapp

import grails.gorm.services.Service

@Service(Taxon)
interface TaxonService {

    Taxon get(Serializable id)

    List<Taxon> list(Map args)

    Long count()

    void delete(Serializable id)

    Taxon save(Taxon taxon)

}