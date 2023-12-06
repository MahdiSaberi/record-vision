package com.data.service;

import com.data.model.Event;
import com.data.model.dto.EventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class EventService {

    @Value("${database.file.name}")
    private String dbFileName;
    private final String tempDir = System.getProperty("java.io.tmpdir");
    private final ObjectMapper mapper = new ObjectMapper();

    public boolean save(Event event) {
        try {
            event.setId(generateId());
            String eventJson = mapper.writeValueAsString(event);
            FileWriter fileWriter;
            fileWriter = new FileWriter(getDbDirectory().toFile(), true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(eventJson);
            writer.newLine();
            writer.close();
            fileWriter.close();
            return true;
        } catch (Exception exception) {
            return false;
        }

    }

    private int generateId() throws IOException {
        int id = 0;
        BufferedReader reader = new BufferedReader(new FileReader(getDbDirectory().toFile()));
        String json;
        List<Integer> idList = new ArrayList<>();
        idList.add(0);
        while ((json = reader.readLine()) != null) {
            try {
                EventDto event = readEventFromJson(json);
                idList.add(event.getId());
            } catch (Exception exception) {
                System.out.println("null");
            }
        }
        while (idList.contains(id)) {
            id++;
        }
        reader.close();
        return id;
    }

    private EventDto readEventFromJson(String json) {
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        json = json.replaceAll("\\r\\n|\\r|\\n", "");
        try {
            return mapper.readValue(json, EventDto.class);
        } catch (JsonProcessingException exception) {
            log.error(exception.getMessage());
        }
        return null;
    }

    public boolean removeById(Integer id) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(getDbDirectory().toFile()));
        File tempFile = new File(getTempDbDirectory());
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        String json;
        while ((json = reader.readLine()) != null) {
            EventDto event;
            try {
                event = readEventFromJson(json);
            } catch (Exception exception) {
                event = new EventDto();
                event.setId(-1);
                System.out.println("null");
            }
            if (!Objects.equals(event.getId(), id)) {
                writer.write(json + "\n");
            }
        }
        reader.close();
        writer.close();
        Files.deleteIfExists(getDbDirectory());
        return tempFile.renameTo(getDbDirectory().toFile());
    }

    public List<EventDto> getAll() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(getDbDirectory().toFile()));
        String json;
        List<EventDto> events = new ArrayList<>();
        while ((json = reader.readLine()) != null) {
            try {
                EventDto event = readEventFromJson(json);
                events.add(event);
            } catch (Exception exception) {
                System.out.println("null");
            }
        }
        reader.close();
        return events.stream().filter(Objects::nonNull).toList();
    }

    public Event getById(Integer id) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(getDbDirectory().toFile()));
        String json;
        while ((json = reader.readLine()) != null) {
            Event event = mapper.readValue(json, Event.class);
            if (event.getId() == id) {
                return event;
            }
        }
        reader.close();
        return null;
    }

    private Path getDbDirectory() {
        return Path.of(tempDir, dbFileName);
    }

    private String getTempDbDirectory() {
        Path path = Path.of(tempDir, "temp_" + dbFileName);
        return path.toString();
    }
}
