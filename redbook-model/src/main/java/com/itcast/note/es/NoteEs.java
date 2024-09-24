package com.itcast.note.es;

import lombok.Data;

@Data
public class NoteEs {
    private Integer id;
    private String title;
    private String content;
    private String image;
    private String time;
    private String address;
    private Double longitude;
    private Double latitude;
    private Integer userId;
}
