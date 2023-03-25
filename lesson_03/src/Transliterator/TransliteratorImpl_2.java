package Transliterator;

import java.util.Map;

public class TransliteratorImpl_2 implements Transliterator {
    private final Map<Character, String> dictionary;

    public TransliteratorImpl_2() {
        dictionary = Map.ofEntries(
                Map.entry('А', "A"), Map.entry('Б', "B"), Map.entry('В', "V"),
                Map.entry('Г', "G"), Map.entry('Д', "D"), Map.entry('Е', "E"),
                Map.entry('Ё', "E"), Map.entry('Ж', "ZH"), Map.entry('З', "Z"),
                Map.entry('И', "I"), Map.entry('Й', "I"), Map.entry('К', "K"),
                Map.entry('Л', "L"), Map.entry('М', "M"), Map.entry('Н', "N"),
                Map.entry('О', "O"), Map.entry('П', "P"), Map.entry('Р', "R"),
                Map.entry('С', "S"), Map.entry('Т', "T"), Map.entry('У', "U"),
                Map.entry('Ф', "F"), Map.entry('Х', "KH"), Map.entry('Ц', "TS"),
                Map.entry('Ч', "CH"), Map.entry('Ш', "SH"), Map.entry('Щ', "SHCH"),
                Map.entry('Ы', "Y"), Map.entry('Ь', ""), Map.entry('Ъ', "IE"),
                Map.entry('Э', "E"), Map.entry('Ю', "IU"), Map.entry('Я', "IA")
        );
    }

    @Override
    public String tansliterate(String source) {
        if (source == null){
            throw new NullPointerException("Пустое значение указателя на строку!");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); i++){
            Character letter = source.charAt(i);
            sb.append(dictionary.getOrDefault(letter, letter.toString()));
        }
        return sb.toString();
    }
}
