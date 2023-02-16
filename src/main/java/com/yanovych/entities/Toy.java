package com.yanovych.entities;

import com.yanovych.entities.enums.Color;
import com.yanovych.entities.enums.ToyMaterial;
import com.yanovych.entities.enums.ToySize;
import com.yanovych.entities.enums.ToyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Toy {
    private Long id;
    private String name;
    private Integer minimumAge;
    private Double price;
    private Long toyRoomId;
    private ToyType type;
    private ToySize size;
    private Color color;
    private ToyMaterial material;

    public Toy(String name, Integer minimumAge, Double price, ToyType type, ToySize size, Color color, ToyMaterial material) {
        this.name = name;
        this.minimumAge = minimumAge;
        this.price = price;
        this.type = type;
        this.size = size;
        this.color = color;
        this.material = material;
    }
}
