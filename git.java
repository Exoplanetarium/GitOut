import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class git {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("init")) {
            init();
        }
    }

    public static void init() {
        File git = new File("git");
        if (!git.exists()) {
            git.mkdir();
        }

        File objects = new File(git, "objects");
        if (!objects.exists()) {
            objects.mkdir();
        }

        try {
            File INDEX = new File(git, "INDEX");
            if (!INDEX.exists()) {
                INDEX.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }

}

