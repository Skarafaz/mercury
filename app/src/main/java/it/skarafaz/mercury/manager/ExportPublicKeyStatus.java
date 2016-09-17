package it.skarafaz.mercury.manager;

import it.skarafaz.mercury.R;

public enum ExportPublicKeyStatus {
    SUCCESS(R.string.export_public_key_success),
    ERROR(R.string.export_public_key_error),
    CANNOT_WRITE_EXT_STORAGE(R.string.cannot_write_ext_storage),
    PERMISSION(R.string.export_public_key_permission);

    private int message;

    ExportPublicKeyStatus(int message) {
        this.message = message;
    }

    public int message() {
        return message;
    }
}
