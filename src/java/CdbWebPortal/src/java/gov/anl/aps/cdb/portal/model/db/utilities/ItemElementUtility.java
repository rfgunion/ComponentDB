/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementHistory;
import gov.anl.aps.cdb.portal.view.objects.ItemHierarchyCache;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;

/**
 * DB utility class for item elements.
 */
public class ItemElementUtility {

    private final static String ITEM_ELEMENT_NODE_TYPE = "itemElement";

    private final static String SELECTION_MENU_MODEL_ONCLICK_TEMPLATE = "#{%s.%s(itemGenericViewController.findById(%s))}";
    private final static String CLEAR_ITEM_ONCLICK_TEMPLATE = "#{%s.%s(%s)}";
    private final static String ACTIVE_LOCATION_MENU_ITEM_STYLE = "activeLocationMenuItem";

    private static final Logger logger = LogManager.getLogger(ItemElementUtility.class.getName());

    public static TreeNode createItemElementRoot(Item parentItem) throws CdbException {
        return createTreeRoot(parentItem, false);
    }

    public static TreeNode createItemRoot(Item parentItem) throws CdbException {
        return createTreeRoot(parentItem, true);
    }

    private static TreeNode createTreeRoot(Item parentItem, boolean useChildItem) throws CdbException {
        Object newTreeNodeDataObject;
        String nodeType;
        if (useChildItem) {
            newTreeNodeDataObject = parentItem;
            nodeType = parentItem.getDomain().getName().replace(" ", "");
        } else {
            newTreeNodeDataObject = parentItem.getSelfElement();
            nodeType = ITEM_ELEMENT_NODE_TYPE;
        }

        TreeNode treeRoot = new DefaultTreeNode(nodeType, newTreeNodeDataObject, null);
        if (parentItem == null) {
            throw new CdbException("Cannot create item element tree view: parent item is not set.");
        }

        // Use "tree branch" list to prevent circular trees
        // Whenever new item is encountered, it will be added to the tree branch list before populating
        // element node, and removed from the branch list after population is done
        // If an object is encountered twice in the tree branch, this itemates an error.
        List<Item> itemTreeBranch = new ArrayList<>();
        populateItemNode(treeRoot, parentItem, itemTreeBranch, useChildItem);
        return treeRoot;
    }

    private static void populateItemNode(TreeNode itemElementNode, Item item, List<Item> itemTreeBranch, boolean useChildItem) throws InvalidObjectState {
        List<ItemElement> itemElementList = item.getItemElementDisplayList();
        if (itemElementList == null) {
            return;
        }
        itemTreeBranch.add(item);
        for (ItemElement itemElement : itemElementList) {
            Item childItem = itemElement.getContainedItem();

            TreeNode childItemElementNode = null;

            if (useChildItem) {
                if (childItem != null) {
                    String nodeType = childItem.getDomain().getName().replace(" ", "");;
                    childItemElementNode = new DefaultTreeNode(nodeType, childItem, itemElementNode);
                } else {
                    // no child item is linked to item element. 
                    continue;
                }
            }

            if (childItemElementNode == null) {
                childItemElementNode = new DefaultTreeNode(ITEM_ELEMENT_NODE_TYPE, itemElement, itemElementNode);
            }
            if (childItem != null) {
                if (itemTreeBranch.contains(childItem)) {
                    throw new InvalidObjectState("Cannot create item element tree view: circular child/parent relationship found with items "
                            + childItem.getName() + " and " + childItem.getName() + ".");
                }
                populateItemNode(childItemElementNode, childItem, itemTreeBranch, useChildItem);
            }

        }
        itemTreeBranch.remove(item);
    }

    public static List<ItemHierarchyCache> generateItemHierarchyCacheList(List<Item> itemList) {
        List<ItemHierarchyCache> itemHierarchyCaches = new ArrayList<>();
        for (Item item : itemList) {
            ItemHierarchyCache ihc = new ItemHierarchyCache(item);
            itemHierarchyCaches.add(ihc);
        }

        return itemHierarchyCaches;
    }

