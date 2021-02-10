## Background

Because of the state of Big Tech and deplatforming of individuals and groups, it is clear that "renters have no rights". This is neither good nor bad. Private companies can do what they want. However, we don't have to be at their mercy to have them regulate speech as they see fit. The deplatforming goes as deep as the infrastructure providers, so "just make your own product" is not an adequate answer to losing the ability to communicate. The hardware we all own is our personal computers. By creating a network of "servers" and "clients", there is no central entity that controls us. Encryption protects communication in transit. 

All speech must be free, or no speech is free. It is a foundation of liberty.

## Getting Started

This project is developed in the Visual Studio Code IDE. There are no extra project and dependency files to clutter the project. There is also no one that can object to use of their dependency packages in this project. Yes, Oracle owns Java. If there is an alternative, cross platform language that supports GUI, I will support a rewrite.

## Folder Structure

- `src`: the folder to maintain source Java files

## Dependency Management

Town Square requires JDK 11 and JavaFX 11. There are no other dependencies, and that's by design. Creators and maintainers of open source libraries have demanded their code be removed from certain projects based on ideological reasons. This project will not be subject to the whims of others.

If you use Visual Studio Code, you will need to specify a settings.json file in the .vscode folder to point to the JavaFX libraries. 

"java.project.referencedLibraries": [<br/>
        "lib/**/*.jar",<br/>
        "path-to-javafx/javafx.base.jar",<br/>
        "path-to-javafx/javafx.controls.jar",<br/>
        "path-to-javafx/javafx.fxml.jar",<br/>
        "path-to-javafx/javafx.graphics.jar",<br/>
        "path-to-javafx/javafx.media.jar",<br/>
        "path-to-javafx/javafx.swing.jar",<br/>
        "path-to-javafx/javafx.web.jar",<br/>
        "path-to-javafx/javafx-swt.jar"<br/>
    ]<br/>

To launch a debug session, you will need to specify a launch.json file and add:

- "vmArgs": "--module-path [path-to-javafx] --add-modules javafx.controls,javafx.fxml,javafx.media",

## Unit Tests

The Unit Tests are executed by specifying the xxUnitTestsUtility as the main project. No extra, fancy unit testing framework is used, because it isn't needed.

## Contributions

Contributions are not open, at this time. Feel free to fork the project and create your own version. It's nothing personal, but I don't know you or your motives.
