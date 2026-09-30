public class Verify {
    public static void main(String[] args) {
         if (args.length > 0 && args[0].equals("init")) {
            git.init();
        }
        if (args.length > 0 && args[0].equals("test")) {
            git.createBLOB("text.txt");
            git.createBLOB("text2.txt");
            git.createBLOB("text3.txt");
            git.updateIndex("text.txt");
            git.updateIndex("text2.txt");
            git.updateIndex("text3.txt");
      }
    }
}
