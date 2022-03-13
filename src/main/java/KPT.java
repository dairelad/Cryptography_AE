import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KPT {
    public	static	int	ENC = 1;	// encrypt
    public	static	int	DEC = 2;	// decrypt
    public static ArrayList<String> passwords = new ArrayList<>();
    public static ArrayList<String>  ciphers = new ArrayList<>();
    public static ArrayList<Character>  part1Letters = new ArrayList<>();

    public static void main(String[] args){

        try {
            /************/
            /*  Part 1  */
            /************/

            //read files and populate arrays
            String filepath1 = returnFilePath("src", "passwords.txt");
            readFile(filepath1, passwords); //populates passwords array
            String filepath2 = returnFilePath("src", "cipher.txt");
            readFile(filepath2, ciphers); //populates ciphers array

            //isolate 2 letters that are used for part 1
            part1Letters.add(ciphers.get(1).charAt(0));
            part1Letters.add(ciphers.get(1).charAt(1));

            //remove all non ciphers-texts from cipher array
            for (int i = ciphers.size()-1; i >= 0; i--){
                if (ciphers.get(i).length() < 46){ //46 chosen so that only the 2 ciphers are kept
                    ciphers.remove(i);
                }
            }

            //for each key decrypt the given cipher text and return the one with the correct
            //starting letters
            System.out.println("Cipher 1"); System.out.println(ciphers.get(0));
            System.out.println("Using starting letters: " + part1Letters.get(0) + part1Letters.get(1) + "\n");
            for (String key:passwords) {
                String dec = R96Crypto.encdec(DEC,key,ciphers.get(0)); //first cipher
                Scanner sc = new Scanner(dec);
                String str = sc.next();
                if (str.length() > 1) {
                    char firstLetter = str.charAt(0);
                    char secondLetter = str.charAt(1);
                    if (firstLetter == part1Letters.get(0) && secondLetter == part1Letters.get(1)) {
                        System.out.println("Key used - " + key + ": ");
                        System.out.println(dec);
                        System.out.println();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Program finished (finally)");
        }

    }

    //method reads a file into an arraylist
    public static void readFile(String filePath, ArrayList<String> array) throws IOException {
        String line;
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        while ((line = br.readLine()) != null) {
            array.add(line);
        }
    }

    //method returns filepath given a name and source folder
    public static String returnFilePath(String sourceFolder, String fileName) throws IOException {
        // code to find file in directory
        Path path = Paths.get(sourceFolder); //look in the source folder
        List<Path> result = findByFileName(path, fileName); //for this filename
        //result.forEach(x -> System.out.println(x)); //print filepath
        //escape characters used to remove brackets from String
        String filePath = result.toString().replaceAll("[\\p{Ps}\\p{Pe}]", "");
        return filePath;
    }

    //helper for returnFilePath
    public static List<Path> findByFileName(Path path, String fileName) throws IOException {
        List<Path> result;
        try (Stream<Path> pathStream = Files.find(path,
                Integer.MAX_VALUE,
                (p, basicFileAttributes) ->
                        p.getFileName().toString().equalsIgnoreCase(fileName))
        ) {
            result = pathStream.collect(Collectors.toList());
        }
        return result;
    }
}
