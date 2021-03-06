musical-ear-trainer
===================

![](https://www.dropbox.com/s/ui49osnqpui7c5p/piano.png?dl=1)

A tool to help you practice playing novel melodies by ear on a piano-like virtual instrument.

## How the program works

This program contains a virtual piano. You select the properties for a melody (e.g., key, length, tempo), and the piano can automatically play a randomly-generated melody with those properties. Your job is then to play the melody back. If you fail, the incorrect key you press will turn red, and you can have the program replay the melody. Once you succeed -- or if you change any of the melody properties -- the program can play a new melody. When you press the last (correct) key of a melody, the key will turn green.

## Code design

The **MusicaEarTrainer** class is the main class and is a JFrame. It represents the entire program window and defines the Swing components you'll use to define the melody properties, start/stop/repeat the automatically played melody, etc. It also has a **Piano** object that contains the virtual piano that is added below the components.

The MusicaEarTrainer object sends messages to the Piano object. In addition, it listens for property change events from Piano (e.g., Is an automatically played melody in progress?). These properties are represented by the **Modes** enumeration.

The Piano class has instances of **MelodyMaker** and **MIDISynth**. The former generates melodies and iterates through them for auto-playing and evaluating user input. The latter is a simple class that generates the piano-like sounds you hear.

## Compiling/building/running

This program is intended to be run as a standalone executable JAR file using the [Java Runtime Environment](https://java.com/en/download/) (JRE), version 7 or higher. The JAR file is available [here](https://www.dropbox.com/s/1uviq1wivqdhtw6/MusicalEarTrainer.jar?dl=1). If you use Windows or a popular Linux distribution, installing the JRE and double-clicking the JAR file should suffice. If you're on a Mac, you might be required to install the full Java Development Kit (which requires registering with Oracle, etc.).
