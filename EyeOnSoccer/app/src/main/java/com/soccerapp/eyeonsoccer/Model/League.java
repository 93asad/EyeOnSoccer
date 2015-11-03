package com.soccerapp.eyeonsoccer.Model;

/**
 * Created by Asad on 18/10/2015. Represents league object
 */
public class League {
    private int mLogoId; //Id of logo
    private String mName;

    /**Get logo id
     *
     * @return
     */
    public int getLogoId() {
        return mLogoId;
    }

    /**Set logo id
     *
     * @param logoId
     */
    public void setLogoId(int logoId) {
        this.mLogoId = logoId;
    }

    /**Get name
     *
     * @return
     */
    public String getName() {
        return mName;
    }

    /**Set name
     *
     * @param name
     */
    public void setName(String name) {
        this.mName = name;
    }
}
