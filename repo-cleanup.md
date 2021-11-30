# Repo cleanup

download bfg jar from
https://rtyley.github.io/bfg-repo-cleaner/

tutorial:
https://www.saschawillems.de/blog/2017/09/10/how-to-shrink-down-a-github-repository/


basic workflow:
```
# clone repo
git clone --mirror https://github.com/scalamolecule/molecule.git

# check size before
cd molecule.git
du -sh .

# run --delete-files or other command from parent dir
cd ..
java -jar bfg.jar --delete-files "*.jar" molecule.git

// commit and push
cd molecule.git
git reflog expire --expire=now --all && git gc --prune=now --aggressive
git push

# check size after
du -sh .
```