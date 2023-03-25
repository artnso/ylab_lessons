package DatedMap;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DatedMapImpl implements DatedMap {
    // Хранения ключей, данных и даты создания организовано в 2-х объектах Map
    // возможно организовать через объекты List с меньшим потреблением памяти
    private Map<String, String> keyValuesMap;
    private Map<String, Date> keyDatesMap;

    public DatedMapImpl() {
        keyValuesMap = new HashMap<>();
        keyDatesMap = new HashMap<>();
    }

    @Override
    public void put(String key, String value) {
        keyValuesMap.put(key, value);
        keyDatesMap.put(key, new Date());
    }

    @Override
    public String get(String key) {
        return keyValuesMap.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return keyValuesMap.containsKey(key);
    }

    @Override
    public void remove(String key) {
        keyValuesMap.remove(key);
        keyDatesMap.remove(key);

    }
    @Override
    public Set<String> keySet() {
        return keyValuesMap.keySet();
    }

    @Override
    public Date getKeyLastInsertionDate(String key) {
        return keyDatesMap.get(key);
    }
}
