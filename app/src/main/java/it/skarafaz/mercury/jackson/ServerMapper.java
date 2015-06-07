package it.skarafaz.mercury.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import it.skarafaz.mercury.data.Command;
import it.skarafaz.mercury.data.Server;

public class ServerMapper {
    public static final String IS_MISSING = "is missing";
    public static final String DEFAULT_SERVER_LABEL = "Server";
    public static final String DEFAULT_COMMAND_LABEL = "Command";
    public static final int DEFAULT_PORT = 22;
    private ObjectMapper mapper;

    public ServerMapper() {
        mapper = new ObjectMapper();
    }

    public Server readValue(File src) throws IOException, ValidationException {
        Server server = mapper.readValue(src, Server.class);
        Map<String, String> validationErrors = validateServer(server);
        if (validationErrors.size() > 0) {
            throw new ValidationException(getValidationErrorMessage(src, validationErrors));
        }
        return server;
    }

    private Map<String, String> validateServer(Server server) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (StringUtils.isBlank(server.getName())) {
            server.setName(DEFAULT_SERVER_LABEL);
        }
        if (StringUtils.isBlank(server.getHost())) {
            errors.put("host", IS_MISSING);
        }
        if (server.getPort() == null) {
            server.setPort(DEFAULT_PORT);
        }
        if (StringUtils.isBlank(server.getUser())) {
            errors.put("user", IS_MISSING);
        }
        if (StringUtils.isBlank(server.getPassword())) {
            errors.put("password", IS_MISSING);
        }
        if (server.getCommands() == null) {
            server.setCommands(new ArrayList<Command>());
        } else {
            for (int i = 0; i < server.getCommands().size(); i++) {
                Command command = server.getCommands().get(i);
                if (StringUtils.isBlank(command.getName())) {
                    command.setName(DEFAULT_COMMAND_LABEL);
                }
                if (command.getSudo() == null) {
                    command.setSudo(Boolean.FALSE);
                }
                if (StringUtils.isBlank(command.getCmd())) {
                    errors.put(String.format("commands[%d].cmd", i), IS_MISSING);
                }
            }
        }
        return errors;
    }

    private String getValidationErrorMessage(File src, Map<String, String> errors) {
        StringBuilder sb = new StringBuilder(String.format("Failed to validate [%s]: ", src));
        int i = 1;
        for (Map.Entry<String, String> entry : errors.entrySet()) {
            sb.append(String.format("%s %s", entry.getKey(), entry.getValue()));
            if (i != errors.entrySet().size()) {
                sb.append(", ");
            }
            i++;
        }
        return sb.toString();
    }
}
