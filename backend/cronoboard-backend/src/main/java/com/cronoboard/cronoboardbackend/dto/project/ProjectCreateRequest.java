package com.cronoboard.cronoboardbackend.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ProjectCreateRequest {

    @NotBlank
    private String name;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "colorHex must be like #RRGGBB")
    private String colorHex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }
}


