package it.skarafaz.mercury.model;

@SuppressWarnings("unused")
public class RegularCommand extends Command {
    private String cmd;
    private Boolean multiple;
    private String download;
    private Boolean view;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Boolean getMultiple() {
        return multiple;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public Boolean getView() {
        return view;
    }

    public void setView(Boolean view) {
        this.view = view;
    }

    @Override
    public String getInfo() {
        return getCmd();
    }

    @Override
    public String getProgressText() {
        return getMultiple() ? String.valueOf(getRunning()) : "";
    }
}
