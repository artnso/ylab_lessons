package FileSortBugFix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Sorter {
    private final int chunkLength;

    Sorter(){
        Long availableMemory = Runtime.getRuntime().maxMemory();
        if (availableMemory / 1000 < Integer.MAX_VALUE){
            chunkLength = (int)(availableMemory / 1000);
        } else {
            chunkLength = 1_000_000;
        }
    }

    private List<Long> getChunk(Scanner scanner, int chunkLength){
        List<Long> chunk = new ArrayList<>();
        while (scanner.hasNextLong()){
            if (chunk.size() >= chunkLength) {
                break;
            }
            chunk.add(scanner.nextLong());
        }
        return chunk;
    }

    private void saveChunk(List<Long> chunk, int chunkNumber) throws IOException {
        File chunkFile = new File("chunk" + chunkNumber + ".txt");
        try (PrintWriter pw = new PrintWriter(chunkFile)){
            for (long number: chunk){
                pw.println(number);
            }
            pw.flush();
        }
    }

    private File mergeFiles(int chunkCount) throws IOException {
        SortedMap<Long, Integer> heap = new TreeMap<>();
        List<File> chunkFiles = new ArrayList<>();
        List<Scanner> chunkScanners = new ArrayList<>();

        for (int i = 0; i < chunkCount; i++){
            chunkFiles.add(new File("chunk" + i + ".txt"));
            chunkScanners.add(new Scanner(new FileInputStream(chunkFiles.get(i))));
        }

        for (Scanner chunkScanner: chunkScanners) {
            if (chunkScanner.hasNextLong()){
                heap.put(chunkScanner.nextLong(), chunkScanners.indexOf(chunkScanner));
            }
        }

        int count = 0;
        File outFile = new File("outData.txt");
        try (PrintWriter pw = new PrintWriter(outFile)) {
            // выполняем сортировку слиянием данных из разных файлов
            while (count != chunkCount){
                Long number = heap.firstKey();
                int index = heap.get(number);
                heap.remove(number);
                pw.println(number);
                if (chunkScanners.get(index).hasNextLong()){
                    heap.put(chunkScanners.get(index).nextLong(), index);
                } else {
                    count += 1;
                }
            }
            pw.flush();
        }

        for (Scanner chunkScanner: chunkScanners) {
            chunkScanner.close();
        }

        for (File chunkFile: chunkFiles) {
            chunkFile.deleteOnExit();
        }

        return outFile;
    }

    public File sortFile(File dataFile) throws IOException {
        Scanner scanner = new Scanner(new FileInputStream(dataFile));
        int chunkNumber = 0;

        while (scanner.hasNextLong()){
            List<Long> chunk = getChunk(scanner, chunkLength);
            Collections.sort(chunk);
            saveChunk(chunk, chunkNumber);
            chunkNumber += 1;
        }

        scanner.close();
        return mergeFiles(chunkNumber);
    }
}
