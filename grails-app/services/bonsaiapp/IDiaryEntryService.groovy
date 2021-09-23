package bonsaiapp

import grails.gorm.services.Service

@Service(DiaryEntry)
interface IDiaryEntryService {

    DiaryEntry get(Serializable id)

    List<DiaryEntry> list(Map args)

    Long count()

    void delete(Serializable id)

    DiaryEntry save(DiaryEntry diaryEntry)

}