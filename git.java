import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.codec.digest.DigestUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDateTime;
// changes dssdsffdssdfsdfsfdsdfsdfsdfsdfdsfdsfdsf

public class git {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("init")) {
            init();
        }
        
    }

    public static void init() {
        int fileExistsCounter = 0;
        File git = new File("git");
        if (!git.exists()) {
            git.mkdir();
        } else {
            fileExistsCounter++;
        }

        File objects = new File(git, "objects");
        if (!objects.exists()) {
            objects.mkdir();
        } else {
            fileExistsCounter++;
        }

        try {
            File index = new File(git, "index");
            if (!index.exists()) {
                index.createNewFile();
                
            } else {
                fileExistsCounter++;
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
                fileExistsCounter++;
            }
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
        if (fileExistsCounter == 4) {
            System.out.println("Git Repository Already Exists");
        } else {
            System.out.println("Git Repository Created");
        }
    }

    public static String hashFile(String input) {
        String hashed = DigestUtils.sha1Hex(input);
        return hashed;
    }

    public static void createBLOB(String fileName) throws IOException{
        
        File file = new File(fileName);
        if (file.exists() == false || file.isDirectory() == true) {
            throw new IOException("file either doesnt exists or is directory");
        }

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
        try (FileWriter writer = new FileWriter(index)) {
            writer.write(indexContents.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String createTree(String dirPath) {
        try {
            File dir = new File(dirPath);
            StringBuilder treeString = new StringBuilder();
            treeString = createTreeHelper(dir);

            // read tree hash
            File tree = new File("git/objects/" + hashFile(treeString.toString()));
            FileWriter wr = new FileWriter(tree);
            wr.write(treeString.toString());
            wr.close();
            tree.createNewFile();
            return hashFile(treeString.toString());

        } catch (IOException e) {
            return "";
        }
        
    }

    public static StringBuilder createTreeHelper(File dir) throws IOException {            
        StringBuilder sb = new StringBuilder();
        File[] fileArr = dir.listFiles();
        for (File file : fileArr) {
            if (file.isDirectory()) {
                StringBuilder dirContents = createTreeHelper(file);
                if (!sb.isEmpty()) {
                    sb.append("\n");
                }

                sb.append("tree " + hashFile(dirContents.toString()) + " " + file.getName());
            } else {
                createBLOB(file.getPath());
                if (!sb.isEmpty()) {
                    sb.append("\n");
                }

                StringBuilder contents = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (!firstLine) {
                        contents.append("\n");
                    }
                    contents.append(line);
                    firstLine = false;
                }
                reader.close();
                String fileContents = contents.toString();
                String hashed = hashFile(fileContents);
                sb.append("blob " + hashed + " " + file.getName());
            }
        }

        return sb;
    }

    public static String createTreeFromIndex() {
        try {
            // sorting the working list
            BufferedReader br = new BufferedReader(new FileReader("git/index"));
            ArrayList<ArrayList<String>> workingList = new ArrayList<>();

/* 

            ArrayList<ArrayList<String>> toBeSorted = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                ArrayList<String> entry = new ArrayList<>(List.of("blob " + line.split(" ")[0]));
                workingList.add(entry);

                ArrayList<String> paths = new ArrayList<>();
                for (String dir : line.split(" ")[1].split("/")) {
                    paths.add(dir);
                }
                toBeSorted.add(paths);
            }

            ArrayList<ArrayList<String>> sorted = pathSorter(toBeSorted);
            for (int i = 0; i < workingList.size(); i++) {
                for (int j = 0; j < sorted.get(i).size(); j++) {
                    workingList.get(i).add(sorted.get(i).get(j));
                }
            }

            br.close();
*/

            String line = br.readLine();
            while (line != null) {
                ArrayList<String> entry = new ArrayList<>();
                String hash = line.split(" ")[0];
                String path = line.split(" ")[1];

                entry.add("blob " + hash);
                String[] paths = path.split("/");
                for (String dir : paths) {
                    entry.add(dir);
                }
                workingList.add(entry);
                line = br.readLine();
            }


            workingList = pathSorter(workingList);
            br.close();



            ArrayList<ArrayList<String>> finalTree = createTreeFromIndexHelper(workingList);
            StringBuilder rootStr = new StringBuilder();
            boolean firstLine = true;
            for (String treePart : finalTree.get(0)) {
                if (!firstLine) {
                    rootStr.append(" ");
                }
                rootStr.append(treePart);
                firstLine = false;
            }

            String[] finals = rootStr.toString().split(" ");
            String fhash = finals[1];
            return fhash;
        } catch(IOException e) {
            return "";
        }
    }

    public static ArrayList<ArrayList<String>> createTreeFromIndexHelper(ArrayList<ArrayList<String>> arr) throws IOException{
        if (arr.size() >= 1) {
            ArrayList<String> pathToMerge = new ArrayList<>();
            int countFinishedDirs = 0;
            for (int i = 0; i < arr.size(); i++) {    
                if (arr.get(i).size() == 2) {
                    countFinishedDirs++;
                    continue;
                }        
                
                if (pathToMerge.isEmpty()) {
                    pathToMerge = arr.get(i);
                }

                // iterates over deepest paths
                while (arr.get(i).size() == pathToMerge.size()) {
                    StringBuilder path = new StringBuilder();
                    boolean firstLine = true;
                    ArrayList<String> treeEntry = new ArrayList<>();
                    for (int j = 0; j < arr.get(i).size() - 1; j++) {
                        if (j == 0) {
                            continue;
                        } else {
                            if (!firstLine) {
                                path.append("/");
                            }
                            path.append(arr.get(i).get(j));
                            firstLine = false;

                            treeEntry.add(arr.get(i).get(j));
                        }  
                    }
                    String hash = createTree(path.toString());

                    // collapses blobs that are now in a tree
                    ArrayList<ArrayList<String>> arrWithBlobsCollapsed = new ArrayList<>();
                    for (ArrayList<String> entry : arr) {
                        String[] pathSplit = path.toString().split("/");
                        if (!entry.get(entry.size() - 2).equals(pathSplit[pathSplit.length - 1])) {
                            arrWithBlobsCollapsed.add(entry);
                        }
                    }

                    arr = arrWithBlobsCollapsed;
                    treeEntry.add(0, "tree " + hash);
                    arr.add(i, treeEntry);
                }
            }

            if (countFinishedDirs == arr.size()) {
                // package as root
                StringBuilder finalMerge = new StringBuilder();
                boolean firstLine = true;
                for (ArrayList<String> entry : arr) {
                    if (!firstLine) {
                        finalMerge.append("\n");
                    }
                    boolean firstPart = true;
                    for (String part : entry) {
                        if (!firstPart) {
                            finalMerge.append(" ");
                        }
                        finalMerge.append(part);
                        firstPart = false;
                    }
                    firstLine = false;
                }

                String hash = hashFile(finalMerge.toString());
                String finalStr = finalMerge.toString();
                FileWriter rootw = new FileWriter("git/objects/" + hash);
                rootw.write(finalStr);
                rootw.close();


                String lastOne = "tree " + hash + " (root)";
                ArrayList<String> puttingIntoArray = new ArrayList<>(List.of(lastOne));
                ArrayList<ArrayList<String>> finalArray = new ArrayList<>(List.of(puttingIntoArray));
                return finalArray;
            }

            arr = createTreeFromIndexHelper(arr);
        }
        
        return arr;
        
    }

    public static ArrayList<ArrayList<String>> pathSorter(ArrayList<ArrayList<String>> arr) {
        ArrayList<ArrayList<String>> sorted = new ArrayList<>();
        int maxSize = 0;
        for (ArrayList<String> entry : arr) {
            if (entry.size() > maxSize) {
                maxSize = entry.size();
            }
        }

        while (maxSize > 0) {
            for (ArrayList<String> entry : arr) {
                if (entry.size() == maxSize) {
                    sorted.add(entry);
                }
            }
            maxSize--;
        }

        return sorted;
    }

    public static String commit (String author, String message) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader("git/HEAD"));
            String parent = br.readAllAsString();
            br.close();
            String treeHash = createTreeFromIndex();
            StringBuilder sb = new StringBuilder();
            if (parent.length() == 0 || parent == null) {
                sb.append("tree: " + treeHash + "\n");
                sb.append("author: " + author + "\n");
                sb.append("date: " + LocalDateTime.now() + "\n");
                sb.append("message: " + message);
            } else {
                sb.append("tree: " + treeHash + "\n");
                sb.append("parent: " + parent + "\n");
                sb.append("author: " + author + "\n");
                sb.append("date: " + LocalDateTime.now() + "\n");
                sb.append("message: " + message);
            }
            String temp = sb.toString();
            String hash = hashFile(temp);
            File obj = new File("git/objects/" + hash);
            obj.createNewFile();
            FileWriter fw = new FileWriter("git/objects/" + hash);
            fw.write(temp);
            fw.close();

            FileWriter head = new FileWriter("git/HEAD");
            head.write(hash);
            head.close();

            return hash;
        } catch (IOException e) {
            throw new IOException("dsdsfsdf");
        }

    }
    // get original hash of root of the commit
    // make the full text (probably another method, recursin), return the full workinglist
    // rewrite the file from this workinglist

    public static void regenerateIndex(String commitHash) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("git/objects/" + commitHash));
        String[] treeArray = br.readLine().split(" ");
        br.close();

        String treeHash = treeArray[1];

        ArrayList<String> lines = makeIndex(treeHash, ""); // recursive method here

        FileWriter fw = new FileWriter("git/index");
        for (int i = 0; i < lines.size(); i++) {
            fw.write(lines.get(i));
            if (i < lines.size() - 1) {
                fw.write("\n");
            }
        }
        fw.close();
    }



    // while
    public static ArrayList<String> makeIndex(String treeHash, String path) throws IOException{
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("git/objects/" + treeHash));
        String line = br.readLine();
        while (line != null) {
            String[] parts = line.split(" ");
            String type = parts[0];
            String hash = parts[1];
            String name = parts[2];

            if (type.equals("blob")) {
                String add = hash + " " + path + name;
                lines.add(add);
            } else if (type.equals("tree")) {
                ArrayList<String> temp = new ArrayList<>();
                String newPath = path + name + "/";
                temp = makeIndex(hash, newPath);
                for (String current : temp) {
                    lines.add(current);
                }
            }
            line = br.readLine();
        }

        br.close();

        return lines;
    }

    public static void checkout(String commitHash) {
        // save lines before replace in arraylist done
        // delete these files and folders
        // regen
        // recreate the files from this index
        // move head to that commit
        try {
            ArrayList<String> oldPaths = new ArrayList<>();
            BufferedReader br1 = new BufferedReader(new FileReader("git/index"));
            String line = br1.readLine();
            while (line != null) {
                String current = line.split(" ")[1];
                oldPaths.add(current);
                line = br1.readLine();
            }

            br1.close();

            for (String current : oldPaths) {
                File file = new File(current);
                file.delete();

                String[] parts = current.split("/");
                for (int i = parts.length - 1; i > 0; i--) {
                    String dir = "";
                    for (int j = 0; j < i; j++) {
                        if (j > 0) {
                            dir = dir + "/";
                        }
                        dir = dir + parts[j];
                    }
                    File folder = new File(dir);
                    if (folder.list() != null && folder.list().length == 0) {
                        folder.delete();
                    }
                }
            }

            regenerateIndex(commitHash);

            BufferedReader br = new BufferedReader(new FileReader("git/index"));
            line = br.readLine();
            while (line != null) {
                String hash = line.split(" ")[0];
                String path = line.split(" ")[1];
                String[] parts = path.split("/");
                String dir = "";
                for (int i = 0; i < parts.length -1; i++) {
                    if (i > 0) {
                        dir = dir + "/";
                    }
                    dir = dir + parts[i];
                    File folder = new File(dir);
                    folder.mkdir();
                }

                Path blob = Path.of("git/objects/" + hash);
                String contents = Files.readString(blob);
                FileWriter fw = new FileWriter(path);
                fw.write(contents);
                fw.close();

                line = br.readLine();

                
            }
            br.close();

            FileWriter fw = new FileWriter("git/HEAD");
            fw.write(commitHash);
            fw.close();




        } catch (Exception e) {
            System.out.println("IOException");
        }
    }

    
}
