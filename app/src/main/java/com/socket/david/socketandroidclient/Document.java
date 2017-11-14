package com.socket.david.socketandroidclient;

/**
 * Model Document Class
 *
 * This class represents an object of each document element returned from the server
 *
 * @author David Galvis
 */

public class Document {
    String id;
    String name;
    String price;

    public Document(String id, String name, String price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
