package com.meteordevelopments.duels.util.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.meteordevelopments.duels.util.Log;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public final class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER;
    private static final ObjectWriter OBJECT_WRITER;

    static {
        final JsonFactory factory = new JsonFactory();
        factory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

        OBJECT_MAPPER = JsonMapper.builder(factory).build();
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        OBJECT_WRITER = OBJECT_MAPPER.writer(buildDefaultPrettyPrinter());
    }

    private JsonUtil() {
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static ObjectWriter getObjectWriter() {
        return OBJECT_WRITER;
    }

    public static <T> void registerDeserializer(final Class<T> type, final Class<? extends DefaultBasedDeserializer<T>> deserializerClass) {
        final SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new BeanDeserializerModifier() {

            @Override
            public JsonDeserializer<?> modifyDeserializer(final DeserializationConfig config, final BeanDescription description, final JsonDeserializer<?> deserializer) {
                if (description.getBeanClass().equals(type)) {
                    try {
                        return deserializerClass.getConstructor(JsonDeserializer.class).newInstance(deserializer);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException ex) {
                        Log.error("Failed to instantiate JSON deserializer", ex);
                        return deserializer;
                    }
                }

                return deserializer;
            }
        });
        OBJECT_MAPPER.registerModule(module);
    }

    private static PrettyPrinter buildDefaultPrettyPrinter() {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter() {

            @Override
            public DefaultPrettyPrinter withSeparators(Separators separators) {
                _separators = separators;
                _objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSeparator() + " ";
                return this;
            }

            @NotNull
            @Override
            public DefaultPrettyPrinter createInstance() {
                return new DefaultPrettyPrinter(this);
            }
        };

        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        printer.indentObjectsWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        return printer;
    }
}
