package FileSort;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Sorter {
    private final int chunkLength;
    public Sorter() {
        // длина массива - доступная память деленная на 3
        Long availableMemory = Runtime.getRuntime().maxMemory();
        // расчет 4 * 300 - 4 байта под long и памяти брать в 300 раз меньше
        if (availableMemory / (4 * 300) < Integer.MAX_VALUE) {
            chunkLength = (int)(availableMemory / (4 * 300));
        } else {
            chunkLength = 1_00_000;
            // вариант, когда памяти много - размер последовательности делаем максимально возможным
            // chunkLength = Integer.MAX_VALUE;
        }
    }

    /**
     * Сортировка слиянием массивов в памяти
     * @param arr
     * @return
     */
    private long[] mergeSort(long[] arr){
        if (arr.length == 1 || arr.length == 0) {
            return arr;
        }
        long[] leftArr = mergeSort(Arrays.copyOfRange(arr, 0, arr.length/2));
        long[] rightArr = mergeSort(Arrays.copyOfRange(arr, arr.length/2, arr.length));

        int n = 0;
        int m = 0;
        int k = 0;
        long[] resultArr = new long[leftArr.length + rightArr.length];
        while ((n < leftArr.length) && (m < rightArr.length)) {
            if (leftArr[n] < rightArr[m]) {
                resultArr[k] = leftArr[n];
                n++;
            } else {
                resultArr[k] = rightArr[m];
                m++;
            }
            k++;
        }
        while (n < leftArr.length){
            resultArr[k] = leftArr[n];
            n++;
            k++;
        }
        while (m < rightArr.length){
            resultArr[k] = rightArr[m];
            m++;
            k++;
        }
        return resultArr;
    }

    /**
     * Получение фрагмента файла для сортировки
     * @param scanner
     * @param chunkLength
     * @return
     */
    private long[] getChunk(Scanner scanner, int chunkLength){
        long[] arr = new long[chunkLength];
        int j = 0;
        while (scanner.hasNextLong()){
            arr[j] = scanner.nextLong();
            j++;
            if (j >=chunkLength) {
                break;
            }
        }
        return Arrays.copyOfRange(arr, 0, j);
    }

    /**
     * Сохранение отсортированного массива в промежуточном файле
     * @param chunkArr
     * @param chunkNumber
     * @return
     * @throws IOException
     */
    private boolean saveChunk(long[] chunkArr, int chunkNumber) throws IOException {
        File file = new File("chunk" + chunkNumber + ".txt");
        try (PrintWriter pw = new PrintWriter(file)) {
            for (int i = 0; i < chunkArr.length; i++) {
                pw.println(chunkArr[i]);
            }
            pw.flush();
        }
        return true;
    }

    /**
     * Слияние промежуточных файлов с последующим их удалением
     * @param chunkCount
     * @return
     * @throws IOException
     */
    private File mergeFiles(int chunkCount)  throws IOException  {
        TreeMap<Long, Integer> heap = new TreeMap<>(); // куча для слияния отсортированных файлов
        File[] chunkFiles = new File[chunkCount]; // отсортированные файлы
        Scanner[] chunkScanners = new Scanner[chunkCount]; // сканеры файлов
        for (int i = 0; i < chunkCount; i++) {
            chunkFiles[i] = new File("chunk" + i + ".txt");
            chunkScanners[i] = new Scanner(chunkFiles[i]);
        }

        // инициализируем кучу
        for (int i = 0; i < chunkCount; i++) {
            if (chunkScanners[i].hasNextLong()){
                heap.put(chunkScanners[i].nextLong(), i);
            }
        }

        int count = 0;
        File file = new File("outData.txt"); // имя файла можно вынести в параметры класса
        try (PrintWriter pw = new PrintWriter(file)) {
            // выполняем сортировку слиянием данных из разных файлов
            while (count != chunkCount){
                Long number = heap.firstKey();
                int index = heap.get(number);
                heap.remove(number);
                pw.println(number);
                if (chunkScanners[index].hasNextLong()){
                    heap.put(chunkScanners[index].nextLong(),index);
                } else {
                    count++;
                }
            }
            pw.flush();
        }

        for (int i = 0; i < chunkCount; i++) {
            chunkFiles[i].deleteOnExit(); // Чтобы посмотреть промежуточные файлы, можно закомментировать данную строку
            chunkScanners[i].close();
        }
        return file;
    }

    /**
     * Сортировка файлы
     * @param dataFile - файл, предназначенный для сортировки
     * @return
     * @throws IOException
     */
    public File sortFile(File dataFile) throws IOException {

        Scanner scanner = new Scanner(dataFile);
        int chunkNumber = 0;
        long[] result = new long[0];
        while (scanner.hasNextLong()) {
            long[] arr = getChunk(scanner, chunkLength);
            result = mergeSort(arr);
            saveChunk(result, chunkNumber);
            chunkNumber++;
        }
        scanner.close();

        return mergeFiles(chunkNumber);
    }
}
