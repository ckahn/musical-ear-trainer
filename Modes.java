/*
 * Three different states of the Piano object.
 * 
 * AUTOPLAY = Program is currently playing a melody on the piano.
 * RECITE = User is expected to repeat the melody just heard.
 * IDLE = Piano can be played, but user is not expected to repeat the melody.
 */
public enum Modes {
    AUTOPLAY, RECITE, IDLE;
}
