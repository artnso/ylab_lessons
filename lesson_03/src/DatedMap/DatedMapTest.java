package DatedMap;

public class DatedMapTest {
    public static void main(String[] args) {
        DatedMap datedMap = new DatedMapImpl();
        datedMap.put("Key1", "Value1");
        System.out.println(datedMap.getKeyLastInsertionDate("Key1")); // Дата создания ключа 1
        System.out.println(datedMap.getKeyLastInsertionDate("Key2")); // null - т.к. ключ 2 еще не создан
        datedMap.put("Key2", "Value2");
        System.out.println(datedMap.getKeyLastInsertionDate("Key2")); // Дата создания ключа 2
        datedMap.put("Key3", "Value3");
        System.out.println(datedMap.getKeyLastInsertionDate("Key3")); // Дата создания ключа 3
        datedMap.remove("Key3");
        System.out.println(datedMap.getKeyLastInsertionDate("Key5")); // null - т.к. ключ 3 удален
    }
}
