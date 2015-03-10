#run at directory of the build

#create file
echo "#!/bin/sh
exec java -jar \"\$0\" \"\$@\"

" > ./caveland

#attach the .jar
cat ./CavelandObf.jar >> ./caveland
chmod +x ./caveland