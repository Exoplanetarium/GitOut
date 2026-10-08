public class Verify {
    public static void main(String[] args) {
        git.init();
        git.createBLOB("f0/f1/text.txt");
        git.createBLOB("f0/text2.txt");
        git.createBLOB("f0/text3.txt");
        git.createBLOB("f0/f1/f2/text4.txt");
        git.createBLOB("f3/text5.txt");
        git.updateIndex("f0/f1/text.txt");
        git.updateIndex("f0/text2.txt");
        git.updateIndex("f0/text3.txt");
        git.updateIndex("f0/f1/f2/text4.txt");
        git.updateIndex("f3/text5.txt");
        System.out.println(git.createTreeFromIndex());
    }
}
