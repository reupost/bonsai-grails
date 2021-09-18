package bonsaiapp

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class EntityPicsServiceSpec extends Specification {

    EntityPicsService entityPicsService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new EntityPics(...).save(flush: true, failOnError: true)
        //new EntityPics(...).save(flush: true, failOnError: true)
        //EntityPics entityPics = new EntityPics(...).save(flush: true, failOnError: true)
        //new EntityPics(...).save(flush: true, failOnError: true)
        //new EntityPics(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //entityPics.id
    }

    void "test get"() {
        setupData()

        expect:
        entityPicsService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<EntityPics> entityPicsList = entityPicsService.list(max: 2, offset: 2)

        then:
        entityPicsList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        entityPicsService.count() == 5
    }

    void "test delete"() {
        Long entityPicsId = setupData()

        expect:
        entityPicsService.count() == 5

        when:
        entityPicsService.delete(entityPicsId)
        sessionFactory.currentSession.flush()

        then:
        entityPicsService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        EntityPics entityPics = new EntityPics()
        entityPicsService.save(entityPics)

        then:
        entityPics.id != null
    }
}
