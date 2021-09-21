package bonsaiapp

class Copy {

    static GroovyObject copy(GroovyObject from, GroovyObject to) {
        from?.properties?.findAll { !(it.key =~ ~/(meta)?[cC]lass/) }?.each {
            try {
                to.setProperty(it.key, from[it.key])
            }
            catch (MissingPropertyException ex) {}
        }
        to
    }

    static <T> T copy(GroovyObject from, Class<T> to) {
        copy(from, to.newInstance())
    }

}
