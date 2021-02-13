echo verifying JavaFX dependency

if [ ! -d "javafx-sdk-11.0.2/lib" ]
then
    echo unzipping JavaFX dependency
    unzip javafx-linux.zip
fi

echo verifying Java runtime dependency

if [ ! -d "jdk-11.0.10+9" ]
then
    echo unzipping Java runtime dependency
    gunzip -c OpenJDK11U-jdk_x64_linux_hotspot_11.0.10_9.tar.gz| tar xopf -
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
    unzip -o $clonefile
    rm $clonefile
fi

echo starting Town Square

./jdk-11.0.10+9/bin/java --module-path "javafx-sdk-11.0.2/lib" --add-modules "javafx.controls,javafx.fxml,javafx.media" -jar App.jar