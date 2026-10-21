init
hashFile
createBLOB
updateIndex
createTree, createTreeHelper
createTreeFromIndex, createTreeFromIndexHelper, pathSorter

added commit(), creates commit files and saves them in objects, then also updates head
added gitwrapper, it is easy to run git commands now
- things i changed: 
- i added the checks for file not existing or directory to throw exception,
- i made it so it still builds root when index only has one file,
- i saved root tree because it didnt before
- i also returned the real root hash not whole string hash like it did before

added regenerate index, i also remade the part of the createtreefromindex bc i didnt really follow it at all so i just coded it agian since it wasn't working. it uses ur pathsorter but it builds each entry with its hash and its path together from the start, then it sorts all entries

added checkout