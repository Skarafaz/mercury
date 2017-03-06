/*
 * Mercury-SSH
 * Copyright (C) 2017 Skarafaz
 *
 * This file is part of Mercury-SSH.
 *
 * Mercury-SSH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Mercury-SSH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mercury-SSH.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.skarafaz.mercury.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.manager.ConfigManager;
import it.skarafaz.mercury.model.Command;
import it.skarafaz.mercury.model.Entry;
import it.skarafaz.mercury.model.RegularCommand;
import it.skarafaz.mercury.model.Ruler;
import it.skarafaz.mercury.model.Server;
import it.skarafaz.mercury.model.ServerAuthType;

public class ServerMapper {
    private static final Logger logger = LoggerFactory.getLogger(ServerMapper.class);
    private ObjectMapper mapper;

    public ServerMapper() {
        PolymorphicDeserializer deserializer = new PolymorphicDeserializer(Entry.class, RegularCommand.class, Ruler.class);
        mapper = new ObjectMapper();
        mapper.registerModule((new SimpleModule()).addDeserializer(Entry.class, deserializer));
    }

    public Server readValue(File src) throws IOException, ValidationException {
        Server server = mapper.readValue(src, Server.class);
        Map<String, String> errors = validateServer(server);
        if (errors.size() > 0) {
            throw new ValidationException(getValidationErrorMessage(src, errors));
        }
        return server;
    }

    private Map<String, String> validateServer(Server server) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (StringUtils.isBlank(server.getName())) {
            server.setName(getString(R.string.server));
        }
        if (StringUtils.isBlank(server.getHost()) && StringUtils.isBlank(server.getMDnsName())) {
            errors.put("host/mdnsname", getString(R.string.validation_missing));
        }
        if (server.getPort() == null) {
            server.setPort(22);
        } else if (server.getPort() < 1 || server.getPort() > 65535) {
            errors.put("port", getString(R.string.validation_invalid));
        }
        if (server.getMDnsType() == null) {
            server.setMDnsType("_ssh._tcp");
        }
        if (StringUtils.isBlank(server.getUser())) {
            errors.put("user", getString(R.string.validation_missing));
        }
        if (StringUtils.isEmpty(server.getPassword())) {
            server.setPassword(null);
        }
        if (StringUtils.isEmpty(server.getAuthType())) {
            server.setAuthType(ServerAuthType.RSA2048.toString());
        }
        try {
            ServerAuthType.valueOf(ServerAuthType.appendDefaultLength(server.getAuthType()));
        } catch (IllegalArgumentException e) {
            errors.put("authtype", getString(R.string.validation_invalid));
        }
        if (StringUtils.isBlank(server.getSudoPath())) {
            server.setSudoPath("sudo");
        }
        if (StringUtils.isBlank(server.getNohupPath())) {
            server.setNohupPath("nohup");
        }
        if (server.getEntries() == null) {
            server.setEntries(new ArrayList<Entry>());
        } else {
            for (int i = 0; i < server.getEntries().size(); i++) {
                errors.putAll(validateEntry(server, server.getEntries().get(i), i));
            }
        }
        return errors;
    }

    private Map<String, String> validateEntry(Server server, Entry entry, int index) {
        Map<String, String> errors = new LinkedHashMap<>();
        entry.setServer(server);
        if (entry.getIcon() != null) {
            entry.setIcon(new File(ConfigManager.getInstance().getConfigDir(), entry.getIcon())
                    .getAbsolutePath());
        }
        if (StringUtils.isBlank(entry.getName())) {
            entry.setName(getString(R.string.command));
        }
        if (entry instanceof Command) {
            errors.putAll(validateCommand((Command) entry, index));
        }
        return errors;
    }

    private Map<String, String> validateCommand(Command command, int index) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (command.getSudo() == null) {
            command.setSudo(Boolean.FALSE);
        }
        if (command.getConfirm() == null) {
            command.setConfirm(Boolean.FALSE);
        }
        if (command.getWait() == null) {
            command.setWait(Boolean.FALSE);
        }
        if (command.getBackground() == null) {
            command.setBackground(Boolean.FALSE);
        }
        if (command.getSilent() == null) {
            command.setSilent(Boolean.FALSE);
        }
        if (command instanceof RegularCommand) {
            errors.putAll(validateRegularCommand((RegularCommand) command, index));
        } else if (command instanceof Ruler) {
            errors.putAll(validateRuler((Ruler) command, index));
        }
        return errors;
    }

    private Map<String, String> validateRuler(Ruler ruler, int index) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (StringUtils.isBlank(ruler.getGetCmd())) {
            errors.put(String.format("commands[%d].getcmd", index), getString(R.string.validation_missing));
        }
        if (StringUtils.isBlank(ruler.getSetCmd())) {
            errors.put(String.format("commands[%d].setcmd", index), getString(R.string.validation_missing));
        }
        if (ruler.getMin() == null) {
            ruler.setMin(0);
        }
        if (ruler.getMax() == null) {
            ruler.setMax(100);
        }
        if (ruler.getStep() == null) {
            ruler.setStep(1);
        }
        return errors;
    }

    private Map<String, String> validateRegularCommand(RegularCommand command, int index) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (StringUtils.isBlank(command.getCmd())) {
            errors.put(String.format("commands[%d].cmd", index), getString(R.string.validation_missing));
        }
        if (command.getMultiple() == null) {
            command.setMultiple(Boolean.FALSE);
        }
        if (command.getView() == null) {
            command.setView(Boolean.FALSE);
        }
        return errors;
    }

    private String getValidationErrorMessage(File src, Map<String, String> errors) {
        StringBuilder sb = new StringBuilder(getString(R.string.validation_file, src));
        int i = 1;
        for (Map.Entry<String, String> entry : errors.entrySet()) {
            sb.append(String.format(" %s %s", entry.getKey(), entry.getValue()));
            if (i != errors.entrySet().size()) {
                sb.append(", ");
            }
            i++;
        }
        return sb.toString();
    }

    private String getString(int resId) {
        return MercuryApplication.getContext().getString(resId);
    }

    private String getString(int resId, Object... formatArgs) {
        return MercuryApplication.getContext().getString(resId, formatArgs);
    }

    private class PolymorphicDeserializer<T> extends StdDeserializer<T> {
        private Map<Class<? extends T>, Collection<String>> registry;

        public PolymorphicDeserializer(Class<T> baseClass, Class<? extends T>... args) {
            super((Class<T>) baseClass.getClass());
            registry = new HashMap<>();
            for (Class<? extends T> arg : args) {
                Collection<String> fields = new HashSet<>();
                for (Class cls = arg; cls != null; cls = cls.getSuperclass()) {
                    for (Field field : cls.getDeclaredFields()) {
                        String name = field.getName();
                        for (Annotation annotation : field.getDeclaredAnnotations()) {
                            if (annotation instanceof JsonProperty) {
                                name = ((JsonProperty) annotation).value();
                                break;
                            }
                        }
                        fields.add(name);
                    }
                }
                logger.debug(String.format("Registering %s as extension of %s with fields %s", arg.getName(), baseClass
                        .getName(), Arrays.toString(fields.toArray())));
                registry.put(arg, fields);
            }
        }

        @Override
        public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            ObjectNode root = (ObjectNode) mapper.readTree(jp);
            for (Map.Entry<Class<? extends T>, Collection<String>> entry: registry.entrySet()) {
                boolean valid = true;
                for (Iterator<Map.Entry<String, JsonNode>> it = root.fields(); it.hasNext(); ) {
                    Map.Entry<String, JsonNode> element = it.next();
                    if (!entry.getValue().contains(element.getKey())) {
                        logger.trace(String.format("deserialize for %s: class %s misses property %s", root, entry
                                .getKey().getName(), element.getKey()));
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    logger.debug(String.format("deserialize for %s: use class %s", root, entry.getKey().getName()));
                    return mapper.treeToValue(root, (Class<T>)entry.getKey());
                }
            }
            return null;
        }
    }
}
