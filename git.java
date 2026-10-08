import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

            ArrayList<ArrayList<String>> finalTree = createTreeFromIndexHelper(workingList);
            StringBuilder rootStr = new StringBuilder();
            FileWriter wr = new FileWriter("git/index");
            boolean firstLine = true;
            for (String treePart : finalTree.get(0)) {
                if (!firstLine) {
                    rootStr.append(" ");
                    wr.write(" ");
                }
                rootStr.append(treePart);
                wr.write(treePart);
                firstLine = false;
            }

            wr.close();
            return hashFile(rootStr.toString());
        } catch(IOException e) {
            return "";
        }
    }

    public static ArrayList<ArrayList<String>> createTreeFromIndexHelper(ArrayList<ArrayList<String>> arr) {
        if (arr.size() > 1) {
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
}
