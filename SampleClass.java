import java.io.*;

public class SampleClass {

    public static void main(String[] args) {
        File fileObj = new File("input.txt"); // Replace with your filename
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(fileObj);
            br = new BufferedReader(fr);

            String data = br.readLine();
            int l = 0;
            int r = data.length() - 1;
            int counter = 0;

            while (l < r) {
                if (data.charAt(l) == data.charAt(r)) {
                    counter++;
                }
                l++;
                r--;
            }

            System.out.println("Counter: " + counter);

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        } catch (IOException e) {
            System.out.println("Error reading file!");
        }
    }
}