    public static void prepareItemElementHistory(List<ItemElement> originalItemElementList,
            List<ItemElement> newItemElementList, EntityInfo entityInfo) {
        for (ItemElement itemElementValue : newItemElementList) {
            int index = -1;
            if (originalItemElementList != null) {
                index = originalItemElementList.indexOf(itemElementValue);
            }
            
            if (index >= 0) {
                // Original element was there.
                ItemElement originalItemElement = originalItemElementList.get(index);
                prepareItemElementHistory(originalItemElement, itemElementValue, entityInfo);
            } else {
                prepareItemElementHistory(null, itemElementValue, entityInfo);
                
                // New item elements may be created hierarchically
                Item containedItem = itemElementValue.getContainedItem();
                if (containedItem != null) {
                    List<ItemElement> itemElementDisplayList = containedItem.getItemElementDisplayList();
                    if (itemElementDisplayList != null) {
                        prepareItemElementHistory(null, itemElementDisplayList, entityInfo);
                    }
                }
            }
        }
    }

    public static void prepareItemElementHistory(ItemElement originalItemElement, ItemElement itemElementValue, EntityInfo entityInfo) {
        if (originalItemElement != null) {
            if (!itemElementValue.equalsByIdContainedItemsAndParentItem(originalItemElement)) {
                // Property value was modified.
                logger.debug("Item element: " + itemElementValue + " was modified, storing history");

                // Save history
                List<ItemElementHistory> itemElementHistoryList = itemElementValue.getItemElementHistoryList();
                ItemElementHistory itemElementHistory = new ItemElementHistory(itemElementValue, entityInfo);
                itemElementHistoryList.add(itemElementHistory);
            }
        } else {
            // New Item Element
            logger.debug("Adding item element history for new item element record " + itemElementValue);
            ItemElementHistory itemElementHistory = new ItemElementHistory(itemElementValue, entityInfo);
            if (itemElementValue.getItemElementHistoryList() == null) {
                itemElementValue.setItemElementHistoryList(new ArrayList<>());
            }
            itemElementValue.getItemElementHistoryList().add(itemElementHistory);
        }
    }

    /**
     * Generates hierarchy of nodes in the form of MenuModel meant to be used as
     * a model in a tiered menu. Could be used in other menus.
     *
     * @param baseNodeName - String that will be displayed on the initial
     * submenu.
     * @param selectionController - [Null accepted] Controller to select item
     * @param selectionMethod - [Null accepted] Method in the controller for
     * selection controller to be called for menuitem command.
     * @param activeItemList - [Null accepted] If provided a location selected
     * style will be applied to the location that lead to the lowest location.
     * @return
     */
    public static DefaultMenuModel generateItemSelectionMenuModel(List<ItemHierarchyCache> firstLevelItemList,
            String baseNodeName,
            String selectionController,
            String selectionMethod,
            List<Item> activeItemList,
            String nullOption,
            String updateTarget,
            String processTarget) {
        DefaultMenuModel generatedMenuModel = new DefaultMenuModel();

        if (firstLevelItemList != null) {
            DefaultSubMenu defaultSubMenu = new DefaultSubMenu(baseNodeName);
            generatedMenuModel.addElement(defaultSubMenu);

            // Add null item 
            DefaultMenuItem nullMenuItem = new DefaultMenuItem();
            nullMenuItem.setValue(nullOption);
            String onClick = String.format(CLEAR_ITEM_ONCLICK_TEMPLATE, selectionController, selectionMethod, "null");
            nullMenuItem.setCommand(onClick);
            nullMenuItem.setUpdate(updateTarget);
            nullMenuItem.setProcess(processTarget);
            defaultSubMenu.getElements().add(nullMenuItem);

            generateItemSelectionMenuModel(defaultSubMenu, firstLevelItemList, selectionController, selectionMethod, activeItemList, updateTarget, processTarget);
        } else {
            DefaultMenuItem menuItem = new DefaultMenuItem(baseNodeName);
            menuItem.setDisabled(true);
            generatedMenuModel.addElement(menuItem);
        }

        return generatedMenuModel;
    }

