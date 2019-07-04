package at.hangman.hangman;

import java.util.ArrayList;
import java.util.List;

public class RoomWrapper {
    private static List<Room> rooms = new ArrayList<>();
    public static List<Room> getRooms(){
        return rooms;
    }
    private RoomWrapper(){

    }
}
