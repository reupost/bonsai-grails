package bonsaiapp

class InputCleaner {

    public String getOnlyLettersAndNumbers(String input)  {
        def matcher = input =~ /\p{Alnum}/
        return matcher.text
    }
}
