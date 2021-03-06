/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date property type handler.
 */
public class DatePropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "Date";
    private static final transient SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM/dd/yy");

    public static String formatDate(String value) {
        try {
            Date date = PropertyValue.InputDateFormat.parse(value);
            return outputDateFormat.format(date);
        } catch (ParseException | RuntimeException ex) {
            // ok, simply return null
        }
        return null;
    }
    
    public DatePropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.DATE);
    }


    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        String displayValue = formatDate(propertyValue.getValue());
        propertyValue.setDisplayValue(displayValue);
    }

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        String displayValue = formatDate(propertyValueHistory.getValue());
        propertyValueHistory.setDisplayValue(displayValue);
    }    
}