    /**
     * Recursive method generates a menu model for locations based on given
     * location root tree node.
     *
     * @param submenu - SubMenu to which child tree nodes will be converted to.
     * @param locationTreeNode - Location TreeNode branch
     * @param setLocationController - [Null accepted] Controller to update item
     * location.
     * @param setLocationMethod - [Null accepted] Method in the location
     * controller to be called for menuitem command.
     * @param activeItemList - Apply location selected style to menu items in
     * the list.
     */
    private static void generateItemSelectionMenuModel(DefaultSubMenu submenu,
            List<ItemHierarchyCache> itemList,
            String setLocationController,
            String setLocationMethod,
            List<Item> activeItemList,
            String updateTarget,
            String processTarget) {

        for (ItemHierarchyCache itemCache : itemList) {
            Item item = itemCache.getParentItem();

            boolean applyLocationActiveStyle = false;
            if (activeItemList != null) {
                if (activeItemList.contains(item)) {
                    applyLocationActiveStyle = true;
                }
            }

            List<ItemHierarchyCache> itemChildren = itemCache.getChildrenItem();
            boolean createSubmenu = (itemChildren != null && itemChildren.size() > 0);

            if (createSubmenu) {
                DefaultSubMenu childSubmenu = new DefaultSubMenu(item.getName());
                if (applyLocationActiveStyle) {
                    childSubmenu.setStyleClass(ACTIVE_LOCATION_MENU_ITEM_STYLE);
                    if (activeItemList.indexOf(item) != activeItemList.size() - 1) {
                        // Still more items in hierarchy no need to highlight two reps of same item. 
                        applyLocationActiveStyle = false;
                    }
                }

                submenu.getElements().add(childSubmenu);
                addMenuItemToSubmenu(childSubmenu, item,
                        setLocationController, setLocationMethod,
                        applyLocationActiveStyle, updateTarget, processTarget);

                generateItemSelectionMenuModel(childSubmenu, itemChildren, setLocationController, setLocationMethod, activeItemList, updateTarget, processTarget);
            } else {
                addMenuItemToSubmenu(submenu, item,
                        setLocationController, setLocationMethod,
                        applyLocationActiveStyle, updateTarget, processTarget);
            }

        }
    }

    /**
     * Create a MenuItem for the location provided and insert into the SubMenu
     * provided. Apply additional necessary attributes based on the input
     * parameters.
     *
     * @param currentSubmenu - Submenu to add menu item to
     * @param item
     * @param setItemController - [Null accepted] Controller to update item
     * location.
     * @param setItemMethod - [Null accepted] Method in the location controller
     * to be called for menuitem command.
     * @param applyActiveLocationStyle - Apply location selected style to menu
     * items in the list.
     */
    private static void addMenuItemToSubmenu(DefaultSubMenu currentSubmenu,
            Item item,
            String setItemController,
            String setItemMethod,
            boolean applyActiveLocationStyle,
            String updateTarget,
            String processTarget) {
        DefaultMenuItem menuItem = new DefaultMenuItem();
        menuItem.setValue(item.getName());

        if (applyActiveLocationStyle) {
            menuItem.setStyleClass(ACTIVE_LOCATION_MENU_ITEM_STYLE);
        }

        if (setItemController != null) {
            String onClick = String.format(SELECTION_MENU_MODEL_ONCLICK_TEMPLATE, setItemController, setItemMethod, item.getId());
            menuItem.setCommand(onClick);
            menuItem.setUpdate(updateTarget);
            menuItem.setProcess(processTarget);
        }
        currentSubmenu.getElements().add(menuItem);
    }

}
