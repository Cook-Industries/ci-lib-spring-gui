package com.ci.lib.spring.web.hmi.mapper;

/**
 * Interface to make objects exportable to a {@link ValueMap}
 */
public interface ValueMapExportable
{

    /**
     * Export the fields contained in {@code object} into the {@code valueMap}
     * 
     * @param valueMap to put {@code object} fields to
     * @param object to extract fields from
     */
    public void exportTo(ValueMap valueMap);

}
