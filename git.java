import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.codec.digest.DigestUtils;

public class git {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("init")) {
            init();
        }
    }

    public static void init() {
        int num = 0;
        File git = new File("git");
        if (!git.exists()) {
            git.mkdir();
        } else {
            num++;
        }

        File objects = new File(git, "objects");
        if (!objects.exists()) {
            objects.mkdir();
        } else {
            num++;
        }

        try {
            File INDEX = new File(git, "INDEX");
            if (!INDEX.exists()) {
                INDEX.createNewFile();
            } else {
                num++;
            }
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }

        try {
            File HEAD = new File(git, "HEAD");
            if (!HEAD.exists()) {
                HEAD.createNewFile();
            } else {
                num++;
            }
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
        if (num == 4) {
            System.out.println("Git Repository Already Exists");
        } else {
            System.out.println("Git Repository Created");
        }
    }

    public static String hashFile(String input) {
        String hashed = DigestUtils.sha1Hex(input);
        return hashed;
    }

    public static void createBLOB(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileContents = sb.toString();
        String hashed = hashFile(fileContents);
        try {
            File save = new File("objects", hashed);
            save.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(hashed))) {
            writer.write(fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
