package it.skarafaz.mercury.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.model.Command;
import it.skarafaz.mercury.model.Server;

public class ServerMapper {
    private ObjectMapper mapper;

    public ServerMapper() {
        mapper = new ObjectMapper();
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
        if (StringUtils.isBlank(server.getHost())) {
            errors.put("host", getString(R.string.validation_missing));
        }
        if (server.getPort() == null) {
            server.setPort(22);
        } else if (server.getPort() < 1 || server.getPort() > 65535) {
            errors.put("port", getString(R.string.validation_invalid));
        }
        if (StringUtils.isBlank(server.getUser())) {
            errors.put("user", getString(R.string.validation_missing));
        }
        if (StringUtils.isBlank(server.getPassword())) {
            errors.put("password", getString(R.string.validation_missing));
        }
        if (server.getCommands() == null) {
            server.setCommands(new ArrayList<Command>());
        } else {
            for (int i = 0; i < server.getCommands().size(); i++) {
                errors.putAll(validateCommand(server.getCommands().get(i), i));
            }
        }
        return errors;
    }

    private Map<String, String> validateCommand(Command command, int index) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (StringUtils.isBlank(command.getName())) {
            command.setName(getString(R.string.command));
        }
        if (command.getSudo() == null) {
            command.setSudo(Boolean.FALSE);
        }
        if (StringUtils.isBlank(command.getCmd())) {
            errors.put(String.format("commands[%d].cmd", index), getString(R.string.validation_missing));
        }
        return errors;
    }

    private String getValidationErrorMessage(File src, Map<String, String> errors) {
        StringBuilder sb = new StringBuilder(getString(R.string.validation_file, src));
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

    private String getString(int resId) {
        return MercuryApplication.getContext().getString(resId);
    }

    private String getString(int resId, Object... formatArgs) {
        return MercuryApplication.getContext().getString(resId, formatArgs);
    }
}
