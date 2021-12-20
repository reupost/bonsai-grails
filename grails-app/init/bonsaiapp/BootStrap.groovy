package bonsaiapp

import grails.gorm.transactions.Transactional

class BootStrap {

    def init = { servletContext ->
        //addTestUser()
    }
    def destroy = {
    }
//
//    @Transactional
//    void addTestUser() {
//        def adminRole = new SpringRole(authority: 'ROLE_ADMIN').save()
//
//        def testUser = new SpringUser(username: 'me', password: 'password').save()
//
//        SpringUserSpringRole.create testUser, adminRole
//
//        SpringUserSpringRole.withSession {
//            it.flush()
//            it.clear()
//        }
//
//        assert SpringUser.count() == 1
//        assert SpringRole.count() == 1
//        assert SpringUserSpringRole.count() == 1
//    }
}
