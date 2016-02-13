package it.skarafaz.mercury.enums;

import it.skarafaz.mercury.R;

public enum LoadConfigExitStatus {
    SUCCESS(R.string.empty_config_dir),
    ERRORS_FOUND(R.string.errors_found),
    CANNOT_READ_EXT_STORAGE(R.string.cannot_read_ext_storage),
    CANNOT_CREATE_CONFIG_DIR(R.string.cannot_create_config_dir),
    PERMISSION(R.string.permission);

    private int msg;

    LoadConfigExitStatus(int msg) {
        this.msg = msg;
    }

    public int msg() {
        return msg;
    }
}
