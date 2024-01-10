package com.example.morpholivescan;

public class devInfo {

    private String assy_make;   /**< Assembly Make (ex: "IDX")					*/
    private String assy_model;  /**< Assembly Model (ex: "TP-4100")			*/
    private String assy_sn;     /**< Assembly Serial Number (ex: "123456")		*/
    private String sw_version;  /**< Embedded Firmware Application Version		*/

    public devInfo() {
        this.assy_make = "";
        this.assy_model = "";
        this.assy_sn = "";
        this.sw_version = "";
    }

    public devInfo(String assy_make, String assy_model, String assy_sn, String sw_version) {
        this.assy_make = assy_make;
        this.assy_model = assy_model;
        this.assy_sn = assy_sn;
        this.sw_version = sw_version;
    }

    public String getAssy_make() {
        return assy_make;
    }

    public String getAssy_model() {
        return assy_model;
    }

    public String getAssy_sn() {
        return assy_sn;
    }

    public String getSw_version() {
        return sw_version;
    }

    public void setAssy_make(String assy_make) {
        this.assy_make = assy_make;
    }

    public void setAssy_model(String assy_model) {
        this.assy_model = assy_model;
    }

    public void setAssy_sn(String assy_sn) {
        this.assy_sn = assy_sn;
    }

    public void setSw_version(String sw_version) {
        this.sw_version = sw_version;
    }

    @Override
    public String toString() {
        return "devInfo{" +
                "assy_make='" + assy_make + '\'' +
                ", assy_model='" + assy_model + '\'' +
                ", assy_sn='" + assy_sn + '\'' +
                ", sw_version='" + sw_version + '\'' +
                '}';
    }
}
