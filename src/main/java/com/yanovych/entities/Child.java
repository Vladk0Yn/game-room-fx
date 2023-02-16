package com.yanovych.entities;

import com.yanovych.entities.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Child {

    private Long id;
    private String name;
    private Integer age;
    private Sex sex;
    private Long roomId;

    public Child(String name, Integer age, Sex sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }
}
