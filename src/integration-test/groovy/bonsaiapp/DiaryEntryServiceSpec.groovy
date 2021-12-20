package bonsaiapp


import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class DiaryEntryServiceSpec extends Specification {

    DiaryEntryService diaryEntryService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new DiaryEntry(...).save(flush: true, failOnError: true)
        //new DiaryEntry(...).save(flush: true, failOnError: true)
        //DiaryEntry diaryEntry = new DiaryEntry(...).save(flush: true, failOnError: true)
        //new DiaryEntry(...).save(flush: true, failOnError: true)
        //new DiaryEntry(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //diaryEntry.id
    }

    void "test get"() {
        setupData()

        expect:
        diaryEntryService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<DiaryEntry> diaryEntryList = diaryEntryService.list(max: 2, offset: 2)

        then:
        diaryEntryList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        diaryEntryService.count() == 5
    }

    void "test delete"() {
        Long diaryEntryId = setupData()

        expect:
        diaryEntryService.count() == 5

        when:
        diaryEntryService.delete(diaryEntryId)
        sessionFactory.currentSession.flush()

        then:
        diaryEntryService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        DiaryEntry diaryEntry = new DiaryEntry()
        diaryEntryService.save(diaryEntry)

        then:
        diaryEntry.id != null
    }
}
