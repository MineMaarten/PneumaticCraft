package igwmod.gui;

public interface IPageLink extends IClickable{

    /**
     * String that is being used by the search bar.
     * @return
     */
    public String getName();

    public String getLinkAddress();
}
