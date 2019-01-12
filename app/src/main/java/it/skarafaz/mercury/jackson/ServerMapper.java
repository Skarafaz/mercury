/*
 * Mercury-SSH
 * Copyright (C) 2019 Skarafaz
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

import com.fasterxml.jackson.databind.ObjectMapper;
import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.model.config.Command;
import it.skarafaz.mercury.model.config.Server;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
        if (StringUtils.isEmpty(server.getPassword())) {
            server.setPassword(null);
        }
        if (server.getSudoNoPasswd() == null) {
            server.setSudoNoPasswd(false);
        }
        if (StringUtils.isBlank(server.getSudoPath())) {
            server.setSudoPath("sudo");
        }
        if (StringUtils.isBlank(server.getNohupPath())) {
            server.setNohupPath("nohup");
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
        if (command.getConfirm() == null) {
            command.setConfirm(Boolean.FALSE);
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
}
