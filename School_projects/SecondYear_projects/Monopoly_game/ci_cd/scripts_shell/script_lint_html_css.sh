npm install htmlhint
npm install csslint-cli
npx htmlhint --nocolor -f compact ./pages/*.html > ./ci_cd/linthtml_report.txt
npx csslint-cli --threshold=0 ./styles/*.css > ./ci_cd/lintcss_report.txt
exit 0