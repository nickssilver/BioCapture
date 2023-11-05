package com.idemia.morpholivescan;

public class tpDevList {

    private String devName;	/**< Device name								*/
    private String assy_model;	/**< Assembly Model (ex: “TP-4100”)			*/
    private String assy_sn;	/**< Assembly Serial Number (ex: “123456”)		*/
    private int libType;			/**< Link library type							*/

    private int handel;

    public tpDevList(String devName, String assy_model, String assy_sn, int libType)
    {
        this.devName = devName;
        this.assy_model = assy_model;
        this.assy_sn = assy_sn;
        this.libType = libType;

        this.handel=0;
    }

    public String getDevName() {
        return devName;
    }

    public String getAssy_model() {
        return assy_model;
    }

    public String getAssy_sn() {
        return assy_sn;
    }

    public int getLibType() {
        return libType;
    }

    public int getHandel() {
        return handel;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public void setAssy_model(String assy_model) {
        this.assy_model = assy_model;
    }

    public void setAssy_sn(String assy_sn) {
        this.assy_sn = assy_sn;
    }

    public void setLibType(int libType) {
        this.libType = libType;
    }

    public void setHandel(int handel) {
        this.handel = handel;
    }

    @Override
    public String toString() {
        return "tpDevList{" +
                "devName='" + devName + '\'' +
                ", assy_model='" + assy_model + '\'' +
                ", assy_sn='" + assy_sn + '\'' +
                ", libType=" + libType +
                ", handel=" + handel +
                '}';
    }
}
