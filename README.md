musical-ear-trainer
===================

A tool to help you practice playing novel melodies by ear on a piano-like virtual instrument.

## How the program works

This program contains a virtual piano. You select the properties for a melody (e.g., key, length, tempo), and the piano can automatically play a randomly-generated melody with those properties. Your job is then to play the melody back. If you fail, you can have the program replay the melody. Once you succeed -- or if you change any of the melody properties -- the program can play a new melody.

## Code design

The **MusicaEarTrainer** class is the main class. It represents the entire program window and defines the Swing components you'll use to define the melody properties, start/stop/repeat the automatically played melody, etc. It also creates a **Piano** object that contains the virtual piano that is added below the components.

The MusicaEarTrainer object sends messages to the Piano object. In addition, the MusicaEarTrainer objects listens for changes in the Piano object's state (e.g., Is an automatically played melody in progress?). The states are represented by the **Modes** enumeration.

The Piano class creates instances of **MelodyMaker** and **MIDISynth**. The former generates melodies that will be automatically played, and the Piano object uses it to know what notes to automatically play, and to determine whether a user is correctly playing the melody back. MIDISynth is a simple interface that generates the piano-like sounds you hear.

## Compiling/building/running

This program is written in Java and does not use any special libraries. It is intended to be run as a standalone executable JAR file. 
