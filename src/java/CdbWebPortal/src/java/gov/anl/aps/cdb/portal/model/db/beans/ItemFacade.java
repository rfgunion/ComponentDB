/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */

@Stateless
public class ItemFacade extends ItemFacadeBase<Item> {
    
    @Override
    public String getDomainName() {
        return "";
    }
    
    public ItemFacade() {
        super(Item.class);
    }
    
    public static ItemFacade getInstance() {
        return (ItemFacade) SessionUtility.findFacade(ItemFacade.class.getSimpleName()); 
    }
    
}
