/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.PropertyMetadataController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;

/**
 *
 * @author djarosz
 */
public class PropertyMetadataSettings extends CdbEntitySettingsBase<PropertyMetadataController> {
    
    public PropertyMetadataSettings(PropertyMetadataController pmc) {
        super(pmc);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
