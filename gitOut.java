import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class gitOut {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("init")) {
                File gitOut = new File ("gitOut");
                if (!gitOut.exists()) {
                    gitOut.mkdir();
                }

                File objects = new File(gitOut, "objects");
            } 
    }
}
