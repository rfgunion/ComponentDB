/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperSource extends ImportHelperBase<Source, SourceController> {


    protected static String completionUrlValue = "/views/source/list?faces-redirect=true";
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new StringColumnSpec(0, "Name", "name", "setName", true, "Name of vendor/manufacturer", 64));
        specs.add(new StringColumnSpec(1, "Description", "description", "setDescription", false, "Description of vendor/manufacturer", 256));
        specs.add(new StringColumnSpec(2, "Contact Info", "contactInfo", "setContactInfo", false, "Contact name and phone number etc", 64));
        specs.add(new StringColumnSpec(3, "URL", "url", "setUrl", false, "URL for vendor/manufacturer", 256));
        
        return specs;
    } 
   
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public SourceController getEntityController() {
        return SourceController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Source Template";
    }

    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        Source entity = getEntityController().createEntityInstance();
        return new CreateInfo(entity, true, "");
    }  

    protected boolean ignoreDuplicates() {
        return true;
    }
}
