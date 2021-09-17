package bonsaiapp

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class BonsaiServiceSpec extends Specification {

    IBonsaiService bonsaiService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Bonsai(...).save(flush: true, failOnError: true)
        //new Bonsai(...).save(flush: true, failOnError: true)
        //Bonsai bonsai = new Bonsai(...).save(flush: true, failOnError: true)
        //new Bonsai(...).save(flush: true, failOnError: true)
        //new Bonsai(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //bonsai.id
    }

    void "test get"() {
        setupData()

        expect:
        bonsaiService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Bonsai> bonsaiList = bonsaiService.list(max: 2, offset: 2)

        then:
        bonsaiList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        bonsaiService.count() == 5
    }

    void "test delete"() {
        Long bonsaiId = setupData()

        expect:
        bonsaiService.count() == 5

        when:
        bonsaiService.delete(bonsaiId)
        sessionFactory.currentSession.flush()

        then:
        bonsaiService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Bonsai bonsai = new Bonsai()
        bonsaiService.save(bonsai)

        then:
        bonsai.id != null
    }
}
