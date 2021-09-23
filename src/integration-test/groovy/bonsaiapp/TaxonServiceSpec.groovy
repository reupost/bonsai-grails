package bonsaiapp

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class TaxonServiceSpec extends Specification {

    ITaxonService taxonService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Taxon(...).save(flush: true, failOnError: true)
        //new Taxon(...).save(flush: true, failOnError: true)
        //Taxon taxon = new Taxon(...).save(flush: true, failOnError: true)
        //new Taxon(...).save(flush: true, failOnError: true)
        //new Taxon(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //taxon.id
    }

    void "test get"() {
        setupData()

        expect:
        taxonService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Taxon> taxonList = taxonService.list(max: 2, offset: 2)

        then:
        taxonList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        taxonService.count() == 5
    }

    void "test delete"() {
        Long taxonId = setupData()

        expect:
        taxonService.count() == 5

        when:
        taxonService.delete(taxonId)
        sessionFactory.currentSession.flush()

        then:
        taxonService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Taxon taxon = new Taxon()
        taxonService.save(taxon)

        then:
        taxon.id != null
    }
}
