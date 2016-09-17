package it.skarafaz.mercury.manager;

import it.skarafaz.mercury.R;

public enum ExportPublicKeyStatus {
    SUCCESS(R.string.export_public_key_success),
    ERROR(R.string.export_public_key_error),
    PERMISSIONS(R.string.export_public_key_permissions),
    CANNOT_WRITE(R.string.cannot_write_ext_storage);

    private int message;

    ExportPublicKeyStatus(int message) {
        this.message = message;
    }

    public int message() {
        return message;
    }
}
