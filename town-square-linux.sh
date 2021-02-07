[ ! -d "javafx-sdk-11.0.2/lib" ] && 
    unzip javafx-linux.zip

[ ! -d "jdk-11.0.10+9" ] && 
    gunzip -c OpenJDK11U-jdk_x64_linux_hotspot_11.0.10_9.tar.gz| tar xopf -

./jdk-11.0.10+9/bin/java --module-path "javafx-sdk-11.0.2/lib" --add-modules "javafx.controls,javafx.fxml,javafx.media" -jar App.jar