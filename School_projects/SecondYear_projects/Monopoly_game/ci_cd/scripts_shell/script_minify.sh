npm install minify
mkdir -p build
cd build/
mkdir pages script styles
cd ..
for f in pages/*.html; do npx minify "$f" > "./build/$f"; done
for f in script/*.js; do npx minify "$f" > "./build/$f"; done
for f in styles/*.css; do npx minify "$f" > "./build/$f"; done
npx minify "index.html" > "./build/index.html"
exit 0