/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <p>
 * <gerviba@gerviba.hu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.       Szabó Gergely
 */
package hu.gerviba.authsch.struct;

/**
 * @author Gerviba
 */
public enum BMEUnitScope {
    BME           (true, false, false, false, false),
    BME_NEWBIE    (true, false,  false, true , false),
    BME_VIK       (true, true,  false, false, false),
    BME_ACTIVE    (true, false, true,  false, false),
    BME_VIK_ACTIVE(true, true,  true,  false, false),
    BME_VIK_NEWBIE(true, true,  false, true , false),
    BME_VBK       (true, false, false, false, true ),
    BME_VBK_ACTIVE(true, false, true,  false, true ),
    BME_VBK_NEWBIE(true, false, true,  true , true );

    private final boolean bme;
    private final boolean vik;
    private final boolean active;
    private final boolean newbie;
    private final boolean vbk;

    private BMEUnitScope(boolean bme, boolean vik, boolean active, boolean newbie, boolean vbk) {
        this.bme = bme;
        this.vik = vik;
        this.active = active;
        this.newbie = newbie;
        this.vbk = vbk;
    }

    public boolean isBme() {
        return bme;
    }

    public boolean isVik() {
        return vik;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isNewbie() {
        return newbie;
    }

    public boolean isVbk() {
        return vbk;
    }
}
