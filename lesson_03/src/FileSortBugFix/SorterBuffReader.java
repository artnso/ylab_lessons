package FileSortBugFix;

import java.io.*;
import java.util.*;

public class SorterBuffReader {
    private final int chunkLength;

    SorterBuffReader(){
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
        List<BufferedReader> chunkBufferedReaders = new ArrayList<>();

        for (int i = 0; i < chunkCount; i++){
            chunkFiles.add(new File("chunk" + i + ".txt"));
            chunkBufferedReaders.add(new BufferedReader(new FileReader(chunkFiles.get(i))));
        }

        for (BufferedReader chunkBufferedReader: chunkBufferedReaders) {
            if (!chunkBufferedReader.readLine().isEmpty()){
                heap.put(Long.parseLong(chunkBufferedReader.readLine()), chunkBufferedReaders.indexOf(chunkBufferedReader));
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
                if (chunkBufferedReaders.get(index).ready()){
                    heap.put(Long.parseLong(chunkBufferedReaders.get(index).readLine()), index);
                } else {
                    count += 1;
                }
            }
            pw.flush();
        }

        for (BufferedReader chunkBufferedReader: chunkBufferedReaders) {
            chunkBufferedReader.close();
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
