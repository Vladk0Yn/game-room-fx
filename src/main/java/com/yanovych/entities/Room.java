package com.yanovych.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private Long id;
    private String name;
    private Integer capacity;
    private Double budget;
    private Integer minimumChildAge;
    private Integer maximumChildAge;
    @EqualsAndHashCode.Exclude
    private List<Child> childrenInRoom;
    @EqualsAndHashCode.Exclude
    private List<Toy> toysInRoom;

    public Room(String name, Integer capacity, Double budget, Integer minimumChildAge, Integer maximumChildAge) {
        this.name = name;
        this.capacity = capacity;
        this.budget = budget;
        this.minimumChildAge = minimumChildAge;
        this.maximumChildAge = maximumChildAge;
    }

    @Override
    public String toString() {
        return name + " (min age: " + minimumChildAge + ", max age: " + maximumChildAge + ", budget: " + budget + ", capacity: " + capacity + ")";
    }
}
