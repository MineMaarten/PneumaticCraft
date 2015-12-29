package igwmod.gui;

public interface IClickable extends IReservedSpace, IWidget{
    public boolean onMouseClick(GuiWiki gui, int x, int y);
}
