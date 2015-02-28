package models;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class VoteSerializer extends JsonSerializer<Vote> {
    @Override
    public void serialize(Vote value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeNumberField("category", value.category.id);
        jgen.writeNumberField("score", value.score);
        jgen.writeEndObject();
    }
}
