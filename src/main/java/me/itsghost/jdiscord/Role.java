package me.itsghost.jdiscord;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Role {
    private final String name;
    private final String id;
    private final String color;
    private final Map<String, Integer> role;

    public Role(String name, String id, String color){
        this.name = name;
        this.id = id;
        this.color = color;
        this.role = new HashMap<>();
    }
    public Role(String name, String id, String color,  Map<String, Integer> role){
        this.name = name;
        this.id = id;
        this.color = color;
        this.role = role;
    }

    public String toString(){
        return "name: " + name + " | id: " + id  + " | color: " + color + " | role meta (map): " + role;
    }
}