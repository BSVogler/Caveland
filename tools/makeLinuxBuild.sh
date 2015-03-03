#create file
echo "#!bin/sh
exec java -jar \"\$0\" \"\$@\"

" > ./caveland

#attach the .jar
cat ./build.jar >> ./caveland
chmod +x ./caveland