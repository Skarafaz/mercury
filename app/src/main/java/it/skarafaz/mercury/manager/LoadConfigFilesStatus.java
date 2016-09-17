package it.skarafaz.mercury.manager;

import it.skarafaz.mercury.R;

public enum LoadConfigFilesStatus {
    SUCCESS(R.string.empty_config_dir),
    ERROR(R.string.load_config_files_error),
    CANNOT_READ_EXT_STORAGE(R.string.cannot_read_ext_storage),
    CANNOT_CREATE_CONFIG_DIR(R.string.cannot_create_config_dir),
    PERMISSION(R.string.load_config_files_permission);

    private int message;

    LoadConfigFilesStatus(int message) {
        this.message = message;
    }

    public int message() {
        return message;
    }
}
