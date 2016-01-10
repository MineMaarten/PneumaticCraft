package pneumaticCraft.api.universalSensor;

public interface ISensorRegistry{
    /**
     * Registry for IPollSensorSetting, EntityPollSensor and IEventSensorSetting, and any other instance of ISensorSetting.
     * @param sensor
     */
    public void registerSensor(ISensorSetting sensor);

    /**
     * Registry for IBlockAndCoordinateEventSensor
     * @param sensor
     */
    public void registerSensor(IBlockAndCoordinateEventSensor sensor);

    /**
     * Registry for IBlockAndCoordinatePollSensor
     * @param sensor
     */
    public void registerSensor(IBlockAndCoordinatePollSensor sensor);
}
