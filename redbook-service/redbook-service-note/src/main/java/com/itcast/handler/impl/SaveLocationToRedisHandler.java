package com.itcast.handler.impl;

import com.itcast.constant.RedisConstant;
import com.itcast.handler.NoteHandler;
import com.itcast.note.dto.NoteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SaveLocationToRedisHandler extends NoteHandler {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void handle(NoteDto noteDto) throws IOException, InterruptedException {
        RedisGeoCommands.GeoLocation<Object> geoLocation
                = new RedisGeoCommands.GeoLocation<>(noteDto.getId(), new Point(noteDto.getLongitude(), noteDto.getLatitude()));
        redisTemplate.opsForGeo().add(RedisConstant.NOTE_GEO, geoLocation);

        if(nextHandler != null) {
            nextHandler.handle(noteDto);
        }
    }
}
