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
        if (args.length > 0 && args[0].equals("test")) {
            createBLOB("text.txt");
            updateIndex("text.txt");
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
            File index = new File(git, "index");
            if (!index.exists()) {
                index.createNewFile();
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
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    sb.append("\n");
                }
                sb.append(line);
                firstLine = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileContents = sb.toString();
        String hashed = hashFile(fileContents);
        File save = new File("git/objects", hashed);
        try {
            save.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(save))) {
            writer.write(fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateIndex(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    sb.append("\n");
                }
                sb.append(line);
                firstLine = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileContents = sb.toString();
        String hashed = hashFile(fileContents);
        File index = new File("git", "index");
        StringBuilder indexContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(index))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!line.endsWith(" " + fileName)) {
                    if (!firstLine) {
                        indexContents.append("\n");
                    }
                    indexContents.append(line);
                    firstLine = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (indexContents.length() > 0) {
            indexContents.append("\n");
        }
        indexContents.append(hashed + " " + fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(index))) {
            writer.write(indexContents.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
