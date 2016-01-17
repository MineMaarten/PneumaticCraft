package pneumaticCraft.common.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import pneumaticCraft.api.item.IInventoryItem;
import pneumaticCraft.api.item.IItemRegistry;
import pneumaticCraft.api.item.IUpgradeAcceptor;

public class ItemRegistry implements IItemRegistry{

    private static ItemRegistry INSTANCE = new ItemRegistry();
    public final List<IInventoryItem> inventoryItems = new ArrayList<IInventoryItem>();
    private final Map<Item, List<IUpgradeAcceptor>> upgradeToAcceptors = new HashMap<Item, List<IUpgradeAcceptor>>();

    public static ItemRegistry getInstance(){
        return INSTANCE;
    }

    @Override
    public void registerInventoryItem(IInventoryItem handler){
        if(handler == null) throw new NullPointerException("IInventoryItem is null!");
        inventoryItems.add(handler);
    }

    @Override
    public Item getUpgrade(EnumUpgrade type){
        return Itemss.upgrades.get(type);
    }

    @Override
    public void registerUpgradeAcceptor(IUpgradeAcceptor upgradeAcceptor){
        if(upgradeAcceptor == null) throw new NullPointerException("Upgrade acceptor is null!");
        Set<Item> applicableUpgrades = upgradeAcceptor.getApplicableUpgrades();
        if(applicableUpgrades != null) {
            for(Item applicableUpgrade : applicableUpgrades) {
                List<IUpgradeAcceptor> acceptors = upgradeToAcceptors.get(applicableUpgrade);
                if(acceptors == null) {
                    acceptors = new ArrayList<IUpgradeAcceptor>();
                    upgradeToAcceptors.put(applicableUpgrade, acceptors);
                }
                acceptors.add(upgradeAcceptor);
            }
        }
    }

    @Override
    public void addTooltip(Item upgrade, List<String> tooltip){
        List<IUpgradeAcceptor> acceptors = upgradeToAcceptors.get(upgrade);
        if(acceptors != null) {
            List<String> tempList = new ArrayList<String>(acceptors.size());
            for(IUpgradeAcceptor acceptor : acceptors) {
                tempList.add("-" + StatCollector.translateToLocal(acceptor.getName()));
            }
            Collections.sort(tempList);
            tooltip.addAll(tempList);
        }
    }

}
