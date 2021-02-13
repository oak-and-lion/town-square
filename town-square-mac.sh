echo verifying JavaFX dependency

if [ ! -d "javafx" ]
then
    echo unzipping JavaFX dependency
    tar xopf javafx-mac.zip
fi

echo verifying Java runtime dependency

if [ ! -d "jdk-11.0.2.jdk" ]
then
    echo unzipping Java runtime dependency
    gunzip -c openjdk-11.0.2_osx-x64_bin.tar.gz| tar xopf -
fi

appjarfile="_new_App.jar"
appverfile="_new_App.ver"
if [ -f $appjarfile ]
then
    echo new App.jar file
    mv $appjarfile App.jar
    if [ -f $appverfile ]
    then
        rm $appverfile
    fi
fi

clonefile="my_square.clone"
if [ -f $clonefile ]
then
    unzip $clonefile
    rm $clonefile
fi

echo starting Town Square

./jdk-11.0.2.jdk/Contents/Home/bin/java --module-path "javafx/lib" --add-modules "javafx.controls,javafx.fxml,javafx.media" -jar App.jar