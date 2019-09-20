package com.khadn.donotdisturb;

public class Setting {


    public String get_KEY() {
        return _KEY;
    }

    public void set_KEY(String _KEY) {
        this._KEY = _KEY;
    }

    public String get_VALUE() {
        return _VALUE;
    }

    public void set_VALUE(String _VALUE) {
        this._VALUE = _VALUE;
    }

    public Setting(String _KEY, String _VALUE) {
        this._KEY = _KEY;
        this._VALUE = _VALUE;
    }

    private String _KEY;
    private String _VALUE;
}
