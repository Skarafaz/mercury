package it.skarafaz.mercury.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class Ruler extends Command {
    @JsonProperty("getcmd")
    private String getCmd;
    @JsonProperty("setcmd")
    private String setCmd;
    private Integer min;
    private Integer max;
    private Integer step;

    @JsonIgnore
    private Integer value;

    public String getGetCmd() {
        return getCmd;
    }

    public void setGetCmd(String getCmd) {
        this.getCmd = getCmd;
    }

    public String getSetCmd() {
        return setCmd;
    }

    public void setSetCmd(String setCmd) {
        this.setCmd = setCmd;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getInfo() {
        return "GET: " + getGetCmd() + "\nSET: " + getSetCmd();
    }

    @Override
    public String getProgressText() {
        return "";
    }
}
