package bonsaiapp

class InputCleaner {

    static String getOnlyLettersAndNumbers(String input)  {
        def matcher = input =~ /\p{Alnum}/
        return matcher.text
    }
}
