import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CTO {
    public	static	int	ENC = 1;	// encrypt
    public	static	int	DEC = 2;	// decrypt
    public static ArrayList<String> passwords = new ArrayList<>();
    public static ArrayList<String>  ciphers = new ArrayList<>();
    public static ArrayList<Character>  part1Letters = new ArrayList<>();

    public static void main(String[] args){

        try {
            /********************************************/
            /*  Read in from files and parse to arrays  */
            /********************************************/
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("File read and parsed successfully (finally block)");
        }

        /************/
        /*  Part 2  */
        /************/
        System.out.println("Given cipher text: " + ciphers.get(1) + "\n");

        // keep track of highest and second highest IoC values
        String validPT = ""; String validkey = ""; double validIoC = 0;
        String secondPT = ""; String secondkey = ""; double secondIoC = 0;

        DecimalFormat df = new DecimalFormat("#0.#####");
        for (String key:passwords) {
            String dec0 = R96Crypto.encdec(DEC, key, ciphers.get(1)); //second cipher

            //format plaintext
            String decFormat = dec0;
            decFormat = decFormat.replaceAll(" ", "").toLowerCase();
            int decLength = decFormat.length();
            decFormat = decFormat.replaceAll(".", "$0 ");

            MultiSet m1 = new MultiSet(decFormat);
            List<WordSetElement> letterFreq = m1.getFreq();

            int freqInfo = calcFreq(letterFreq);
            double ioc = (1.00 / (decLength * (decLength - 1))) * freqInfo;
//            double randomIocVal = 0.0385;
//            ioc = ioc/randomIocVal;

            //keep track of 2 highest IoC values
            if (ioc > validIoC & ioc > secondIoC){
                secondkey = validkey; secondPT = validPT; secondIoC = validIoC;
                validkey = key; validPT = dec0; validIoC = ioc;
            }
            if (ioc < validIoC & ioc > secondIoC){
                secondkey = key; secondPT = dec0; secondIoC = ioc;
            }

            if(ioc >= 0.0385 && ioc <= 0.07){
                System.out.println("Message found with index of coincidence within range:");
                System.out.println("Key: " + key + "\nPlain text: " + dec0 + "\tIoC: " + df.format(ioc));
            }
        }

        System.out.println("\nThe valid message as determined by having an index of coincidence closest to English is:\n" +
                validPT);

        /****************/
        /*  Experiment  */
        /****************/
        System.out.println("\n*Experiment* for computing Nu: ");
        System.out.println("\nCharacters needed for unambiguous decoding: > " +
                experiment(validPT, validIoC, secondPT, secondIoC));
    }

    public static int experiment(String pt, double ioc1, String pt2, double ioc2){
        int charNum = pt.length();
        pt = pt.replaceAll(" ", "").toLowerCase();
        pt = pt.replaceAll("[^a-zA-Z]", "");

        DecimalFormat df = new DecimalFormat("#0.#####");
        StringBuilder sb = new StringBuilder(pt);

        for (char c : pt.toCharArray()) {
            sb = sb.deleteCharAt(sb.length()- 1);
            String ptM = sb.toString();
            System.out.println(ptM);
            ptM = ptM.replaceAll(".", "$0 ");
            MultiSet m1 = new MultiSet(ptM);
            List<WordSetElement> letterFreq = m1.getFreq();
            int sumOfFreqsMultipliedByFreqMinus1 = calcFreq(letterFreq);
            double ioc = (1.00 / (sb.length() * (sb.length() - 1))) * sumOfFreqsMultipliedByFreqMinus1;
            double randomIocVal = 0.0385;
            ioc = ioc/randomIocVal;
            charNum = sb.length();
            System.out.println("Number of characters: " + charNum + "\tIoC1: " + df.format(ioc) + "\n");

            if (ioc < 1.5 || ioc > 2){
                System.out.println("IoC1: " + df.format(ioc) + " is out of range for a valid English plaintext message");
                return charNum;
            }
        }
        return charNum;
    }

    //calculate frequency part of index of coincidence equation
    public static int calcFreq(List<WordSetElement> frequencies){
        int freqCalc = 0;
        for(WordSetElement freq: frequencies){
            int frequency = freq.getFreq();
            freqCalc += frequency * (frequency - 1);
            //System.out.println(freq.getKey() + " : " + frequency + " * (" + (frequency - 1) + " - 1) = " + freqCalc);
        }
        return freqCalc;
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
