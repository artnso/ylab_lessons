package Transliterator;

import java.util.HashMap;
import java.util.Map;

public class TransliteratorImpl implements Transliterator{
    private Map<String, String> dictionary;

    TransliteratorImpl(){
        String russianLetters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЫЬЪЭЮЯ";
        String englishTranslitLetters = "A,B,V,G,D,E,E,ZH,Z,I,I,K,L,M,N,O,P,R,S,T,U,F,KH,TS,CH,SH,SHCH,Y,,IE,E,IU,IA";
        dictionary = new HashMap<>();
        String[] englishTranslitArr = englishTranslitLetters.split(",");
        String[] russianLettersArr = russianLetters.split("");
        for (int i = 0; i < russianLetters.length(); i++){
            dictionary.put(russianLettersArr[i], englishTranslitArr[i]);
        }
//        Для транслитерации одновременно прописных и строчных букв - код ниже
//        Строчные русские буквы будут транслитерироваться в прописные английские
//        for (int i = 0; i < russianLetters.length(); i++){
//            dictionary.put(russianLettersArr[i].toUpperCase(), englishTranslitArr[i]);
//        }

    }

    @Override
    public String tansliterate(String source) {
        if (source == null){
            throw new NullPointerException("Пустое значение указателя на строку!");
        }
        String result = "";
        for (int i = 0; i < source.length(); i++){
            String letter = source.substring(i, i + 1);
            result += dictionary.getOrDefault(letter, letter);
        }
        return result;
    }
}
