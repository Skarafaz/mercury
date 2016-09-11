package it.skarafaz.mercury.manager;

import it.skarafaz.mercury.R;

public enum ConfigStatus {
    SUCCESS(R.string.empty_config_dir),
    ERRORS_FOUND(R.string.errors_found),
    CANNOT_READ_EXT_STORAGE(R.string.cannot_read_ext_storage),
    CANNOT_CREATE_CONFIG_DIR(R.string.cannot_create_config_dir),
    PERMISSION(R.string.permission);

    private int message;

    ConfigStatus(int message) {
        this.message = message;
    }

    public int message() {
        return message;
    }
}
