package com.songoda.epichoppers.containers.impl;

import com.songoda.epichoppers.containers.CustomContainer;
import com.songoda.epichoppers.containers.IContainer;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.stackable.Stackable;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class FabledSkyBlockImpl implements IContainer {
    @Override
    public CustomContainer getCustomContainer(Block block) {
        return new Container(block);
    }

    static class Container extends CustomContainer {
        private final Stackable stackable;

        public Container(Block block) {
            super();

            StackableManager stackableManager = SkyBlock.getInstance().getStackableManager();
            XMaterial xMaterial = XMaterial.matchXMaterial(block.getType());

            this.stackable = stackableManager.getStack(block.getLocation(), xMaterial);
        }

        @Override
        public boolean addToContainer(ItemStack itemToMove) {
            if (XMaterial.matchXMaterial(itemToMove) != this.stackable.getMaterial()) {
                return false;
            }

            this.stackable.addOne();
            if (this.stackable.getMaxSize() > 0 && this.stackable.isMaxSize()) {
                this.stackable.setSize(this.stackable.getMaxSize());
                return false;
            }

            return true;
        }

        @Override
        public ItemStack[] getItems() {
            return new ItemStack[]{new ItemStack(this.stackable.getMaterial().parseMaterial(), this.stackable.getSize())};
        }

        @Override
        public void removeFromContainer(ItemStack itemToMove, int amountToMove) {
            if (XMaterial.matchXMaterial(itemToMove) != this.stackable.getMaterial()) {
                return;
            }

            this.stackable.setSize(this.stackable.getSize() - amountToMove);
        }

        @Override
        public boolean isContainer() {
            return this.stackable != null;
        }
    }
}
