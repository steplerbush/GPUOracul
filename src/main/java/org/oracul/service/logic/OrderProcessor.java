package org.oracul.service.logic;

/**
 * Created by Miramax on 27.12.2015.
 */
public class OrderProcessor {

    private Double fullWorkLoad;

    private Double currentWorkLoad;

    public Double addToWorkLoad(Double oneWorkLoad) {
        currentWorkLoad += oneWorkLoad;
        return currentWorkLoad;
    }

    public Double releaseWorkLoad(Double oneWorkLoad) {
        currentWorkLoad -= oneWorkLoad;
        return currentWorkLoad;
    }

    public Boolean canAddWorkLoad(Double oneWorkLoad) {
        if ((currentWorkLoad+oneWorkLoad) < fullWorkLoad)
            return true;
        return false;
    }


}
