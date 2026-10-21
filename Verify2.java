import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Verify2 {
    public static void main(String[] args) throws IOException{
        git.init();
        FileWriter fw1 = new FileWriter("f3/text5.txt");
        fw1.write("bonjour");
        fw1.close();
        git.updateIndex("f3/text5.txt");
        git.updateIndex("f0/f1/text.txt");
        git.updateIndex("f0/text2.txt");
        git.updateIndex("f0/text3.txt");
        git.updateIndex("f0/f1/f2/text4.txt");
        git.updateIndex("f0/f1/f2/text4.txt");
        git.updateIndex("f3/text5.txt");
        git.updateIndex("f3/text5.txt");
        System.out.println(git.createTreeFromIndex());
        System.out.println(git.createTreeFromIndex());
        FileWriter fw = new FileWriter("f3/text5.txt");
        fw.write("bonjour again");
        fw.close();
        git.updateIndex("f3/text5.txt");
        System.out.println(git.createTreeFromIndex());
        String a = git.commit("Connor", "first");
        System.out.println(a);

        FileWriter fw2 = new FileWriter("f3/text5.txt");
        fw2.write("bonjour againdoidiodiodoi");
        fw2.close();

        System.out.println(git.commit("Connor", "second"));
        git.regenerateIndex(a);

        git.checkout(a);
    }
}
