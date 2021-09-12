package bonsaiapp

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class PicServiceSpec extends Specification {

    PicService picService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Pic(...).save(flush: true, failOnError: true)
        //new Pic(...).save(flush: true, failOnError: true)
        //Pic pic = new Pic(...).save(flush: true, failOnError: true)
        //new Pic(...).save(flush: true, failOnError: true)
        //new Pic(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //pic.id
    }

    void "test get"() {
        setupData()

        expect:
        picService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Pic> picList = picService.list(max: 2, offset: 2)

        then:
        picList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        picService.count() == 5
    }

    void "test delete"() {
        Long picId = setupData()

        expect:
        picService.count() == 5

        when:
        picService.delete(picId)
        sessionFactory.currentSession.flush()

        then:
        picService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Pic pic = new Pic()
        picService.save(pic)

        then:
        pic.id != null
    }
}
