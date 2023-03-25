package Transliterator;

import java.util.HashMap;
import java.util.Map;

public class TransliteratorImpl implements Transliterator{
    private Map<Character, String> dictionary;

    TransliteratorImpl(){

        String russianLetters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЫЬЪЭЮЯ";
        String englishTranslitLetters = "A,B,V,G,D,E,E,ZH,Z,I,I,K,L,M,N,O,P,R,S,T,U,F,KH,TS,CH,SH,SHCH,SHCH,,IE,E,IU,IA";
        dictionary = new HashMap<>();
        String[] englishTranslitArr = englishTranslitLetters.split(",");
        for (int i = 0; i < russianLetters.length(); i++){
            dictionary.put(russianLetters.charAt(i), englishTranslitArr[i]);
        }

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
