@echo off

if NOT EXIST "javafx\" (
    mkdir javafx
    
    cd javafx

    tar -x -f ..\javafx-windows.zip

    cd ..
)

java --module-path "javafx/lib" --add-modules "javafx.controls,javafx.fxml" -jar App.jar