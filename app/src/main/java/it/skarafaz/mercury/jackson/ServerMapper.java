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
import it.skarafaz.mercury.exception.ValidationException;

public class ServerMapper {
    public static final String IS_MISSING = "is missing";
    public static final String DEFAULT_SERVER_LABEL = "Server";
    public static final String DEFAULT_COMMAND_LABEL_ = "Command";
    private ObjectMapper mapper;

    public ServerMapper() {
        mapper = new ObjectMapper();
    }

    public Server readValue(File src) throws IOException, ValidationException {
        Server server = mapper.readValue(src, Server.class);
        Map<String, String> anomalies = validateServer(server);
        if (anomalies.size() > 0) {
            throw new ValidationException(getValidationErrorMessage(src, anomalies));
        }
        return server;
    }

    private Map<String, String> validateServer(Server server) {
        Map<String, String> anomalies = new LinkedHashMap<>();
        if (StringUtils.isBlank(server.getName())) {
            server.setName(DEFAULT_SERVER_LABEL);
        }
        if (StringUtils.isBlank(server.getHost())) {
            anomalies.put("host", IS_MISSING);
        }
        if (server.getPort() == null) {
            server.setPort(22);
        }
        if (StringUtils.isBlank(server.getUser())) {
            anomalies.put("user", IS_MISSING);
        }
        if (StringUtils.isBlank(server.getPassword())) {
            anomalies.put("password", IS_MISSING);
        }
        if (server.getCommands() == null) {
            server.setCommands(new ArrayList<Command>());
        } else {
            for (int i = 0; i < server.getCommands().size(); i++) {
                Command command = server.getCommands().get(i);
                if (StringUtils.isBlank(command.getName())) {
                    command.setName(DEFAULT_COMMAND_LABEL_);
                }
                if (command.isSudo() == null) {
                    command.setSudo(Boolean.FALSE);
                }
                if (StringUtils.isBlank(command.getCmd())) {
                    anomalies.put(String.format("commands[%d].cmd", i), IS_MISSING);
                }
            }
        }
        return anomalies;
    }

    private String getValidationErrorMessage(File src, Map<String, String> anomalies) {
        StringBuilder sb = new StringBuilder(String.format("Failed to validate [%s]: ", src));
        int i = 1;
        for (Map.Entry<String, String> entry : anomalies.entrySet()) {
            sb.append(String.format("%s %s", entry.getKey(), entry.getValue()));
            if (i != anomalies.entrySet().size()) {
                sb.append(", ");
            }
            i++;
        }
        return sb.toString();
    }
}
