package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public class FileStorage {

    private final ObjectMapper objectMapper;

    public FileStorage() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void save(LabData data, String filePath) throws IOException {
        File file = new File(filePath);
        objectMapper.writeValue(file, data);
    }

    public LabData load(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Файл не найден: " + filePath);
        }
        if (!file.canRead()) {
            throw new IOException("Нет прав на чтение файла: " + filePath);
        }
        return objectMapper.readValue(file, LabData.class);
    }
}
