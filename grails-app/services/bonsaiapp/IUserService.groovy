package bonsaiapp

import grails.gorm.services.Service

@Service(User)
interface IUserService {

    User get(Serializable id)

    List<User> list(Map args)

    List<User> listAll(Map args) //TODO until we have a proper search workflow...

    Long count()

    void delete(Serializable id)

    User save(User user)
}