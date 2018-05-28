package ch.model;

public class EncapsulatedParameters {
    private OverviewParameters overviewParameters;
    private WeeklyParameters weeklyParameters;


    public OverviewParameters getOverviewParameters() {
        return overviewParameters;
    }

    public void setOverviewParameters(OverviewParameters overviewParameters) {
        this.overviewParameters = overviewParameters;
    }

    public WeeklyParameters getWeeklyParameters() {
        return weeklyParameters;
    }

    public void setWeeklyParameters(WeeklyParameters weeklyParameters) {
        this.weeklyParameters = weeklyParameters;
    }
}
