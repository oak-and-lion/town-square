@echo off

if NOT EXIST "javafx\" (
    tar -x -f javafx-windows.zip
)

if NOT EXIST "jdk-11.0.9\" (
    tar -x -f jdk-11.0.9_windows-x64_bin.zip
)

if EXIST "_new_App.jar" (
    copy /B /Y _new_App.jar App.jar
    del _new_App.jar
)

jdk-11.0.9\bin\java --module-path "javafx/lib" --add-modules "javafx.controls,javafx.fxml" -jar App.jar