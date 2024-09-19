npm i  # Installe ce qui est décrit dans package.json
npm run start & # Lance le serveur
cd ./ci_cd/
npx cypress run | sed 's/\x1B\[[0-9;]\{1,\}[A-Za-z]//g' > cypress_report.txt  # Lance les tests
cd ..
kill -9 $(ps | grep node | awk '{print $1}') # Cette ligne pourrait permettre de tuer serve
exit 0